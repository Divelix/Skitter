package com.divelix.skitter.data

import com.badlogic.gdx.math.Vector2
import ktx.collections.gdxMapOf

object Data {
    var renderTime = 0f
    var physicsTime = 0f
    var reloadTimer = 0f
    var score = 0
    val matchHistory = gdxMapOf<Enemy, Int>()
    val playerData: PlayerData
//    val loverData: LoverData
    val dirVec = Vector2()

    init {
        val shipData = ShipData(0f, 0f)
        val gunData = GunData(0f, 0, 0f, 0f, 0f, 0f)
        playerData = PlayerData(shipData, gunData)

//        val reader = JsonReader()
//        val enemyReader = reader.parse(Constants.ENEMIES_FILE.toInternalFile())
//        val loverSpecs = enemyReader.get("enemies")[0].get("specs")
//        loverData = LoverData(loverSpecs[0].asFloat(), loverSpecs[1].asFloat(), loverSpecs[2].asFloat(), loverSpecs[3].asFloat())
    }
}

data class PlayerData(val ship: ShipData,
                      val gun: GunData)

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
