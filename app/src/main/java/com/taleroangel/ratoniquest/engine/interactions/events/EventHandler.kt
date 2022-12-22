package com.taleroangel.ratoniquest.engine.interactions.events

/**
 * Defines an EventHandler responsibilities
 */
interface EventHandler {
    fun publish(event: Event)
    fun registerEventConsumer(eventConsumer: EventConsumer)
}