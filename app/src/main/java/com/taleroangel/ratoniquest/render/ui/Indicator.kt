package com.taleroangel.ratoniquest.render.ui

import android.content.Context
import android.graphics.*
import android.provider.CalendarContract.Colors
import com.taleroangel.ratoniquest.tools.GeometricTools

class Indicator(
    context: Context,
    resource: Int,
    position: GeometricTools.Position,
    areaRadius: Float,
    val bitmapSize: Float,
    var text: String,
    val textPaint: List<Paint>,
    val textAndBitmapPadding: Int = 20
) : UIComponent(position, areaRadius) {

    constructor(
        canvas: Canvas,
        context: Context,
        resource: Int,
        areaRadius: Float,
        bitmapSize: Float,
        text: String,
        textPaint: List<Paint> = listOf(
            Paint().apply {
                color = Color.WHITE
                textSize = 80F
                typeface = Typeface.create(this.typeface, Typeface.BOLD)
            },
            Paint().apply {
                style = Paint.Style.STROKE
                strokeWidth = 5F
                color = Color.BLACK
                textSize = 80F
                typeface = Typeface.create(this.typeface, Typeface.BOLD)
            }
        ),
        textAndBitmapPadding: Int = 20
    ) : this(
        context,
        resource,
        GeometricTools.Position((canvas.width - areaRadius - bitmapSize), areaRadius),
        areaRadius,
        bitmapSize,
        text,
        textPaint,
        textAndBitmapPadding
    )

    val bitmap: Bitmap = BitmapFactory.decodeResource(context.resources,
        resource,
        BitmapFactory.Options().apply { inScaled = false })

    override fun draw(canvas: Canvas) {
        canvas.drawBitmap(
            bitmap,
            null,
            GeometricTools.Position(position.x - areaRadius, position.y).toRectF(bitmapSize),
            null
        )
        textPaint.forEach {
            canvas.drawText(
                text,
                (position.x + bitmapSize + textAndBitmapPadding - areaRadius),
                (position.y + (it.textSize / 3)),
                it
            )
        }
    }
}