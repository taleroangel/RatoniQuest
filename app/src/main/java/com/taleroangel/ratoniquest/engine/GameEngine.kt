package com.taleroangel.ratoniquest.engine

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.taleroangel.ratoniquest.R
import com.taleroangel.ratoniquest.render.events.Event
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
        SpriteSheet(context, R.drawable.debug_sheet, 50, 80F),
        areaRadius = 80.0F,
        position = GeometricTools.Position(500F, 500F),
        velocity = 10.0F
    ) {
        /* Overridden member functions */
        override fun draw(canvas: Canvas) {
            spriteSheet.draw(canvas, position, direction)
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
                    println("Kiss kiss")
                }
            }
        }
    }

    /* Mascot */
    private val mascot = object : Mascot(
        tag = "mascot::main",
        spriteSheet = SpriteSheet(context, R.drawable.debug_sheet, 50, 60F),
        position = GeometricTools.Position(400F, 400F),
        areaRadius = 60F,
        velocity = player.velocity * 0.68F,
        follow = player,
        minFollowDistanceOffset = 40.0F
    ) {
        /* Draw method */
        override fun draw(canvas: Canvas) {
            spriteSheet.draw(canvas, position, direction)
        }

        override fun consume(event: Event) {
            throw java.lang.IllegalStateException("Mascot has no consumer")
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
        isInitialized = false;
    }

    fun ensureInitialized(canvas: Canvas) {
        if (isInitialized) return;

        // Initialize UI elements if not already
        joystick = Joystick(canvas)
        kissButton = object : Button(
            GeometricTools.Position(
                DEFAULT_PADDING + BUTTON_SIZE, canvas.height - DEFAULT_PADDING - BUTTON_SIZE
            ), Event(
                Button::class, "button::kiss", Player::class, "player::main"
            )
        ) {
            override fun draw(canvas: Canvas) {
                super.draw(canvas)
                // Draw icon
                canvas.drawCircle(position.x, position.y, BORDER_RADIUS, Paint().apply {
                    color = Color.RED
                    style = Paint.Style.FILL_AND_STROKE
                })
            }
        }

        // Calculate max canvas size
        maxHeight = canvas.height
        maxWidth = canvas.width

        // Initialize subscriber events
        gameLoop.eventListeners.add(player)

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
                        gameLoop.eventQueue.add(kissButton.post()) // Post an event is button was pressed
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
            textSize = 50F
        }

        // Draw into the canvas
        canvas.drawText(gameLoop.toString(), 0F, 50F, paint)
        canvas.drawText(player.toString(), 0F, 100F, paint)
        canvas.drawText(joystick.toString(), 0F, 150F, paint)
        canvas.drawText(this.javaClass.canonicalName!!, 0F, (canvas.height - 50F), paint)
    }
}