package com.taleroangel.ratoniquest.engine

import com.taleroangel.ratoniquest.tools.GeometricTools

abstract class PhysicsObject(
    val areaRadius: Float,
    var position: GeometricTools.Position,
    var direction: GeometricTools.Direction = GeometricTools.Direction.NONE,
    var velocity: Float = 0.0F,
    val collisionType: CollisionType = CollisionType.NONE
) {
    data class CircularConstraints(
        val areaRadius: Float,
        val position: GeometricTools.Position
    )

    enum class CollisionType {
        NONE,
        SOLID,
        BIG_MASS,
        SMALL_MASS,
    }

    fun getConstraints() = CircularConstraints(areaRadius, position)
    abstract fun onCollision(other: CircularConstraints): Unit
}