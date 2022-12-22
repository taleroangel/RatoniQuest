package com.taleroangel.ratoniquest.render.sprites

import android.content.Context
import android.graphics.*
import com.taleroangel.ratoniquest.tools.GeometricTools

class SpriteSheet(
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

    fun draw(
        canvas: Canvas,
        position: GeometricTools.Position,
        direction: GeometricTools.Direction
    ) {
        canvas.drawBitmap(
            bitmap, Rect(
                (bitmapSize * direction.index),
                (bitmapSize * currentStage),
                (bitmapSize * (direction.index + 1)),
                (bitmapSize * (currentStage + 1)),
            ), position.toRectF(spriteSize), null
        )

        drawCounter++
        if (drawCounter >= maxDrawPerStage) {
            drawCounter = 0 // Restart the counter
            currentStage = (currentStage + 1) % stages // Move the stage
        }
    }
}