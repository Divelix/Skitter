package com.divelix.skitter.data

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import ktx.collections.gdxMapOf

object Data {
    var renderTime = 0f
    var physicsTime = 0f
    var reloadTimer = 0f
    var score = 0
    val matchHistory = gdxMapOf<Enemy, Int>()
    val PLAYER_DATA_OLD: PlayerDataOld

    //    val loverData: LoverData
    val dirVec = Vector2()

    init {
        val shipData = ShipData(0f, 0f)
        val gunData = GunData(0f, 0, 0f, 0f, 0f, 0f)
        PLAYER_DATA_OLD = PlayerDataOld(shipData, gunData)

//        val reader = JsonReader()
//        val enemyReader = reader.parse(Constants.ENEMIES_FILE.toInternalFile())
//        val loverSpecs = enemyReader.get("enemies")[0].get("specs")
//        loverData = LoverData(loverSpecs[0].asFloat(), loverSpecs[1].asFloat(), loverSpecs[2].asFloat(), loverSpecs[3].asFloat())
    }
}

data class PlayerDataOld(val ship: ShipData,
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

//-------------------------------------------- Player Data -----------------------------------------
data class PlayerData(
        var id: Int = -1,
        var name: String = "",
        var coins: Int = 0,
        var activeShip: Int = 0,
        var activeShipSpecs: Array<Float> = Array(),
        var activeGun: Int = 0,
        var activeGunSpecs: Array<Float> = Array(),
        var ships: Array<EquipData> = Array(),
        var guns: Array<EquipData> = Array(),
        var mods: PlayerModsData = PlayerModsData()
)

data class EquipData(
        var index: Int = -1,
        var level: Int = 0,
        var mods: Array<ModAvatarData> = Array()
)

data class ModAvatarData(
        var index: Int = -1,
        var level: Int = 0,
        var quantity: Int = 1
)

data class PlayerModsData(
        var ship: Array<ModAvatarData> = Array(),
        var gun: Array<ModAvatarData> = Array()
)

//--------------------------------------------Equip Data -------------------------------------------
//-------------------------------------------- Mods Data -------------------------------------------