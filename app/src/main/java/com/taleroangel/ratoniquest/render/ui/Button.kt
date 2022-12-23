package com.taleroangel.ratoniquest.render.ui

import android.content.Context
import android.graphics.*
import com.taleroangel.ratoniquest.engine.interactions.events.Event
import com.taleroangel.ratoniquest.engine.interactions.events.EventGenerator
import com.taleroangel.ratoniquest.tools.GeometricTools

class Button(
    context: Context,
    resource: Int,
    position: GeometricTools.Position,
    val event: Event
) : UIComponent(position, BUTTON_SIZE), EventGenerator {

    val bitmap: Bitmap = BitmapFactory.decodeResource(
        context.resources,
        resource,
        BitmapFactory.Options().apply { inScaled = false })

    companion object {
        const val DEFAULT_PADDING = 60.0F
        const val BUTTON_SIZE = 80.0F
        const val BORDER_RADIUS = 20.0F
    }

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

        // Draw the resource
        canvas.drawBitmap(bitmap, null, position.toRectF(DEFAULT_PADDING), null)
    }

    override fun postEvent(): Event = event
}