package com.divelix.skitter.data

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.FloatArray
import ktx.collections.GdxFloatArray
import ktx.collections.GdxMap
import ktx.collections.gdxIdentityMapOf
import ktx.collections.gdxMapOf

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

//-------------------------------------------- Ships Data -------------------------------------------
//-------------------------------------------- Guns Data --------------------------------------------
enum class GunType {
    PISTOL,
    SHOTGUN
}

data class GunData(
        var gunType: GunType = GunType.PISTOL,
        var level: Int = 1
)

//-------------------------------------------- Mods Data -------------------------------------------
enum class ModEffectOld(val initValue: Float) {
    // Ship specs
    HEALTH(50f),
    SPEED(10f),

    // Gun specs
    DAMAGE(10f),
    CAPACITY(1f),
    RELOAD(1f),

}

data class ModData(
        var index: Int = -1,
        var name: String = "",
        var effects: Array<ModEffectOld>
)

//----------------------------------------- NEW DATA STRUCTURE --------------------------
//---------------- LOCAL ----------------
//----------- EQUIP -----------
sealed class EquipSpec
data class ShipSpecs(
        val health: Array<Float>,
        val speed: Array<Float>
) : EquipSpec()
data class GunSpecs(
        val damage: Array<Float>,
        val capacity: Array<Float>,
        val reload: Array<Float>,
        val speed: Array<Float>,
        val crit: Array<Float>,
        val chance: Array<Float>
) : EquipSpec()

sealed class Equip
data class Ship(val index: Int, val name: String, val specs: ShipSpecs) : Equip()
data class Gun(val index: Int, val name: String, val specs: GunSpecs) : Equip()

//----------- MOD -----------
sealed class Mod
data class ShipMod(val index: Int = 0, val name: String = "None", val effects: GdxMap<ShipModEffects, GdxFloatArray> = gdxMapOf()) : Mod()
data class GunMod(val index: Int = 0, val name: String = "None", val effects: GdxMap<GunModEffects, GdxFloatArray> = gdxMapOf()) : Mod()

enum class ShipModEffects {
    HealthBooster,
    SpeedBooster
}

enum class GunModEffects {
    DamageBooster
}

//----------- PLAYER -----------
data class Player(
        val id: Int,
        val name: String,
        val coins: Int,
        val activeEquips: ActiveEquips,
        val equips: Equips,
        val mods: Mods
)

data class ActiveEquips(val ship: ActiveEquip, val gun: ActiveEquip)

data class ActiveEquip(val index: Int, val level: Int, val mods: Array<ModAlias>)

data class ModAlias(val index: Int, val level: Int, val quantity: Int)

data class Equips(val ships: Array<EquipAlias>, val guns: Array<EquipAlias>)

data class EquipAlias(val index: Int, val level: Int, val mods: Array<ModAlias>)

data class Mods(val ship: Array<ModAlias>, val gun: Array<ModAlias>)