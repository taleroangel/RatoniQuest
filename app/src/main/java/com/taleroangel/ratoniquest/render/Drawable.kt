package com.taleroangel.ratoniquest.render

import com.taleroangel.ratoniquest.engine.PhysicsObject
import com.taleroangel.ratoniquest.render.sprites.SpriteSheet
import com.taleroangel.ratoniquest.tools.GeometricTools

abstract class Drawable(
    var spriteSheet: SpriteSheet,
    areaRadius: Float,
    position: GeometricTools.Position,
    direction: GeometricTools.Direction = GeometricTools.Direction.NONE,
    velocity: Float = 0.0F,
) : Renderable, PhysicsObject(areaRadius, position, direction, velocity) {}