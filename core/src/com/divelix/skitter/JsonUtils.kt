package com.divelix.skitter

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.divelix.skitter.data.*
import ktx.collections.*
import ktx.json.JsonSerializer
import ktx.json.fromJson
import java.util.*

class EquipAliasSerializer: JsonSerializer<EquipAlias> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): EquipAlias {
        val modType = EquipType.valueOf(jsonValue[0].asString().toUpperCase(Locale.ROOT))
        val index = jsonValue[1].asInt()
        val level = jsonValue[2].asInt()
        val mods = gdxArrayOf<ModAlias>()
        jsonValue[3].forEach {
            mods.add(json.fromJson<ModAlias>(it.toString()))
        }
        require (
                when (modType) {
                    EquipType.SHIP -> mods.all { it.type == ModType.SHIP_MOD }
                    EquipType.GUN -> mods.all { it.type == ModType.GUN_MOD }
                }
        ) { "Equip deserialization error: all mod types should match equip type" }
        return EquipAlias(modType, index, level, mods)
    }

    override fun write(json: Json, value: EquipAlias, type: Class<*>?) {
        require (
            when (value.type) {
                EquipType.SHIP -> value.mods.all { it.type == ModType.SHIP_MOD }
                EquipType.GUN -> value.mods.all { it.type == ModType.GUN_MOD }
            }
        ) { "Equip serialization error: all mod types should match equip type" }
        json.run {
            writeObjectStart()
            writeValue("type", value.type())
            writeValue("index", value.index)
            writeValue("level", value.level)
            writeArrayStart("mods")
            value.mods.forEach { writeValue(it) }
            writeArrayEnd()
            writeObjectEnd()
        }
    }
}

class EquipSerializer: JsonSerializer<Equip> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): Equip {
        val equipType = EquipType.valueOf(jsonValue[0].asString().toUpperCase(Locale.ROOT))
        val index = jsonValue[1].asInt()
        val name = jsonValue[2].asString()
        val description = jsonValue[3].asString()
        val specs = when (equipType) {
            EquipType.SHIP -> ShipSpecs(
                    GdxFloatArray(jsonValue[4][0].asFloatArray()),
                    GdxFloatArray(jsonValue[4][1].asFloatArray())
            )
            EquipType.GUN -> GunSpecs(
                    GdxFloatArray(jsonValue[4][0].asFloatArray()),
                    GdxFloatArray(jsonValue[4][1].asFloatArray()),
                    GdxFloatArray(jsonValue[4][2].asFloatArray()),
                    GdxFloatArray(jsonValue[4][3].asFloatArray()),
                    GdxFloatArray(jsonValue[4][4].asFloatArray()),
                    GdxFloatArray(jsonValue[4][5].asFloatArray())
            )
        }
        return Equip(equipType, index, name, description, specs)
    }

    override fun write(json: Json, value: Equip, type: Class<*>?) {
        json.run {
            writeObjectStart()
            writeValue("type", value.type())
            writeValue("index", value.index)
            writeValue("name", value.name)
            writeValue("description", value.description)
            writeObjectStart("specs")
            when (value.type) {
                EquipType.SHIP -> {
                    val shipSpecs = value.specs as ShipSpecs
                    writeValue("health", shipSpecs.health)
                    writeValue("speed", shipSpecs.speed)
                }
                EquipType.GUN -> {
                    val gunSpecs = value.specs as GunSpecs
                    writeValue("damage", gunSpecs.damage)
                    writeValue("capacity", gunSpecs.capacity)
                    writeValue("reload", gunSpecs.reload)
                    writeValue("speed", gunSpecs.speed)
                    writeValue("crit", gunSpecs.crit)
                    writeValue("chance", gunSpecs.chance)
                }
            }
            writeObjectEnd()
            writeObjectEnd()
        }
    }
}

class ModAliasSerializer: JsonSerializer<ModAlias> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): ModAlias {
        val modType = ModType.valueOf(jsonValue[0].asString().toUpperCase(Locale.ROOT))
        val index = jsonValue[1].asInt()
        val level = jsonValue[2].asInt()
        val quantity = jsonValue[3].asInt()
        return ModAlias(modType, index, level, quantity)
    }

    override fun write(json: Json, value: ModAlias, type: Class<*>?) {
        json.run {
            writeObjectStart()
            writeValue("type", value.type())
            writeValue("index", value.index)
            writeValue("level", value.level)
            writeValue("quantity", value.quantity)
            writeObjectEnd()
        }
    }
}

class ModSerializer: JsonSerializer<Mod> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): Mod {
        val modType = ModType.valueOf(jsonValue[0].asString().toUpperCase(Locale.ROOT))
        val index = jsonValue[1].asInt()
        val name = jsonValue[2].asString()
        val effects = gdxMapOf<ModEffect, GdxFloatArray>()
        jsonValue[3].forEach {
            val effectName = when (modType) {
                ModType.SHIP_MOD -> when (it.name) {
                    ModEffect.ShipModEffect.HealthBooster::class.simpleName -> ModEffect.ShipModEffect.HealthBooster
                    ModEffect.ShipModEffect.SpeedBooster::class.simpleName -> ModEffect.ShipModEffect.SpeedBooster
                    else -> throw Exception("No such ship mod effect")
                }
                ModType.GUN_MOD -> when (it.name) {
                    ModEffect.GunModEffect.DamageBooster::class.simpleName -> ModEffect.GunModEffect.DamageBooster
                    else -> throw Exception("No such gun mod effect")
                }
            }
            effects[effectName] = GdxFloatArray(it.asFloatArray())
        }
        return Mod(modType, index, name, effects)
    }

    override fun write(json: Json, value: Mod, type: Class<*>?) {
        json.run {
            writeObjectStart()
            writeValue("type", value.type())
            writeValue("index", value.index)
            writeValue("name", value.name)
            writeObjectStart("effects")
            value.effects.forEach {
                writeValue(it.key::class.simpleName, it.value)
            }
            writeObjectEnd()
            writeObjectEnd()
        }
    }
}

//class ShipSerializer : JsonSerializer<Ship> {
//    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): Ship {
//        val index = jsonValue[0].asInt()
//        val name = jsonValue[1].asString()
//        val shipSpecs = ShipSpecs(
//                jsonValue[2][0].asFloatArray().toGdxArray(),
//                jsonValue[2][1].asFloatArray().toGdxArray()
//        )
//        return Ship(index, name, shipSpecs)
//    }
//
//    override fun write(json: Json, value: Ship, type: Class<*>?) {
//        json.run {
//            writeObjectStart()
//            writeValue(Ship::index.name, value.index)
//            writeValue(Ship::name.name, value.name)
//            writeObjectStart(Ship::specs.name)
//            writeValue(ShipSpecs::health.name, value.specs.health.toArray())
//            writeValue(ShipSpecs::speed.name, value.specs.speed.toArray())
//            writeObjectEnd()
//            writeObjectEnd()
//        }
//    }
//}
//
//class GunSerializer : JsonSerializer<Gun> {
//    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): Gun {
//        val index = jsonValue[0].asInt()
//        val name = jsonValue[1].asString()
//        val gunSpecs = GunSpecs(
//                jsonValue[2][0].asFloatArray().toGdxArray(),
//                jsonValue[2][1].asFloatArray().toGdxArray(),
//                jsonValue[2][2].asFloatArray().toGdxArray(),
//                jsonValue[2][3].asFloatArray().toGdxArray(),
//                jsonValue[2][4].asFloatArray().toGdxArray(),
//                jsonValue[2][5].asFloatArray().toGdxArray()
//        )
//        return Gun(index, name, gunSpecs)
//    }
//
//    override fun write(json: Json, value: Gun, type: Class<*>?) {
//        json.run {
//            writeObjectStart()
//            writeValue(Gun::index.name, value.index)
//            writeValue(Gun::name.name, value.name)
//            writeObjectStart(Gun::specs.name)
//            writeValue(GunSpecs::damage.name, value.specs.damage.toArray())
//            writeValue(GunSpecs::capacity.name, value.specs.capacity.toArray())
//            writeValue(GunSpecs::reload.name, value.specs.reload.toArray())
//            writeValue(GunSpecs::speed.name, value.specs.speed.toArray())
//            writeValue(GunSpecs::crit.name, value.specs.crit.toArray())
//            writeValue(GunSpecs::chance.name, value.specs.chance.toArray())
//            writeObjectEnd()
//            writeObjectEnd()
//        }
//    }
//}

//class ShipModSerializer : JsonSerializer<ShipMod> {
//    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): ShipMod {
//        val index = jsonValue[0].asInt()
//        val name = jsonValue[1].asString()
//        val effects = gdxMapOf<ShipModEffects, FloatArray>()
//        jsonValue[2].forEach {
//            effects.put(ShipModEffects.valueOf(it.name), it.asFloatArray().toGdxArray())
//        }
//        return ShipMod(index, name, effects)
//    }
//
//    override fun write(json: Json, value: ShipMod, type: Class<*>?) {
//        json.run {
//            writeObjectStart()
//            writeValue(ShipMod::index.name, value.index)
//            writeValue(ShipMod::name.name, value.name)
//            writeObjectStart(ShipMod::effects.name)
//            value.effects.forEach {
//                json.writeValue(it.key.name, it.value.toArray())
//            }
//            writeObjectEnd()
//            writeObjectEnd()
//        }
//    }
//}
//
//class GunModSerializer : JsonSerializer<GunMod> {
//    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): GunMod {
//        val index = jsonValue[0].asInt()
//        val name = jsonValue[1].asString()
//        val effects = gdxMapOf<GunModEffects, FloatArray>()
//        jsonValue[2].forEach {
//            effects.put(GunModEffects.valueOf(it.name), it.asFloatArray().toGdxArray())
//        }
//        return GunMod(index, name, effects)
//    }
//
//    override fun write(json: Json, value: GunMod, type: Class<*>?) {
//        json.run {
//            writeObjectStart()
//            writeValue(GunMod::index.name, value.index)
//            writeValue(GunMod::name.name, value.name)
//            writeObjectStart(GunMod::effects.name)
//            value.effects.forEach {
//                json.writeValue(it.key.name, it.value.toArray())
//            }
//            writeObjectEnd()
//            writeObjectEnd()
//        }
//    }
//}

class GdxIntArraySerializer : JsonSerializer<GdxIntArray> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): GdxIntArray {
        return GdxIntArray(jsonValue.asIntArray())
    }

    override fun write(json: Json, value: GdxIntArray, type: Class<*>?) {
        json.run {
            writeArrayStart()
            value.toArray().forEach { writeValue(it) }
            writeArrayEnd()
        }
    }
}

class GdxFloatArraySerializer : JsonSerializer<GdxFloatArray> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): GdxFloatArray {
        return GdxFloatArray(jsonValue.asFloatArray())
    }

    override fun write(json: Json, value: GdxFloatArray, type: Class<*>?) {
        json.run {
            writeArrayStart()
            value.toArray().forEach { writeValue(it) }
            writeArrayEnd()
        }
    }
}