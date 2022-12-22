package com.taleroangel.ratoniquest.engine.interactions.events

/**
 * Can send events
 */
interface EventGenerator {
    fun postEvent(): Event;
}