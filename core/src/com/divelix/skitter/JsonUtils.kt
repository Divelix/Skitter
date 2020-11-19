package com.divelix.skitter

import com.badlogic.gdx.utils.FloatArray
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.divelix.skitter.data.GunMod
import com.divelix.skitter.data.GunModEffects
import com.divelix.skitter.data.ShipMod
import com.divelix.skitter.data.ShipModEffects
import ktx.collections.gdxMapOf
import ktx.collections.toGdxArray
import ktx.json.JsonSerializer

class ShipModSerializer: JsonSerializer<ShipMod> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): ShipMod {
        val index = jsonValue[0].asInt()
        val name = jsonValue[1].asString()
        val effects = gdxMapOf<ShipModEffects, FloatArray>()
        jsonValue[2].forEach {
            effects.put(ShipModEffects.valueOf(it.name), it.asFloatArray().toGdxArray())
        }
        return ShipMod(index, name, effects)
    }

    override fun write(json: Json, value: ShipMod, type: Class<*>?) {
        json.run {
            writeObjectStart()
            writeValue(ShipMod::index.name, value.index)
            writeValue(ShipMod::name.name, value.name)
            writeObjectStart(ShipMod::effects.name)
            value.effects.forEach {
                json.writeValue(it.key.name, it.value.toArray())
            }
            writeObjectEnd()
            writeObjectEnd()
        }
    }
}

class GunModSerializer: JsonSerializer<GunMod> {
    override fun read(json: Json, jsonValue: JsonValue, type: Class<*>?): GunMod {
        val index = jsonValue[0].asInt()
        val name = jsonValue[1].asString()
        val effects = gdxMapOf<GunModEffects, FloatArray>()
        jsonValue[2].forEach {
            effects.put(GunModEffects.valueOf(it.name), it.asFloatArray().toGdxArray())
        }
        return GunMod(index, name, effects)
    }

    override fun write(json: Json, value: GunMod, type: Class<*>?) {
        json.run {
            writeObjectStart()
            writeValue(GunMod::index.name, value.index)
            writeValue(GunMod::name.name, value.name)
            writeObjectStart(GunMod::effects.name)
            value.effects.forEach {
                json.writeValue(it.key.name, it.value.toArray())
            }
            writeObjectEnd()
            writeObjectEnd()
        }
    }
}