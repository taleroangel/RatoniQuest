package com.taleroangel.ratoniquest.tools

import android.graphics.RectF
import com.taleroangel.ratoniquest.engine.PhysicsObject
import kotlin.math.*

object GeometricTools {

    data class Position(
        var x: Float,
        var y: Float
    ) {
        fun toRectF(radius: Float): RectF =
            RectF(
                x - radius,
                y - radius,
                x + radius,
                y + radius
            )
    }

    enum class Direction {
        NW, N, NE,
        W, NONE, E,
        SW, S, SE
    }

    fun deltaComponentDistance(from: Position, to: Position) =
        Pair((to.x - from.x), (to.y - from.y))

    fun euclideanDistance(from: Position, to: Position) =
        sqrt((abs(from.x - to.x)).pow(2) + (abs(from.y - to.y)).pow(2))

    fun isWithinBoundaries(radius: Float, from: Position, to: Position) =
        euclideanDistance(from, to) <= radius;

    fun isColliding(
        obj1: PhysicsObject.CircularConstraints,
        obj2: PhysicsObject.CircularConstraints
    ): Boolean {
        val distance = euclideanDistance(obj1.position, obj2.position);
        return distance <= (obj1.areaRadius + obj2.areaRadius)
    }

    fun findComponents(
        from: Position,
        to: Position,
        capVal: Float? = null
    ): Triple<Float, Float, Float> {
        val pairs = deltaComponentDistance(from, to)
        val norm = euclideanDistance(from, to)
        if (norm == 0F) return Triple(0F, 0F, Float.NaN)
        return Triple(
            (pairs.first / (capVal ?: norm)),
            (pairs.second / (capVal ?: norm)),
            if (-pairs.second > 0) (acos(pairs.first / norm)) else (PI.toFloat() + (acos(-pairs.first / norm)))
        )
    }

    fun angleToDirection(angle: Float): Direction {
        return when {
            (angle.isNaN()) -> Direction.NONE
            (angle < PI / 6) -> Direction.E
            (angle < PI / 3) -> Direction.NE
            (angle < 2 * PI / 3) -> Direction.N
            (angle < 5 * PI / 6) -> Direction.NW
            (angle < 7 * PI / 6) -> Direction.W
            (angle < 4 * PI / 3) -> Direction.SW
            (angle < 5 * PI / 3) -> Direction.S
            (angle < 11 * PI / 6) -> Direction.SE
            (angle <= 2 * PI) -> Direction.E
            else -> Direction.NONE
        }
    }

    fun collisionPushBack(
        obj1: PhysicsObject.CircularConstraints,
        obj2: PhysicsObject.CircularConstraints
    ): Position {
        val deltas = deltaComponentDistance(obj1.position, obj2.position)
        val distance = euclideanDistance(obj1.position, obj2.position)
        val factor = abs(distance - obj2.areaRadius - obj1.areaRadius) / distance
        return Position(
            obj1.position.x - (deltas.first * factor),
            obj1.position.y - (deltas.second * factor)
        )
    }
}