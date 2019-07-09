package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.joints.MouseJoint

class MouseComponent: Component {
    lateinit var mouseJoint: MouseJoint
}