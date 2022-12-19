package com.taleroangel.ratoniquest.render.game

import com.taleroangel.ratoniquest.render.Drawable

abstract class Player(
    protected val TAG: String = "NPC"
) : Drawable() {

    abstract val velocity: Float

    override fun toString(): String =
        String.format("Player[%s](x=%.1f, y=%.1f)", TAG, position.x, position.y)
}