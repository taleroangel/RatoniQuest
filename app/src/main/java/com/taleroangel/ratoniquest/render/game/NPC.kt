package com.taleroangel.ratoniquest.render.game

import android.graphics.Canvas
import com.taleroangel.ratoniquest.engine.interactions.events.Event
import com.taleroangel.ratoniquest.render.sprites.SpriteSheet
import com.taleroangel.ratoniquest.tools.GeometricTools
import kotlin.math.roundToInt
import kotlin.random.Random

abstract class NPC(
    tag: String = "npc::any",
    spriteSheet: SpriteSheet,
    areaRadius: Float,
    position: GeometricTools.Position,
    direction: GeometricTools.Direction = GeometricTools.Direction.NONE,
    velocity: Float = 5.0F,
    collisionType: CollisionType = CollisionType.LIGHT
) : Player(tag, spriteSheet, areaRadius, position, direction, velocity, collisionType) {

    var height: Int? = null
    var width: Int? = null
    var target = position
    var inMotion = true

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        height = canvas.height
        width = canvas.width
    }

    override fun update() {
        if (height == null || width == null) return
        if (!inMotion) {
            direction = GeometricTools.Direction.NONE
            return
        }

        if ((GeometricTools.euclideanDistance(
                position,
                target
            ) < 10F)
        ) {
            // 2% Probability of moving
            if ((Random.nextFloat() * 500F).roundToInt() == 250) {
                target =
                    GeometricTools.Position(
                        Random.nextFloat() * width!!,
                        Random.nextFloat() * height!!
                    )
            } else {
                direction = GeometricTools.Direction.NONE
            }
        } else {
            // Distance per components
            val components = GeometricTools.findComponents(position, target)

            // Follow
            position.x += velocity * components.first
            position.y += velocity * components.second
            direction = GeometricTools.angleToDirection(components.third)
        }
    }

    override fun consumeEvent(event: Event) {
        if (event.targetTag == tag)
            inMotion = !inMotion
    }

    override fun toString() =
        String.format("NPC[%s](x=%.2f, y=%.2f, inMotion=%b)", tag, position.x, position.y, inMotion)
}