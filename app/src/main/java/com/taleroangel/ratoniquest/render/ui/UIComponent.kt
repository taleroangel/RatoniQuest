package com.taleroangel.ratoniquest.render.ui

import com.taleroangel.ratoniquest.engine.graphics.Renderable
import com.taleroangel.ratoniquest.tools.GeometricTools

abstract class UIComponent(
    val position: GeometricTools.Position,
    val areaRadius: Float
) : Renderable {
    open var pressed: Boolean = false

    fun checkPressed(touchPosition: GeometricTools.Position): Boolean =
        GeometricTools.isWithinBoundaries(areaRadius, position, touchPosition)

    override fun update(): Nothing {
        throw java.lang.IllegalArgumentException("default update() should not be called from an UI element")
    }
}