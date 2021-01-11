package com.divelix.skitter.data

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IntArray
import com.divelix.skitter.gdxFloatArrayOf
import ktx.collections.*
import java.util.*

object Data {
    var renderTime = 0f
    var physicsTime = 0f
    var reloadTimer = 0f
    var score = 0
    val matchHistory = gdxMapOf<Enemy, Int>()
    val playerDataOld: PlayerDataOld

    //    val loverData: LoverData
    val dirVec = Vector2()

    init {
        val shipData = ShipDataOld(0f, 0f)
        val gunData = GunDataOld(0f, 0, 0f, 0f, 0f, 0f)
        playerDataOld = PlayerDataOld(shipData, gunData)

//        val reader = JsonReader()
//        val enemyReader = reader.parse(Constants.ENEMIES_FILE.toInternalFile())
//        val loverSpecs = enemyReader.get("enemies")[0].get("specs")
//        loverData = LoverData(loverSpecs[0].asFloat(), loverSpecs[1].asFloat(), loverSpecs[2].asFloat(), loverSpecs[3].asFloat())
    }
}

data class PlayerDataOld(val shipOld: ShipDataOld,
                         val gunOld: GunDataOld)

data class ShipDataOld(var health: Float,
                       var speed: Float)

data class GunDataOld(var damage: Float,
                      var capacity: Int,
                      var reloadTime: Float,
                      var bulletSpeed: Float,
                      var critMultiplier: Float,
                      var critChance: Float)

data class LoverData(var health: Float,
                     var maxSpeed: Float,
                     var maxForce: Float,
                     var damage: Float)

//----------------------------------------- NEW DATA STRUCTURE -------------------------------------
//---------------- Local ----------------
//----------- EQUIP -----------
sealed class EquipSpec
data class ShipSpecs(
        val health: GdxFloatArray = GdxFloatArray(),
        val speed: GdxFloatArray = GdxFloatArray()
) : EquipSpec()

data class GunSpecs(
        val damage: GdxFloatArray = GdxFloatArray(),
        val capacity: GdxFloatArray = GdxFloatArray(),
        val reload: GdxFloatArray = GdxFloatArray(),
        val speed: GdxFloatArray = GdxFloatArray(),
        val crit: GdxFloatArray = GdxFloatArray(),
        val chance: GdxFloatArray = GdxFloatArray()
) : EquipSpec()

sealed class Equip
data class Ship(
        val index: Int = 0,
        val name: String = "None",
        val specs: ShipSpecs = ShipSpecs()
) : Equip()

data class Gun(
        val index: Int = 0,
        val name: String = "None",
        val specs: GunSpecs = GunSpecs()
) : Equip()

data class GunsData(
        val specs: Array<String> = gdxArrayOf(),
        val guns: Array<Gun> = gdxArrayOf()
)

data class ShipsData(
        val specs: Array<String> = gdxArrayOf(),
        val ships: Array<Ship> = gdxArrayOf()
)

//----------- MOD -----------
enum class ModType {
    SHIP_MOD,
    GUN_MOD;
    operator fun invoke() = toString().toLowerCase(Locale.ROOT)
}

data class Mod(
        val type: ModType = ModType.SHIP_MOD,
        val index: Int = 0,
        val name: String = "None",
        val effects: GdxMap<ModEffect, GdxFloatArray> = gdxMapOf()
)

data class A(
        val floats1: GdxFloatArray = gdxFloatArrayOf(),
        val floats2: GdxFloatArray = gdxFloatArrayOf(),
)

sealed class ModEffect {
    sealed class ShipModEffect: ModEffect() {
        object HealthBooster : ShipModEffect()
        object SpeedBooster : ShipModEffect()
    }

    sealed class GunModEffect: ModEffect() {
        object DamageBooster : GunModEffect()
    }
}

data class ModsData(
        val sellPrices: GdxIntArray = IntArray(),
        val upgradePrices: GdxIntArray = IntArray(),
        val mods: Array<Mod> = gdxArrayOf()
)

//----------------- Remote -----------------
//----------- PLAYER -----------
data class Player(
        val id: Int = -1,
        val name: String = "None",
        val coins: Int = 0,
        val activeEquips: ActiveEquips = ActiveEquips(),
        val equips: Equips = Equips(),
        val mods: Array<ModAlias> = gdxArrayOf()
)

data class ActiveEquips(val ship: ActiveEquip = ActiveEquip(), val gun: ActiveEquip = ActiveEquip())

data class ActiveEquip(val index: Int = 0, val level: Int = 0, val mods: Array<ModAlias> = gdxArrayOf())

data class ModAlias(val type: ModType = ModType.SHIP_MOD, val index: Int = 0, val level: Int = 0, val quantity: Int = 0)

data class EquipAlias(val index: Int = 0, val level: Int = 0, val mods: Array<ModAlias> = gdxArrayOf())

data class Equips(val ships: Array<EquipAlias> = gdxArrayOf(), val guns: Array<EquipAlias> = gdxArrayOf())
