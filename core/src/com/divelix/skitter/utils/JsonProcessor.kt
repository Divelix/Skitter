package com.divelix.skitter.utils

import com.badlogic.gdx.utils.Json
import com.divelix.skitter.*
import ktx.json.setSerializer

object JsonProcessor : Json() {
    init {
        setUsePrototypes(false) // to not erase default values (false, 0)
        setSerializer(GdxIntArraySerializer())
        setSerializer(GdxFloatArraySerializer())
        // Player data (remote)
        setSerializer(EquipAliasSerializer())
        setSerializer(ModAliasSerializer())
        // Game data (local)
        setSerializer(EquipSerializer())
        setSerializer(ModSerializer())
    }
}