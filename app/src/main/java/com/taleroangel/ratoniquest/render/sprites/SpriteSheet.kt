package com.taleroangel.ratoniquest.render.sprites

import android.content.Context
import android.graphics.*
import com.taleroangel.ratoniquest.tools.GeometricTools

/**
 * An animated sprite that repeats itself indefinitely
 *
 * @param context Context from which the resources will be grabbed
 * @param resource Resource ID for the sprite
 * @param renderSize Size in which the sprite will be rendered
 * @param bitmapSize Size of the bitmap sprite
 * @param stages How many (Horizontal) stages does the sprite bitmap has
 * @param drawsPerStage How many draws will be made per stage (inversely alters animation speed)
 */
class SpriteSheet(
    context: Context,
    resource: Int,
    var renderSize: Float,
    var bitmapSize: Int = 50,
    var stages: Int = 3,
    var drawsPerStage: Int = 15
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
            ), position.toRectF(renderSize), null
        )

        drawCounter++
        if (drawCounter >= drawsPerStage) {
            drawCounter = 0 // Restart the counter
            currentStage = (currentStage + 1) % stages // Move the stage
        }
    }
}