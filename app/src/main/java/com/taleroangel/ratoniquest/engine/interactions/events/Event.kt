package com.taleroangel.ratoniquest.engine.interactions.events

import kotlin.reflect.KClass

/**
 * An event is a piece on information that can be interchanged between objects or UI elements
 * An [EventGenerator] can create an event targeted to multiple [EventConsumer] if the [targetTag] is empty\
 * the event will then be sent to every [targetClass] object
 * filling the [targetTag] will send the event to the object with the corresponding [targetClass] and [targetTag]
 *
 * @constructor Create a basic event
 * @param generatorClass The class that generated the event
 * @param generatorTag TAG of the object that generated the event
 * @param targetClass The class that the event is targeted to
 * @param targetTag TAG of the object that the event is targeted to
 */
data class Event(
    val generatorClass: KClass<out EventGenerator>,
    val generatorTag: String? = null,
    val targetClass: KClass<out EventConsumer>,
    val targetTag: String? = null
)