package com.taleroangel.ratoniquest.engine.interactions

import com.taleroangel.ratoniquest.engine.interactions.events.Event
import com.taleroangel.ratoniquest.engine.interactions.events.EventConsumer
import com.taleroangel.ratoniquest.engine.interactions.events.EventHandler
import java.util.*
import kotlin.collections.ArrayList

/**
 * Handle interactions between object or UI
 */
class InteractionsEngine : Thread(), EventHandler {
    var eventCount = 0
        private set

    private val eventQueue: Queue<Event> = ArrayDeque()
    private val eventListeners: MutableList<EventConsumer> = ArrayList()
    private val interactions: MutableList<CanInteract> = ArrayList()

    /**
     * Register a [CanInteract] object into the [InteractionsEngine]
     */
    fun register(interact: CanInteract) {
        interactions.add(interact)
    }

    /**
     * Request a [CanInteract] for interacting with it
     */
    fun request(tag: String): CanInteract? = interactions.find { it.tag == tag }

    /**
     * Publish an [Event] fot it to be delivered to every [EventConsumer] in the queue
     */
    override fun publish(event: Event) {
        eventQueue.add(event)
    }

    /**
     * Register for [Event] delivery
     */
    override fun registerEventConsumer(eventConsumer: EventConsumer) {
        eventListeners.add(eventConsumer)
    }

    override fun run() {
        super.run()
        while (!currentThread().isInterrupted) {
            //0. Game updates stage
            while (eventQueue.isNotEmpty()) {
                val event = eventQueue.poll()!!
                eventListeners.forEach { eventConsumer ->
                    if ((event.targetTag == eventConsumer.tag) || ((event.targetTag != null) && (eventConsumer::class == event.targetClass))) eventConsumer.consumeEvent(
                        event
                    )
                }
                eventCount++
            }
        }
    }

    override fun toString() = String.format("InteractionsEngine(eventsHandled=%d)", eventCount)
}