package com.taleroangel.ratoniquest.render.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.taleroangel.ratoniquest.render.Renderable
import com.taleroangel.ratoniquest.tools.GeometricTools

class Button(
    position: GeometricTools.Position
) : UIComponent(position, BUTTON_SIZE) {

    companion object {
        const val DEFAULT_PADDING = 60.0F
        const val BUTTON_SIZE = 80.0F
        const val BORDER_RADIUS = 20.0F
    }

    constructor(canvas: Canvas, padding: Float = DEFAULT_PADDING) : this(
        GeometricTools.Position(
            padding + BUTTON_SIZE,
            canvas.height - padding - BUTTON_SIZE
        )
    )

    override fun draw(canvas: Canvas) {
        // Draw outer box
        canvas.drawPath(
            Path().apply {
                addRoundRect(
                    RectF(
                        position.x - BUTTON_SIZE,
                        position.y + BUTTON_SIZE,
                        position.x + BUTTON_SIZE,
                        position.y - BUTTON_SIZE
                    ),
                    floatArrayOf(
                        BORDER_RADIUS, BORDER_RADIUS,   // Top left radius in px
                        BORDER_RADIUS, BORDER_RADIUS,   // Top right radius in px
                        BORDER_RADIUS, BORDER_RADIUS,   // Bottom right radius in px
                        BORDER_RADIUS, BORDER_RADIUS    // Bottom left radius in px
                    ),
                    Path.Direction.CW
                )
            }, Paint().apply {
                // Set color if pressed or not
                color = if (pressed) Color.GRAY else Color.WHITE
                // Change style
                style = Paint.Style.FILL_AND_STROKE
            })

        // Draw icon
        canvas.drawCircle(position.x, position.y, BORDER_RADIUS, Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL_AND_STROKE
        })
    }
}