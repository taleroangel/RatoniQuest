package com.taleroangel.ratoniquest.engine.graphics

import android.graphics.Canvas

/**
 * Interface for Classes that need to be rendered to the screen
 */
interface Renderable {
    /**
     * How the object will be drawn to the canvas
     */
    fun draw(canvas: Canvas)

    /**
     * Update the state before drawing
     */
    fun update()
}