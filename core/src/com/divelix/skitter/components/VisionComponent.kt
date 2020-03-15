package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectSet

class VisionComponent: Component {
    val visibleEntities = ObjectSet<Entity>(5)
}