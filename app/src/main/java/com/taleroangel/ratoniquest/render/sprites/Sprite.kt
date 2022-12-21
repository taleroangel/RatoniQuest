package com.taleroangel.ratoniquest.render.sprites

import android.graphics.*
import com.taleroangel.ratoniquest.tools.GeometricTools

class Sprite(
    val bitmapSize: Int,
    val spriteSize: Float,
    val bitmap: Bitmap,
    val rect: Rect
) {
    companion object {
        fun generateCanvasPositionRect(size: Int) = mapOf(
            // NW, N, NE,
            GeometricTools.Direction.NW to Rect(
                0,
                0,
                size,
                size
            ),
            GeometricTools.Direction.N to Rect(
                size,
                0,
                2 * size,
                size
            ),
            GeometricTools.Direction.NE to Rect(
                2 * size,
                0,
                3 * size,
                size
            ),
            // W, NONE, E,
            GeometricTools.Direction.W to Rect(
                0,
                size,
                size,
                2 * size
            ),
            GeometricTools.Direction.NONE to Rect(
                size,
                size,
                2 * size,
                2 * size
            ),
            GeometricTools.Direction.E to Rect(
                2 * size,
                size,
                3 * size,
                2 * size
            ),
            // SW, S, SE
            GeometricTools.Direction.SW to Rect(
                0,
                2 * size,
                size,
                3 * size
            ),
            GeometricTools.Direction.S to Rect(
                size,
                2 * size,
                2 * size,
                3 * size
            ),
            GeometricTools.Direction.SE to Rect(
                2 * size,
                2 * size,
                3 * size,
                3 * size
            ),
        )

        fun generateSpriteMap(
            bitmap: Bitmap,
            bitmapSize: Int,
            spriteSize: Float,
        ): Map<GeometricTools.Direction, Sprite> {
            val canvasPositionRect = generateCanvasPositionRect(bitmapSize)
            return mapOf(
                GeometricTools.Direction.NW to Sprite(
                    bitmapSize,
                    spriteSize,
                    bitmap,
                    canvasPositionRect[GeometricTools.Direction.NW]!!
                ),
                GeometricTools.Direction.N to Sprite(
                    bitmapSize,
                    spriteSize,
                    bitmap,
                    canvasPositionRect[GeometricTools.Direction.N]!!
                ),
                GeometricTools.Direction.NE to Sprite(
                    bitmapSize,
                    spriteSize,
                    bitmap,
                    canvasPositionRect[GeometricTools.Direction.NE]!!
                ),
                GeometricTools.Direction.W to Sprite(
                    bitmapSize,
                    spriteSize,
                    bitmap,
                    canvasPositionRect[GeometricTools.Direction.W]!!
                ),
                GeometricTools.Direction.NONE to Sprite(
                    bitmapSize,
                    spriteSize,
                    bitmap,
                    canvasPositionRect[GeometricTools.Direction.NONE]!!
                ),
                GeometricTools.Direction.E to Sprite(
                    bitmapSize,
                    spriteSize,
                    bitmap,
                    canvasPositionRect[GeometricTools.Direction.E]!!
                ),
                GeometricTools.Direction.SW to Sprite(
                    bitmapSize,
                    spriteSize,
                    bitmap,
                    canvasPositionRect[GeometricTools.Direction.SW]!!
                ),
                GeometricTools.Direction.S to Sprite(
                    bitmapSize,
                    spriteSize,
                    bitmap,
                    canvasPositionRect[GeometricTools.Direction.S]!!
                ),
                GeometricTools.Direction.SE to Sprite(
                    bitmapSize,
                    spriteSize,
                    bitmap,
                    canvasPositionRect[GeometricTools.Direction.SE]!!
                ),
            )
        }
    }

    fun draw(
        canvas: Canvas,
        position: GeometricTools.Position
    ) {
        canvas.drawBitmap(
            bitmap,
            rect,
            position.toRectF(spriteSize),
            null
        )
    }
}