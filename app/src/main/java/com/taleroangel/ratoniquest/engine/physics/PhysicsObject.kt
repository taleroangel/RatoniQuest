package com.taleroangel.ratoniquest.engine.physics

import com.taleroangel.ratoniquest.tools.GeometricTools

/**
 * An object that is subject to the laws of physics via [PhysicsEngine] thus it must declare\
 * properties like it's position, size, velocity or way of handling collisions
 *
 * @constructor Primary an only constructor for physics objects
 * @param areaRadius Object's hit-box is circular, this parameter defines it's hit-box radius
 * @param position Position of the object inside canvas, used in collision detection
 * @param direction Object orientation, use [GeometricTools.angleToDirection]\
 * for converting radians to [GeometricTools.Direction], [GeometricTools.Direction.NONE] by default
 * @param velocity Velocity of the object when moving, by default it won't move (0.0F)
 * @param collisionType [CollisionType] defines how the object will react to collisions, [CollisionType.NONE] by default (no collision)
 */
abstract class PhysicsObject(
    val areaRadius: Float,
    var position: GeometricTools.Position,
    var direction: GeometricTools.Direction = GeometricTools.Direction.NONE,
    var velocity: Float = 0.0F,
    val collisionType: CollisionType = CollisionType.NONE
) {

    /**
     * A class that defines the constraints of an object (hit-box) with it's position
     */
    data class CircularConstraints(
        val areaRadius: Float, val position: GeometricTools.Position
    )

    /**
     * Get current object constraints
     */
    fun getConstraints() = CircularConstraints(areaRadius, position)

    /**
     * How a collision gets handled by an object
     */
    enum class CollisionType {
        /**
         * No reaction to collisions
         */
        NONE,

        /**
         * Can't be moved, objects going into a solid get pushed back
         */
        SOLID,

        /**
         * Heavy object that only get pushed back by solids, and push back light objects with [LIGHT]
         */
        HEAVY,

        /**
         * Light object, will get pushed back by any other object
         */
        LIGHT,
    }

    /**
     * How object reacts to collisions, by default they push back
     */
    fun onCollision(other: CircularConstraints) {
        position = GeometricTools.collisionPushBack(
            getConstraints(), other
        )
    }
}