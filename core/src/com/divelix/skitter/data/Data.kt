package com.divelix.skitter.data

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.gdxFloatArrayOf
import com.divelix.skitter.gdxIntArrayOf
import ktx.collections.*
import java.util.*

object Data {
    var renderTime = 0f
    var physicsTime = 0f
    var reloadTimer = 0f
    var score = 0
    val matchHistory = gdxMapOf<Enemy, Int>()

    //    val loverData: LoverData
    val dirVec = Vector2()
}

//---------------------------------------------- Local ---------------------------------------------

// ----------- GAMEPLAY ------------
data class ActivePlayerData(
        var shipHealth: Float = 1f,
        var shipSpeed: Float = 1f,
        var gunDamage: Float = 1f,
        var gunCapacity: Int = 1,
        var gunReload: Float = 1f,
        var gunSpeed: Float = 1f,
        var gunCrit: Float = 1f,
        var gunChance: Float = 1f
)

//----------- EQUIP -----------
enum class EquipType {
    SHIP,
    GUN;
    operator fun invoke() = toString().lowercase(Locale.ROOT)
}

data class Equip(
        val type: EquipType = EquipType.SHIP,
        val index: Int = 0,
        val name: String = "None",
        val description: String = "",
        val specs: EquipSpecs = ShipSpecs()
)

sealed class EquipSpecs
data class ShipSpecs(
        val health: GdxFloatArray = gdxFloatArrayOf(),
        val speed: GdxFloatArray = gdxFloatArrayOf()
) : EquipSpecs()

data class GunSpecs(
        val damage: GdxFloatArray = gdxFloatArrayOf(),
        val capacity: GdxFloatArray = gdxFloatArrayOf(),
        val reload: GdxFloatArray = gdxFloatArrayOf(),
        val speed: GdxFloatArray = gdxFloatArrayOf(),
        val crit: GdxFloatArray = gdxFloatArrayOf(),
        val chance: GdxFloatArray = gdxFloatArrayOf()
) : EquipSpecs()

data class EquipsData(
        val equips: GdxArray<Equip> = gdxArrayOf()
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

sealed class ModEffect {
    sealed class ShipModEffect: ModEffect() {
        object HealthBooster : ShipModEffect()
        object SpeedBooster : ShipModEffect()
    }

    sealed class GunModEffect: ModEffect() {
        object DamageBooster : GunModEffect()
        object ReloadBooster : GunModEffect()
    }
}

data class ModsData(
        val sellPrices: GdxIntArray = gdxIntArrayOf(),
        val upgradePrices: GdxIntArray = gdxIntArrayOf(),
        val mods: Array<Mod> = gdxArrayOf()
)

//--------------------------------------------- Remote ---------------------------------------------
//----------- PLAYER -----------
data class PlayerData(
        val id: Int = -1,
        val name: String = "None",
        var coins: Int = 0,
        val activeEquips: ActiveEquips = ActiveEquips(),
        val equips: GdxArray<EquipAlias> = gdxArrayOf(),
        val mods: GdxArray<ModAlias> = gdxArrayOf()
)

data class ActiveEquips(var shipIndex: Int = 0, var gunIndex: Int = 0)

data class EquipAlias(val type: EquipType = EquipType.SHIP, val index: Int = 0, var level: Int = 1, val mods: GdxArray<ModAlias> = gdxArrayOf())

data class ModAlias(val type: ModType = ModType.SHIP_MOD, val index: Int = 0, var level: Int = 1, var quantity: Int = 1)
