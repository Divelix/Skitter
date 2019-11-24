package com.divelix.skitter.components

import com.badlogic.ashley.core.Component

class DecayComponent(time: Float = 1f): Component {
    var timer = time //TODO implement this (stop decay after espire timer)
}