package com.divelix.skitter.data.editors

import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModAlias
import ktx.log.error

object ModEditor {

    fun upgradeMod(mod: ModAlias): Boolean {
        return if (mod.level < Constants.MOD_MAX_LEVEL) {
            mod.level++
            true
        } else {
            error { "Mod can't upgrade as it is already has max level" }
            false
        }
    }

    fun incrementMod(mod: ModAlias): Boolean {
        return if (mod.quantity < Constants.MOD_MAX_QUANTITY) {
            mod.quantity++
            true
        } else {
            error { "Mod can't increment as it is already has max quantity" }
            false
        }
    }

    fun decrementMod(mod: ModAlias): Boolean {
        return if (mod.quantity > 1) {
            mod.quantity--
            true
        } else {
            error { "Mod can't decrement as it is already has quantity == 1" }
            false
        }
    }
}