package com.taleroangel.ratoniquest.render.game

import com.taleroangel.ratoniquest.tools.GeometricTools

/**
 * Mascot is a [Player] which always moves int the direction of another specified Player
 */
abstract class Mascot(
    tag: String = "GMascot",
    private val follow: Player,
    /** Set mascot velocity as [follow] velocity time the multiplier*/
    velocityMultiplier: Float = 0.55F
) : Player() {

    override var velocity = follow.velocity * velocityMultiplier
    abstract val minFollowDistanceOffset: Float

    override fun update() {
        // Get follow distance
        val distance = GeometricTools.euclideanDistance(position, follow.position)

        // If no distance detected
        if (distance == 0F) return

        // Check for collision
        if (GeometricTools.isColliding(position, areaRadius, follow.position, follow.areaRadius)) {
            position = GeometricTools.collisionPushBack(
                position,
                areaRadius,
                follow.position,
                follow.areaRadius
            )
        }

        // Follow minimum distance
        if (distance < (areaRadius + follow.areaRadius + minFollowDistanceOffset)) return

        // Distance per components
        val delta = GeometricTools.deltaComponentDistance(position, follow.position)

        // Follow
        position.x += velocity * (delta.first / distance)
        position.y += velocity * (delta.second / distance)
    }
}