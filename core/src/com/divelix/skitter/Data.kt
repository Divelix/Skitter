package com.divelix.skitter

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.JsonReader
import ktx.assets.toInternalFile

object Data {
    var renderTime = 0f
    var physicsTime = 0f
    var reloadTimer = 0f
    var enemiesCount = 0
    var score = 0
    val playerData: PlayerData
    val loverData: LoverData
    val dirVec = Vector2()

    init {
        val reader = JsonReader()
        val playerReader = reader.parse(Constants.PLAYER_FILE.toInternalFile())
        val shipSpecs = playerReader.get("active_ship_specs")
        val shipData = ShipData(shipSpecs[0].asFloat(), shipSpecs[1].asFloat())
        val gunSpecs = playerReader.get("active_gun_specs")
        val gunData = GunData(gunSpecs[0].asFloat(), gunSpecs[1].asInt(), gunSpecs[2].asFloat(), gunSpecs[3].asFloat(), gunSpecs[4].asFloat(), gunSpecs[5].asFloat())
        playerData = PlayerData(shipData, gunData)
        val enemyReader = reader.parse(Constants.ENEMIES_FILE.toInternalFile())
        val loverSpecs = enemyReader.get("enemies")[0].get("specs")
        loverData = LoverData(loverSpecs[0].asFloat(), loverSpecs[1].asFloat(), loverSpecs[2].asFloat(), loverSpecs[3].asFloat())
    }
}

data class PlayerData(val ship: ShipData,
                      var gun: GunData)

data class ShipData(var health: Float,
                    var speed: Float)

data class GunData(var damage: Float,
                   var capacity: Int,
                   var reloadTime: Float,
                   var bulletSpeed: Float,
                   var critMultiplier: Float,
                   var critChance: Float)

data class LoverData(var health: Float,
                     var maxSpeed: Float,
                     var maxForce: Float,
                     var damage: Float)
