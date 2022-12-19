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
import com.taleroangel.ratoniquest.render.ui.Button
import com.taleroangel.ratoniquest.render.ui.Joystick
import com.taleroangel.ratoniquest.render.game.Mascot
import com.taleroangel.ratoniquest.render.game.Player
import com.taleroangel.ratoniquest.tools.GeometricTools
import kotlin.properties.Delegates

class GameEngine(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    /** Game engine variables */
    var isInitialized = false
        private set

    /** Initialize the GameLoop */
    private var gameLoop = GameLoopController(this, holder)

    /** Game components */
    private lateinit var joystick: Joystick
    private lateinit var kissButton: Button

    /** Canvas constraints */
    var maxHeight: Int by Delegates.notNull()
    var maxWidth: Int by Delegates.notNull()

    /** Player */
    private val player = object : Player("Ratón") {
        /** Overridden member variables */
        override val velocity = 10.0F
        override var position = GeometricTools.Position(500F, 500F)
        override var areaRadius = 50.0F
        override var paint = Paint().apply {
            color = context.getColor(R.color.purple_500)
        }

        /* Overridden member functions */
        override fun draw(canvas: Canvas) {
            canvas.drawCircle(position.x, position.y, 50.0F, paint)
        }

        override fun update() {
            // Move according to joystick
            if (joystick.pressed) {
                val direction = joystick.calculateDirection()
                position.x += velocity * direction.first
                position.y += velocity * direction.second
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
    }

    /** Mascot */
    private val mascot = object : Mascot("Dino", player) {
        /* Overridden variables */
        override val minFollowDistanceOffset: Float = 40.0F
        override var areaRadius = 40F
        override var position = GeometricTools.Position(400F, 400F)
        override var paint = Paint().apply {
            color = context.getColor(R.color.teal_200)
        }

        /* Draw method */
        override fun draw(canvas: Canvas) {
            canvas.drawCircle(position.x, position.y, 40F, paint)
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
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        Log.d(this.javaClass.name, "GameView Surface destroyed")
        gameLoop.interrupt()
        gameLoop.join()
    }

    fun ensureInitializedCanvas(canvas: Canvas) {
        // Initialize UI elements if not already
        if (!this::joystick.isInitialized) {
            joystick = Joystick(canvas)
        }
        if (!this::kissButton.isInitialized) {
            kissButton = Button(canvas)
        }

        // Calculate max canvas size
        maxHeight = canvas.height
        maxWidth = canvas.width

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
                    joystick.pressed = false
                    kissButton.pressed = false
                    joystick.resetActuator()
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