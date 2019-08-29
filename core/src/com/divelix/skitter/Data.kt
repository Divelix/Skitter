package com.divelix.skitter

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

object Data {
    var renderTime = 0f
    var physicsTime = 0f
    val playerData = PlayerData(ShipData(100f, 100f, 10f), GunData(1f, 1f, 1f, 1f, 1f, 1f))
    val dynamicData = DynamicData(Vector2(), 10, Array(10))
}

data class DynamicData(val dirVec: Vector2,
                       var ammo: Int,
                       val aims: Array<Vector2>)

data class PlayerData(val ship: ShipData,
                      var gun: GunData)

data class ShipData(var health: Float,
                    var energy: Float,
                    var armor: Float)

data class GunData(var damage: Float,
                   var capacity: Float,
                   var reloadTime: Float,
                   var bulletSpeed: Float,
                   var critChance: Float,
                   var critMultiplier: Float)
