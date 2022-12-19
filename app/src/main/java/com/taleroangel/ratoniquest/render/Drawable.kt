package com.taleroangel.ratoniquest.render

import android.graphics.Paint
import com.taleroangel.ratoniquest.tools.GeometricTools

abstract class Drawable : Renderable {
    abstract var areaRadius: Float
    abstract var position: GeometricTools.Position
    abstract var paint: Paint
}