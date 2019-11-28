package com.divelix.skitter

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import ktx.assets.toLocalFile

object Data {
    var renderTime = 0f
    var physicsTime = 0f
    var reloadTimer = 0f
    var enemiesCount = 0
    var score = 0
    val playerData: PlayerData
    val dynamicData: DynamicData

    init {
        val playerReader = JsonReader().parse("json/player_data.json".toLocalFile())
        val shipSpecs = playerReader.get("active_ship_specs")
        val shipData = ShipData(shipSpecs[0].asFloat(), shipSpecs[1].asFloat())
        val gunSpecs = playerReader.get("active_gun_specs")
        val gunData = GunData(gunSpecs[0].asFloat(), gunSpecs[1].asInt(), gunSpecs[2].asFloat(), gunSpecs[3].asFloat(), gunSpecs[4].asFloat(), gunSpecs[5].asFloat())
        playerData = PlayerData(shipData, gunData)
        dynamicData = DynamicData(Vector2(), Array(10))
    }
}

data class DynamicData(val dirVec: Vector2,
                       val aims: Array<Vector2>)

data class PlayerData(val ship: ShipData,
                      var gun: GunData)

data class ShipData(var health: Float,
                    var speed: Float)

data class GunData(var damage: Float,
                   var capacity: Int,
                   var reloadTime: Float,
                   var bulletSpeed: Float,
                   var critChance: Float,
                   var critMultiplier: Float)
