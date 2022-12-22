package com.taleroangel.ratoniquest.render.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.taleroangel.ratoniquest.render.sprites.ProgressiveSprite
import com.taleroangel.ratoniquest.tools.GeometricTools

/**
 * Show a Dialog above an object, it will follow it's position
 * It has a background sprite as well as a foreground sprite
 *
 * @param uptime How many ms will the [Dialog] be visible once completely rendered
 * @param offset Offset coordinates from the [draw] position
 * @param foregroundOffset  Offset coordinates for the foreground sprite, offset is applied to the [Dialog.offset]
 * @param context Context from which to grab the sprites
 * @param backgroundResource Background sprite resource ID (Must be a [ProgressiveSprite])
 * @param foregroundResource Foreground asset resource ID (No animation, single texture)
 * @param renderSize Size in which to render the dialog
 * @param foregroundSize Size in which to render the foreground
 * @param bitmapSize Size of the background bitmap sprite
 * @param stages Animation stages of the background sprite
 * @param drawsPerStage How many draws will be made per stage (inversely alters animation speed)
 */
class Dialog(
    val uptime: Long,
    val offset: GeometricTools.Position,
    val foregroundOffset: GeometricTools.Position,
    context: Context,
    backgroundResource: Int,
    foregroundResource: Int,
    renderSize: Float,
    val foregroundSize: Float,
    bitmapSize: Int = 50,
    stages: Int = 3,
    drawsPerStage: Int = 10
) : ProgressiveSprite(
    context,
    backgroundResource,
    renderSize,
    bitmapSize,
    stages,
    drawsPerStage
) {
    private var needsDrawing = true
    private var timeCounter: Long? = null

    private val foregroundBitmap: Bitmap = BitmapFactory.decodeResource(
        context.resources,
        foregroundResource,
        BitmapFactory.Options().apply { inScaled = false })

    /**
     * Draw the dialog once, it will disappear when [uptime] (ms) passes
     */
    override fun draw(canvas: Canvas, position: GeometricTools.Position) {
        if (needsDrawing) {
            // Calculate position
            val followPosition =
                GeometricTools.Position(position.x + offset.x, position.y + offset.y)

            // Draw background
            super.draw(canvas, followPosition)

            // Draw foreground if background was already drawn
            if (currentStage >= (stages - 1)) {
                canvas.drawBitmap(
                    foregroundBitmap,
                    null,
                    GeometricTools.Position(
                        followPosition.x + foregroundOffset.x,
                        followPosition.y + foregroundOffset.y
                    ).toRectF(foregroundSize),
                    null
                )

                // Calculate time
                if (timeCounter == null) {
                    timeCounter = System.currentTimeMillis()
                } else if ((System.currentTimeMillis() - timeCounter!!) >= uptime) {
                    needsDrawing = false
                }
            }
        }
    }
}