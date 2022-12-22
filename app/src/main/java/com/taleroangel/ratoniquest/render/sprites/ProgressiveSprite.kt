package com.taleroangel.ratoniquest.render.sprites

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.taleroangel.ratoniquest.tools.GeometricTools

/**
 * An animated sprite that stops it's animation on the last stage
 *
 * @param context Context from which the resources will be grabbed
 * @param resource Resource ID for the sprite
 * @param renderSize Size in which the sprite will be rendered
 * @param bitmapSize Size of the bitmap sprite
 * @param stages How many (Horizontal) stages does the sprite bitmap has
 * @param drawsPerStage How many draws will be made per stage (inversely alters animation speed)
 */
open class ProgressiveSprite(
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
            ), position.toRectF(renderSize), null
        )

        if (currentStage < (stages - 1)) {
            drawCounter++
            if (drawCounter >= drawsPerStage) {
                drawCounter = 0 // Restart the counter
                currentStage++
            }
        }
    }
}