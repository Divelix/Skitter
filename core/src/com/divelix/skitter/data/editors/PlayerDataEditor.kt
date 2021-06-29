package com.divelix.skitter.data.editors

import com.divelix.skitter.data.EquipAlias
import com.divelix.skitter.data.EquipType
import com.divelix.skitter.data.ModAlias
import com.divelix.skitter.data.PlayerData
import ktx.collections.*
import ktx.log.*

class PlayerDataEditor(private val playerData: PlayerData) {

    fun setActiveShip(index: Int): Boolean {
        val ship = playerData.equips.find { it.type == EquipType.SHIP && it.index == index }
        return if (ship != null) {
            playerData.activeEquips.shipIndex = index
            true
        } else {
            false
        }
    }

    fun setActiveGun(index: Int): Boolean {
        val gun = playerData.equips.find { it.type == EquipType.GUN && it.index == index }
        return if (gun != null) {
            playerData.activeEquips.gunIndex = index
            true
        } else {
            false
        }
    }

    // on mod upgrade in menu or found mod in gameplay
    fun addModToEquip(mod: ModAlias, equip: EquipAlias): Boolean {
        val playerEquip = playerData.equips.singleOrNull {it.type == equip.type && it.index == equip.index}
        return playerEquip != null && EquipEditor.addModToEquip(mod, playerEquip)
    }

    fun removeModFromEquip(mod: ModAlias, equip: EquipAlias): Boolean {
        val playerEquip = playerData.equips.singleOrNull {it.type == equip.type && it.index == equip.index}
        return playerEquip != null && EquipEditor.removeModFromEquip(mod, playerEquip)
    }

    fun upgradeMod(mod: ModAlias): Boolean {
        val playerMod = playerData.mods.singleOrNull { it.type == mod.type && it.index == mod.index && it.level == mod.level }
        return if (playerMod != null && playerMod.quantity == 1) {
            ModEditor.upgradeMod(playerMod)
        } else if (playerMod != null && playerMod.quantity > 1) {
            playerData.mods += playerMod.copy(level = playerMod.level + 1, quantity = 1)
            ModEditor.decrementMod(playerMod)
        } else {
            error { "No such mod in PlayerData or quantity < 1" }
            false
        }
    }

    fun sellMod(mod: ModAlias): Boolean {
        TODO()
    }
}