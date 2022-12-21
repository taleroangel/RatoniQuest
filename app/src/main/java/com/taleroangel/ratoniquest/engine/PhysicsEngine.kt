package com.taleroangel.ratoniquest.engine

import com.taleroangel.ratoniquest.tools.GeometricTools

class PhysicsEngine : Thread() {
    val collisionListeners: MutableList<PhysicsObject> = ArrayList()

    fun detectCollisions() {
        for (item in collisionListeners) {
            for (other in collisionListeners) {
                if (item !== other && GeometricTools.isColliding(
                        item.getConstraints(),
                        other.getConstraints()
                    )
                ) {
                    item.onCollision(other.getConstraints())
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
}