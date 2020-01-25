package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Array

class AgentComponent: Component { // VisionComponent name makes more sense
    val visibleEntities = Array<Entity>(5)
}