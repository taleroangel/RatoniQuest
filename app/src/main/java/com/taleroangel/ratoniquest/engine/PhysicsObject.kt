package com.taleroangel.ratoniquest.engine

import com.taleroangel.ratoniquest.tools.GeometricTools

abstract class PhysicsObject(
    val areaRadius: Float,
    var position: GeometricTools.Position,
    var direction: GeometricTools.Direction = GeometricTools.Direction.NONE,
    var velocity: Float = 0.0F,
) {
    data class CircularConstraints(
        val areaRadius: Float,
        val position: GeometricTools.Position
    )

    fun getConstraints() = CircularConstraints(areaRadius, position)
    abstract fun onCollision(other: CircularConstraints): Unit
}