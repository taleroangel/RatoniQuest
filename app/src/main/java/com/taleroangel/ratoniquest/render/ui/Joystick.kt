package com.taleroangel.ratoniquest.render.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.taleroangel.ratoniquest.render.Renderable
import com.taleroangel.ratoniquest.tools.GeometricTools
import kotlin.math.atan

class Joystick(position: GeometricTools.Position) :
    UIComponent(position, OUTER_RADIUS) {

    companion object {
        const val OUTER_RADIUS = 150F
        const val INNER_RADIUS = 60F
        const val DEFAULT_PADDING = 100F
    }

    /**
     * Create Joystick at the bottom-right corner of the screen
     * Requires a [Canvas] to identify the screen constraints
     */
    constructor(canvas: Canvas, padding: Float = DEFAULT_PADDING) : this(
        GeometricTools.Position(
            canvas.width - padding - OUTER_RADIUS, canvas.height - padding - OUTER_RADIUS
        )
    )

    private var innerCircle: GeometricTools.Position = position.copy()

    /**
     * Return direction from -1 to 1 in a pair of coordinates <x, y>
     */
    fun calculateDirection(): Triple<Float, Float, Float> =
        GeometricTools.findComponents(position, innerCircle, areaRadius)


    /**
     * Calculate and set inner actuator position
     */
    fun thumbstick(touchPosition: GeometricTools.Position) {
        val deltas =
            GeometricTools.deltaComponentDistance(position, touchPosition) // Delta distances
        val euclideanDistance =
            GeometricTools.euclideanDistance(position, touchPosition) // Distance from center

        val sumX: Float
        val sumY: Float

        // Calculate distance
        if (euclideanDistance < areaRadius) {
            sumX = position.x + deltas.first
            sumY = position.y + deltas.second
        } else {
            sumX = position.x + (deltas.first / euclideanDistance) * areaRadius
            sumY = position.y + (deltas.second / euclideanDistance) * areaRadius
        }

        // Move position
        innerCircle = GeometricTools.Position(sumX, sumY)
    }

    fun resetActuator() {
        // Make inner circle be at center again
        innerCircle = position.copy()
    }

    override fun draw(canvas: Canvas) {
        // Draw outer circle
        canvas.drawCircle(position.x, position.y, OUTER_RADIUS, Paint().apply {
            color = Color.argb(0.75F, 1F, 1F, 1F)
            style = Paint.Style.FILL_AND_STROKE
        })

        // Draw inner circle
        canvas.drawCircle(innerCircle.x, innerCircle.y, INNER_RADIUS, Paint().apply {
            color = Color.parseColor("#d61010")
            style = Paint.Style.FILL_AND_STROKE
        })
    }

    override fun toString(): String {
        val values = calculateDirection();
        return String.format(
            "Joystick(x=%.2f, y=%.2f, a=%.2f, pressed=%b)",
            values.first,
            values.second,
            values.third,
            pressed
        )
    }
}