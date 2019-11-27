package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class MoveComponent: Component {
    val direction = Vector2()
    var speed = 1f
}