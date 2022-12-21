package com.taleroangel.ratoniquest.render.events

import kotlin.reflect.KClass

interface EventGenerator {
    fun post(): Event;
}