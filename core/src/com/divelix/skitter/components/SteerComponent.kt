package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2

class SteerComponent: Component {
    val steering = Vector2()
    var maxSpeed = 1f
    var maxForce = 1f
}