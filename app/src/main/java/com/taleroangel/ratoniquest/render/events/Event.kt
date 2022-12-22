package com.taleroangel.ratoniquest.render.events

import kotlin.reflect.KClass

data class Event(
    val generatorClass: KClass<out EventGenerator>,
    val generatorTag: String? = null,
    val targetClass: KClass<out EventConsumer>,
    val targetTag: String? = null,
    val info: Any? = null,
)