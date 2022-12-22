package com.taleroangel.ratoniquest.engine.graphics

import android.graphics.Canvas
import android.util.Log
import android.view.SurfaceHolder
import com.taleroangel.ratoniquest.engine.GameEngine

/**
 * Render objects to the canvas
 *
 * @constructor Create the render engine
 * @param gameEngine Game engine from which to call [Renderable.draw] and [Renderable.update]
 * @param surfaceHolder Surface holder from which to obtain the Canvas
 */
class RenderEngine(
    private val gameEngine: GameEngine, private val surfaceHolder: SurfaceHolder
) : Thread() {

    /** Average FPS Counter */
    var averageFPS = 0.0
        private set

    override fun run() {
        super.run()

        // Time and cycle calculations
        var frameCount = 0
        var startTime = System.currentTimeMillis()

        // Game canvas
        var canvas: Canvas? = null

        // Infinite Loop
        while (!currentThread().isInterrupted) {
            try {
                //1. Rendering stage

                // Get the canvas ready for drawing
                canvas = surfaceHolder.lockCanvas()
                gameEngine.ensureInitialized(canvas)

                // Draw to the canvas
                synchronized(surfaceHolder) {
                    gameEngine.update()
                    gameEngine.draw(canvas)
                }
            } catch (_: IllegalArgumentException) {
                // Already locked canvas
            } catch (e: java.lang.NullPointerException) {
                // Game went out of scope
                Log.e(this.javaClass.name, "Surface cannot render")
                break // Break from the loop (Terminates game loop)
            } finally {
                // Done with canvas drawing
                if (canvas != null) surfaceHolder.unlockCanvasAndPost(canvas)
                frameCount++
            }

            // Frame calculation
            val elapsedTime = System.currentTimeMillis() - startTime
            if (elapsedTime >= 1000) // One second passed
            {
                // Calculate average time
                averageFPS = frameCount / (1E-3 * elapsedTime)
                startTime = System.currentTimeMillis()

                // Restart frame count
                frameCount = 0
            }
        }
    }

    override fun toString(): String =
        String.format("GameEngine[Loop](FPS=%.1f)", averageFPS)
}