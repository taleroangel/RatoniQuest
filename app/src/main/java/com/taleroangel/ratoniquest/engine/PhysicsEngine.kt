package com.taleroangel.ratoniquest.engine

import com.taleroangel.ratoniquest.tools.GeometricTools

class PhysicsEngine : Thread() {
    val collisionListeners: MutableList<PhysicsObject> = ArrayList()

    var collisionsHandled: Long = 0
        private set

    fun detectCollisions() {
        for (item in collisionListeners) {
            for (other in collisionListeners) {
                if (item !== other && GeometricTools.isColliding(
                        item.getConstraints(), other.getConstraints()
                    )
                ) {
                    collisionsHandled++
                    when (item.collisionType) {
                        PhysicsObject.CollisionType.SMALL_MASS -> item.onCollision(other.getConstraints())
                        PhysicsObject.CollisionType.SOLID -> other.onCollision(item.getConstraints())
                        PhysicsObject.CollisionType.BIG_MASS -> if (
                            other.collisionType != PhysicsObject.CollisionType.SOLID &&
                            other.collisionType != PhysicsObject.CollisionType.NONE
                        )
                            other.onCollision(
                                item.getConstraints()
                            ) else continue
                        PhysicsObject.CollisionType.NONE -> continue
                    }
                }
            }
        }
    }

    override fun run() {
        super.run()
        while (!currentThread().isInterrupted) {
            detectCollisions()
        }
    }

    override fun toString() =
        String.format("PhysicsEngine(collisionsHandled=%d)", collisionsHandled)
}