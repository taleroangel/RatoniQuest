package com.taleroangel.ratoniquest.engine.physics

import com.taleroangel.ratoniquest.tools.GeometricTools
import java.util.concurrent.locks.Lock

/**
 * Physics and Collision detection in a Thread
 * Every object that wants to react to collisions must [register] itself
 */
class PhysicsEngine : Thread() {
    private val listeners: MutableList<PhysicsObject> = ArrayList()

    /**
     * Register a new [PhysicsObject], physics engine will then be able to modify it's behaviour
     */
    fun register(physicsObject: PhysicsObject) {
        synchronized(listeners) {
            listeners.add(physicsObject)
        }
    }

    private var nCollisions: Long = 0

    fun detectCollisions() {
        synchronized(listeners) {
            for (item in listeners) {
                for (other in listeners) {
                    if (item !== other && GeometricTools.isColliding(
                            item.getConstraints(), other.getConstraints()
                        )
                    ) {
                        nCollisions++
                        when (item.collisionType) {
                            PhysicsObject.CollisionType.LIGHT -> item.onCollision(other.getConstraints())
                            PhysicsObject.CollisionType.SOLID -> other.onCollision(item.getConstraints())
                            PhysicsObject.CollisionType.HEAVY -> if (
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
    }

    override fun run() {
        super.run()
        while (!currentThread().isInterrupted) {
            detectCollisions()
        }
    }

    override fun toString() =
        String.format("PhysicsEngine(collisionsHandled=%d)", nCollisions)
}