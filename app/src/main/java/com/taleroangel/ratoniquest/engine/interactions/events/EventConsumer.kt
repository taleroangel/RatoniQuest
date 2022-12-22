package com.taleroangel.ratoniquest.engine.interactions.events

import com.taleroangel.ratoniquest.engine.interactions.CanInteract

/**
 * Can consume events
 */
interface EventConsumer: CanInteract {
    fun consumeEvent(event: Event)
}