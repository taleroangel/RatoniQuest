package com.taleroangel.ratoniquest.render.game

import com.taleroangel.ratoniquest.render.sprites.SpriteSheet
import com.taleroangel.ratoniquest.tools.GeometricTools

/**
 * Mascot is a [Player] which always moves int the direction of another specified Player
 */
abstract class Mascot(
    tag: String = "mascot::any",
    spriteSheet: SpriteSheet,
    areaRadius: Float,
    position: GeometricTools.Position,
    direction: GeometricTools.Direction = GeometricTools.Direction.NONE,
    velocity: Float = 0.0F,
    private val follow: Player,
    val minFollowDistanceOffset: Float = 0.0F
) : Player(tag, spriteSheet, areaRadius, position, direction, velocity) {

    override fun update() {
        // Get follow distance
        val distance = GeometricTools.euclideanDistance(position, follow.position)

        // If no distance detected
        if (distance == 0F) return
        // Follow minimum distance
        else if (distance < (spriteSheet.spriteSize + follow.spriteSheet.spriteSize + minFollowDistanceOffset)) {
            direction = GeometricTools.Direction.NONE
            return
        }

        // Distance per components
        val components = GeometricTools.findComponents(position, follow.position)

        // Follow
        position.x += velocity * components.first
        position.y += velocity * components.second
        direction = GeometricTools.angleToDirection(components.third)
    }
}