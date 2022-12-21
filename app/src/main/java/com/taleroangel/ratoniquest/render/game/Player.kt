package com.taleroangel.ratoniquest.render.game

import com.taleroangel.ratoniquest.render.Drawable
import com.taleroangel.ratoniquest.render.events.EventConsumer
import com.taleroangel.ratoniquest.render.sprites.SpriteSheet
import com.taleroangel.ratoniquest.tools.GeometricTools

abstract class Player(
    override val tag: String = "player::any",
    spriteSheet: SpriteSheet,
    areaRadius: Float,
    position: GeometricTools.Position,
    direction: GeometricTools.Direction = GeometricTools.Direction.NONE,
    velocity: Float = 0.0F,
) : Drawable(spriteSheet, areaRadius, position, direction, velocity), EventConsumer {

    override fun onCollision(other: CircularConstraints) {
        // Player pushes things around
        if (tag != "player::main") position = GeometricTools.collisionPushBack(
            getConstraints(), other
        )
    }

    override fun toString() =
        String.format("Player[%s](x=%.2f, y=%.2f)", tag, position.x, position.y)
}