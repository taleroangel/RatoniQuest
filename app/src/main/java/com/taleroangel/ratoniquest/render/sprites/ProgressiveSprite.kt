package com.taleroangel.ratoniquest.render.sprites

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.taleroangel.ratoniquest.tools.GeometricTools

open class ProgressiveSprite(
    /** Build context */
    context: Context,
    /** R. resource id */
    resource: Int,
    /** Size to render the sprite */
    var spriteSize: Float,
    /** Bitmap size */
    var bitmapSize: Int = 50,
    /** Stages in the bitmap */
    var stages: Int = 3,
    var maxDrawPerStage: Int = 15
) {
    var currentStage: Int = 0
    var drawCounter: Int = 0

    val bitmap: Bitmap = BitmapFactory.decodeResource(
        context.resources,
        resource,
        BitmapFactory.Options().apply { inScaled = false })

    open fun draw(
        canvas: Canvas,
        position: GeometricTools.Position
    ) {
        canvas.drawBitmap(
            bitmap, Rect(
                currentStage * bitmapSize,
                0,
                (currentStage + 1) * bitmapSize,
                bitmapSize,
            ), position.toRectF(spriteSize), null
        )

        if (currentStage < (stages - 1)) {
            drawCounter++
            if (drawCounter >= maxDrawPerStage) {
                drawCounter = 0 // Restart the counter
                currentStage++
            }
        }
    }
}