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
import com.taleroangel.ratoniquest.engine.graphics.RenderEngine
import com.taleroangel.ratoniquest.engine.interactions.InteractionsEngine
import com.taleroangel.ratoniquest.engine.physics.PhysicsEngine
import com.taleroangel.ratoniquest.engine.interactions.events.Event
import com.taleroangel.ratoniquest.engine.interactions.events.EventGenerator
import com.taleroangel.ratoniquest.render.ui.Dialog
import com.taleroangel.ratoniquest.render.ui.Button
import com.taleroangel.ratoniquest.render.ui.Joystick
import com.taleroangel.ratoniquest.render.game.Mascot
import com.taleroangel.ratoniquest.render.game.NPC
import com.taleroangel.ratoniquest.render.game.Player
import com.taleroangel.ratoniquest.render.sprites.SpriteSheet
import com.taleroangel.ratoniquest.render.ui.Indicator
import com.taleroangel.ratoniquest.tools.GeometricTools
import kotlin.properties.Delegates

class GameEngine(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    /* Initialize the subsystems */
    private var renderEngine = RenderEngine(this, holder)
    private var physicsEngine = PhysicsEngine()
    private var interactionsEngine = InteractionsEngine()

    /* UI components that depend on the Canvas constraints */
    private lateinit var joystick: Joystick
    private lateinit var kissButton: Button
    private lateinit var kissIndicator: Indicator

    /* Canvas constraints (late init) */
    private var maxHeight: Int by Delegates.notNull()
    private var maxWidth: Int by Delegates.notNull()
    private var isInitialized = false

    /* Player */
    private val player = object : Player(
        "player::main",
        SpriteSheet(context, R.drawable.sprite_debugsheet_0, 80F),
        areaRadius = 80.0F,
        position = GeometricTools.Position(500F, 500F),
        velocity = 10.0F,
    ) {
        /* Dialog to show */
        var dialog: Dialog? = null
        val activationDistanceForNPC = 35F
        val nearNPC: MutableMap<String, NPC> = HashMap()
        var kissCount: Int = 0
        val kissAddAmount = 5

        /* Overridden member functions */
        override fun draw(canvas: Canvas) {
            super.draw(canvas) // Draw the player
            dialog?.draw(canvas, position) // Draw the dialog
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

            // Check for NPCs
            for (npc in interactionsEngine.request<NPC>()) {
                // Check distance
                GeometricTools.euclideanDistance(position, npc.position).let {
                    when {
                        (it <= (areaRadius + npc.areaRadius + activationDistanceForNPC)) -> {
                            npc.inMotion = false
                            nearNPC.put(npc.tag, npc)
                        }
                        else -> {
                            npc.inMotion = true
                            nearNPC.remove(npc.tag)
                        }
                    }
                }
            }
        }

        /* When an event arrives */
        override fun consumeEvent(event: Event) {
            when (event.generatorClass) {
                // Kiss button
                Button::class -> if (event.generatorTag == "button::kiss") {
                    dialog = Dialog(
                        uptime = 2500,
                        offset = GeometricTools.Position(areaRadius + 20F, -areaRadius - 20F),
                        foregroundOffset = GeometricTools.Position(5F, -25F),
                        context = context,
                        backgroundResource = R.drawable.progressive_dialog,
                        foregroundResource = if (nearNPC.containsKey("npc::ratona")) R.drawable.asset_kissbutton else R.drawable.progressive_dialog,
                        foregroundSize = 35F,
                        renderSize = 85F
                    ).apply {
                        onCompletion = {
                            kissCount += kissAddAmount
                            kissIndicator.text = kissCount.toString()
                        }
                    }
                }
            }
        }

        override fun toString() =
            String.format("%s(nearNPCs=%d, kiss=%d)", super.toString(), nearNPC.size, kissCount)
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
        override fun consumeEvent(event: Event) {
            /* No consumer for this mascot */
            throw java.lang.IllegalStateException("No consumer for this mascot")
        }
    }

    /* Ratona NPC */
    private val npc = object : NPC(
        "npc::ratona",
        SpriteSheet(context, R.drawable.sprite_debugsheet_0, 80F),
        areaRadius = 80.0F,
        position = GeometricTools.Position(1000F, 800F),
    ) {}

    init {
        holder.addCallback(this)
        isFocusable = true
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d(this.javaClass.name, "GameView surface created")

        // Create engines
        renderEngine = RenderEngine(this, holder)
        physicsEngine = PhysicsEngine()
        interactionsEngine = InteractionsEngine()

        // Start engines
        renderEngine.start()
        physicsEngine.start()
        interactionsEngine.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(this.javaClass.name, "GameView Surface destroyed")

        // Interrupt subsystems
        renderEngine.interrupt()
        physicsEngine.interrupt()
        interactionsEngine.interrupt()

        // Join them
        renderEngine.join()
        physicsEngine.join()
        interactionsEngine.join()

        // Mark as uninitialized
        isInitialized = false
    }

    fun ensureInitialized(canvas: Canvas) {
        if (isInitialized) return

        // Initialize UI elements if not already
        joystick = Joystick(canvas)
        kissIndicator = Indicator(canvas, context, R.drawable.asset_kissbutton, 100F, 30F, "0")
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

        // Initialize all interactions
        with(interactionsEngine) {
            registerEventConsumer(player)
            register(player)
            register(npc)
        }

        // Initialize physics engine
        with(physicsEngine) {
            register(player)
            register(mascot)
            register(npc)
        }

        // Initialization
        isInitialized = true
    }

    fun update() {
        player.update()
        mascot.update()
        npc.update()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        // Draw elements to screen
        mascot.draw(canvas)
        npc.draw(canvas)
        player.draw(canvas)

        // Draw UI
        kissButton.draw(canvas)
        joystick.draw(canvas)
        kissIndicator.draw(canvas)

        // Draw the overlay
        drawInformationOverlay(canvas)
    }

    /**
     * Listen for touch events
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            // Calculate the touch position
            val touchPos = GeometricTools.Position(event.x, event.y)

            // Handle touch type
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
                        interactionsEngine.publish(kissButton.postEvent()) // Post an event is button was pressed
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
     * Render FPS counter and other useful DEBUG information
     */
    private fun drawInformationOverlay(canvas: Canvas) {
        val paint = Paint().apply {
            color = context.getColor(R.color.debug)
            textSize = 40F
        }

        // Draw into the canvas
        canvas.drawText(renderEngine.toString(), 0F, 50F, paint)
        canvas.drawText(physicsEngine.toString(), 0F, 100F, paint)
        canvas.drawText(interactionsEngine.toString(), 0F, 150F, paint)

        // Players
        canvas.drawText(player.toString(), 300F, canvas.height - 50F, paint)
        canvas.drawText(joystick.toString(), 300F, canvas.height - 100F, paint)
        canvas.drawText(npc.toString(), 300F, canvas.height - 150F, paint)
    }
}