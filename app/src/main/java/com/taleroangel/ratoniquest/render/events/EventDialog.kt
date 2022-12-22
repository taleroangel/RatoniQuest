package com.taleroangel.ratoniquest.render.events

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.taleroangel.ratoniquest.render.sprites.ProgressiveSprite
import com.taleroangel.ratoniquest.tools.GeometricTools

class EventDialog(
    /**/
    val uptime: Long,
    val offset: GeometricTools.Position,
    val foregroundPositionOffset: GeometricTools.Position,
    /** Build context */
    context: Context,
    /** R. resource id */
    backgroundResource: Int,
    foregroundResource: Int,
    /** Size to render the sprite */
    spriteSize: Float,
    val foregroundSize: Float = 35F,
    /** Bitmap size */
    bitmapSize: Int = 50,
    /** Stages in the bitmap */
    stages: Int = 3,
    maxDrawPerStage: Int = 10
) : ProgressiveSprite(
    context,
    backgroundResource,
    spriteSize,
    bitmapSize,
    stages,
    maxDrawPerStage
) {
    var needsDrawing = true
    var timeCounter: Long? = null

    val foregroundBitmap: Bitmap = BitmapFactory.decodeResource(
        context.resources,
        foregroundResource,
        BitmapFactory.Options().apply { inScaled = false })

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
                        followPosition.x + foregroundPositionOffset.x,
                        followPosition.y + foregroundPositionOffset.y
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