package com.taleroangel.ratoniquest.render.events

interface EventHandler {
    fun publish(event: Event)
    fun registerConsumer(eventConsumer: EventConsumer)
}