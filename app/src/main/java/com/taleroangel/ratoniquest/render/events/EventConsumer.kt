package com.taleroangel.ratoniquest.render.events

interface EventConsumer {
    val tag: String
    fun consume(event: Event)
}