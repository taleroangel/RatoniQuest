package com.taleroangel.ratoniquest.render.sprites

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import com.taleroangel.ratoniquest.tools.GeometricTools

class SpriteSheet(context: Context, resource: Int, val bitmapSize: Int, val spriteSize: Float) {

    val bitmap: Bitmap = BitmapFactory.decodeResource(
        context.resources,
        resource,
        BitmapFactory.Options().apply { inScaled = false })

    val sprites: Map<GeometricTools.Direction, Sprite> =
        Sprite.generateSpriteMap(bitmap, bitmapSize, spriteSize)

    fun draw(
        canvas: Canvas,
        position: GeometricTools.Position,
        direction: GeometricTools.Direction
    ) = sprites[direction]!!.draw(canvas, position)
}