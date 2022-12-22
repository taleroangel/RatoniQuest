package com.taleroangel.ratoniquest.engine

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.taleroangel.ratoniquest.R
import com.taleroangel.ratoniquest.render.events.Event
import com.taleroangel.ratoniquest.render.events.EventDialog
import com.taleroangel.ratoniquest.render.ui.Button
import com.taleroangel.ratoniquest.render.ui.Joystick
import com.taleroangel.ratoniquest.render.game.Mascot
import com.taleroangel.ratoniquest.render.game.Player
import com.taleroangel.ratoniquest.render.sprites.SpriteSheet
import com.taleroangel.ratoniquest.tools.GeometricTools
import kotlin.properties.Delegates

class GameEngine(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    /* Game engine variables */
    var isInitialized = false
        private set

    /* Initialize the subsystems */
    private var gameLoop = GameLoopController(this, holder)
    private var physicsEngine = PhysicsEngine()

    /* Game components */
    private lateinit var joystick: Joystick
    private lateinit var kissButton: Button

    /* Canvas constraints */
    var maxHeight: Int by Delegates.notNull()
    var maxWidth: Int by Delegates.notNull()

    /* Player */
    private val player = object : Player(
        "player::main",
        SpriteSheet(context, R.drawable.sprite_debugsheet_0, 80F),
        areaRadius = 80.0F,
        position = GeometricTools.Position(500F, 500F),
        velocity = 10.0F,
    ) {
        var dialog: EventDialog? = null

        /* Overridden member functions */

        override fun draw(canvas: Canvas) {
            super.draw(canvas)
            dialog?.draw(canvas, position)
        }

        override fun update() {
            // Move according to joystick
            if (joystick.pressed) {
                val components = joystick.calculateDirection()
                position.x += velocity * components.first
                position.y += velocity * components.second
                direction = GeometricTools.angleToDirection(components.third)
            } else {
                direction = GeometricTools.Direction.NONE
            }

            // Check position max values
            if (position.x > maxWidth) {
                position.x = maxWidth.toFloat()
            } else if (position.x < 0) {
                position.x = 0F
            }

            if (position.y > maxHeight) {
                position.y = maxHeight.toFloat()
            } else if (position.y < 0F) {
                position.y = 0F
            }
        }

        override fun consume(event: Event) {
            when (event.generatorClass) {
                // Kiss button
                Button::class -> if (event.generatorTag == "button::kiss") {
                    dialog = EventDialog(
                        uptime = 2500,
                        offset = GeometricTools.Position(areaRadius + 20F, -areaRadius - 20F),
                        foregroundPositionOffset = GeometricTools.Position(5F, -25F),
                        context = context,
                        backgroundResource = R.drawable.progressive_dialog,
                        foregroundResource = R.drawable.asset_kissbutton,
                        85F
                    )
                }
            }
        }
    }

    /* Mascot */
    private val mascot = object : Mascot(
        tag = "mascot::main",
        spriteSheet = SpriteSheet(context, R.drawable.sprite_debugsheet_0, 60F),
        position = GeometricTools.Position(400F, 400F),
        areaRadius = 60F,
        velocity = player.velocity * 0.68F,
        follow = player,
        minFollowDistanceOffset = 40.0F
    ) {
        override fun consume(event: Event) {
            /* No consumer for this mascot */
            throw java.lang.IllegalStateException("No consumer for this mascot")
        }
    }

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(this.javaClass.name, "GameView surface created")
        gameLoop = GameLoopController(this, holder)
        gameLoop.start()
        physicsEngine = PhysicsEngine()
        physicsEngine.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(this.javaClass.name, "GameView Surface destroyed")
        gameLoop.interrupt()
        physicsEngine.interrupt()
        gameLoop.join()
        physicsEngine.join()
        isInitialized = false
    }

    fun ensureInitialized(canvas: Canvas) {
        if (isInitialized) return

        // Initialize UI elements if not already
        joystick = Joystick(canvas)
        kissButton = Button(
            context, R.drawable.asset_kissbutton, GeometricTools.Position(
                Button.DEFAULT_PADDING + Button.BUTTON_SIZE,
                canvas.height - Button.DEFAULT_PADDING - Button.BUTTON_SIZE
            ), Event(
                Button::class, "button::kiss", Player::class, "player::main"
            )
        )

        // Calculate max canvas size
        maxHeight = canvas.height
        maxWidth = canvas.width

        // Initialize subscriber events
        gameLoop.registerConsumer(player)

        // Initialize physics engine
        physicsEngine.collisionListeners.addAll(listOf(player, mascot))

        // Initialization
        isInitialized = true
    }

    fun update() {
        player.update()
        mascot.update()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Draw elements to screen
        mascot.draw(canvas)
        player.draw(canvas)

        // Draw UI
        kissButton.draw(canvas)
        joystick.draw(canvas)

        // Draw the overlay
        drawInformationOverlay(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            val touchPos = GeometricTools.Position(event.x, event.y)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    joystick.pressed = joystick.checkPressed(touchPos)
                    kissButton.pressed = kissButton.checkPressed(touchPos)
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    if (joystick.pressed) joystick.thumbstick(touchPos)
                    return true
                }

                MotionEvent.ACTION_UP -> {
                    // Joystick
                    if (joystick.pressed) {
                        joystick.resetActuator() // Reset the actuator
                        joystick.pressed = false // Joystick was not pressed
                    }
                    // KissButton
                    if (kissButton.pressed) {
                        gameLoop.publish(kissButton.post()) // Post an event is button was pressed
                        kissButton.pressed = false // Kiss button was un pressed
                    }
                    return true
                }
            }
        }

        // Call for super
        return super.onTouchEvent(event)
    }

    /**
     * Render FPS counter in the top corner
     */
    private fun drawInformationOverlay(canvas: Canvas) {
        val paint = Paint().apply {
            color = context.getColor(R.color.debug)
            textSize = 40F
        }

        // Draw into the canvas
        canvas.drawText(gameLoop.toString(), 0F, 50F, paint)
        canvas.drawText(physicsEngine.toString(), 0F, 100F, paint)
        canvas.drawText(player.toString(), 300F, canvas.height - 50F, paint)
        canvas.drawText(joystick.toString(), 300F, canvas.height - 100F, paint)
    }
}