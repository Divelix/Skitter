package com.divelix.skitter.data.editors

import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.EquipAlias
import com.divelix.skitter.data.ModAlias
import ktx.collections.plusAssign
import ktx.log.error

object EquipEditor {

    fun upgradeEquip(equip: EquipAlias): Boolean {
        return if (equip.level < Constants.EQUIP_MAX_LEVEL) {
            equip.level++
            true
        } else {
            error { "Equip already has max level" }
            false
        }
    }

    fun addModToEquip(mod: ModAlias, equip: EquipAlias): Boolean {
        val duplicate = equip.mods.singleOrNull { it.type == mod.type && it.index == mod.index && it.level == mod.level }
        return if (duplicate == null) {
            equip.mods += mod
            true
        } else {
            error { "Equip already has such mod" }
            false
        }
    }

    fun removeModFromEquip(mod: ModAlias, equip: EquipAlias): Boolean {
        val equipMod = equip.mods.singleOrNull { it.type == mod.type && it.index == mod.index && it.level == mod.level }
        return if (equipMod != null) {
            equip.mods.removeValue(equipMod, false)
            true
        } else {
            error {"Equip cannot remove mod that it doesn't have" }
            false
        }
    }
}