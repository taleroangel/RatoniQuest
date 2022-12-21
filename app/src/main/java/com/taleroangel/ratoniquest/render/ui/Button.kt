package com.taleroangel.ratoniquest.render.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.annotation.CallSuper
import com.taleroangel.ratoniquest.render.events.Event
import com.taleroangel.ratoniquest.render.events.EventGenerator
import com.taleroangel.ratoniquest.tools.GeometricTools

abstract class Button(
    position: GeometricTools.Position,
    val event: Event
) : UIComponent(position, BUTTON_SIZE), EventGenerator {

    companion object {
        const val DEFAULT_PADDING = 60.0F
        const val BUTTON_SIZE = 80.0F
        const val BORDER_RADIUS = 20.0F
    }

    @CallSuper
    override fun draw(canvas: Canvas) {
        // Draw outer box
        canvas.drawPath(
            Path().apply {
                addRoundRect(
                    position.toRectF(BUTTON_SIZE),
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
    }

    override fun post(): Event = event
}