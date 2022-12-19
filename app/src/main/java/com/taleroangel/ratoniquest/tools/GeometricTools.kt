package com.taleroangel.ratoniquest.tools

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

object GeometricTools {

    data class Position(
        var x: Float,
        var y: Float
    )

    fun deltaComponentDistance(from: Position, to: Position) =
        Pair((to.x - from.x), (to.y - from.y))

    fun euclideanDistance(from: Position, to: Position) =
        sqrt((abs(from.x - to.x)).pow(2) + (abs(from.y - to.y)).pow(2))

    fun isWithinBoundaries(radius: Float, from: Position, to: Position) =
        euclideanDistance(from, to) <= radius;

    fun isColliding(pos1: Position, radius1: Float, pos2: Position, radius2: Float): Boolean {
        val distance = euclideanDistance(pos1, pos2);
        return distance <= (radius1 + radius2)
    }

    fun collisionPushBack(
        pos1: Position,
        radius1: Float,
        pos2: Position,
        radius2: Float
    ): Position {
        val deltas = deltaComponentDistance(pos1, pos2)
        val distance = euclideanDistance(pos1, pos2)
        val factor = abs(distance - radius2 - radius1) / distance
        return Position(pos1.x - (deltas.first * factor), pos1.y - (deltas.second * factor))
    }
}