package com.taleroangel.ratoniquest.render

import android.graphics.Canvas

interface Renderable {
    fun draw(canvas: Canvas)
    fun update()
}