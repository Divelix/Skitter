package com.divelix.skitter

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.divelix.skitter.data.*
import ktx.collections.*
import ktx.json.JsonSerializer
import java.util.*

fun gdxFloatArrayOf(vararg elements: Float): GdxFloatArray = GdxFloatArray(elements)

class ModSerializer: JsonSerializer<Mod> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): Mod {
        val modType = ModType.valueOf(jsonValue[0].asString().toUpperCase(Locale.ROOT))
        val index = jsonValue[1].asInt()
        val name = jsonValue[2].asString()
        val effects = gdxMapOf<ModEffect, GdxFloatArray>()
        jsonValue[3].forEach {
            val effectName = if (modType == ModType.SHIP_MOD) {
                when (it.name) {
                    ModEffect.ShipModEffect.HealthBooster::class.simpleName -> ModEffect.ShipModEffect.HealthBooster
                    ModEffect.ShipModEffect.SpeedBooster::class.simpleName -> ModEffect.ShipModEffect.SpeedBooster
                    else -> throw Exception("No such ship mod")
                }
            } else {
                when (it.name) {
                    ModEffect.GunModEffect.DamageBooster::class.simpleName -> ModEffect.GunModEffect.DamageBooster
                    else -> throw Exception("No such gun mod")
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

class ShipSerializer : JsonSerializer<Ship> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): Ship {
        val index = jsonValue[0].asInt()
        val name = jsonValue[1].asString()
        val shipSpecs = ShipSpecs(
                jsonValue[2][0].asFloatArray().toGdxArray(),
                jsonValue[2][1].asFloatArray().toGdxArray()
        )
        return Ship(index, name, shipSpecs)
    }

    override fun write(json: Json, value: Ship, type: Class<*>?) {
        json.run {
            writeObjectStart()
            writeValue(Ship::index.name, value.index)
            writeValue(Ship::name.name, value.name)
            writeObjectStart(Ship::specs.name)
            writeValue(ShipSpecs::health.name, value.specs.health.toArray())
            writeValue(ShipSpecs::speed.name, value.specs.speed.toArray())
            writeObjectEnd()
            writeObjectEnd()
        }
    }
}

class GunSerializer : JsonSerializer<Gun> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): Gun {
        val index = jsonValue[0].asInt()
        val name = jsonValue[1].asString()
        val gunSpecs = GunSpecs(
                jsonValue[2][0].asFloatArray().toGdxArray(),
                jsonValue[2][1].asFloatArray().toGdxArray(),
                jsonValue[2][2].asFloatArray().toGdxArray(),
                jsonValue[2][3].asFloatArray().toGdxArray(),
                jsonValue[2][4].asFloatArray().toGdxArray(),
                jsonValue[2][5].asFloatArray().toGdxArray()
        )
        return Gun(index, name, gunSpecs)
    }

    override fun write(json: Json, value: Gun, type: Class<*>?) {
        json.run {
            writeObjectStart()
            writeValue(Gun::index.name, value.index)
            writeValue(Gun::name.name, value.name)
            writeObjectStart(Gun::specs.name)
            writeValue(GunSpecs::damage.name, value.specs.damage.toArray())
            writeValue(GunSpecs::capacity.name, value.specs.capacity.toArray())
            writeValue(GunSpecs::reload.name, value.specs.reload.toArray())
            writeValue(GunSpecs::speed.name, value.specs.speed.toArray())
            writeValue(GunSpecs::crit.name, value.specs.crit.toArray())
            writeValue(GunSpecs::chance.name, value.specs.chance.toArray())
            writeObjectEnd()
            writeObjectEnd()
        }
    }
}

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