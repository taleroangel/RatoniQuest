package com.taleroangel.ratoniquest.render

import com.taleroangel.ratoniquest.engine.graphics.Renderable
import com.taleroangel.ratoniquest.engine.physics.PhysicsObject
import com.taleroangel.ratoniquest.engine.physics.PhysicsObject.CollisionType
import com.taleroangel.ratoniquest.render.sprites.SpriteSheet
import com.taleroangel.ratoniquest.tools.GeometricTools

/**
 * A class that can be drawn to the screen using a bitmap, it can interact with other Drawables
 *
 * @constructor Create a drawable class
 * @param spriteSheet the [SpriteSheet] to use when rendering
 * @param areaRadius Drawables have a circular hit-box, this parameter specifies the hit-box radius
 * @param position Initial position of the object
 * @param direction Initial direction of the object, [GeometricTools.Direction.NONE] by default
 * @param velocity Movement velocity, No movement by default
 * @param collisionType How the object will react to collisions [CollisionType.NONE] by default
 */
abstract class Drawable(
    var spriteSheet: SpriteSheet,
    areaRadius: Float,
    position: GeometricTools.Position,
    direction: GeometricTools.Direction = GeometricTools.Direction.NONE,
    velocity: Float = 0.0F,
    collisionType: CollisionType = CollisionType.NONE
) : Renderable, PhysicsObject(areaRadius, position, direction, velocity, collisionType)