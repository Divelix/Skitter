package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.Data

class PlayerComponent: Component, Pool.Poolable {
    var health = Data.playerData.ship.health

    override fun reset() {
        health = Data.playerData.ship.health
    }
}
