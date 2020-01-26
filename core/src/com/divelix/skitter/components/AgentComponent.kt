package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectSet

class AgentComponent: Component { // VisionComponent name makes more sense
    val visibleEntities = ObjectSet<Entity>(5)
}