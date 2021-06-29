package com.divelix.skitter.data.editors

import com.divelix.skitter.data.*
import com.divelix.skitter.data.binders.AliasBinder
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
        if (playerMod == null) {
            error { "No such mod in PlayerData" }
            return false
        } else {
            val upgradePrice = AliasBinder.modsData.upgradePrices[playerMod.level-1]
            return if(playerData.coins < upgradePrice) {
                error { "Not enough money to upgrade" }
                false
            } else { // allowed to upgrade
                playerData.coins -= upgradePrice
                when {
                    playerMod.quantity == 1 -> {
                        ModEditor.upgradeMod(playerMod)
                    }
                    playerMod.quantity > 1 -> {
                        playerData.mods += playerMod.copy(level = playerMod.level + 1, quantity = 1)
                        ModEditor.decrementMod(playerMod)
                    }
                    else -> {
                        error { "Upgrade candidate has quantity < 1" }
                        false
                    }
                }
            }
        }
    }

    fun sellMod(mod: ModAlias): Boolean {
        val playerMod = playerData.mods.singleOrNull { it.type == mod.type && it.index == mod.index && it.level == mod.level }
        if (playerMod == null) {
            error { "No such mod in PlayerData" }
            return false
        } else {
            val sellPrice = AliasBinder.modsData.sellPrices[playerMod.level-1]
            playerData.coins += sellPrice
            return when {
                playerMod.quantity == 1 -> {
                    playerData.mods.removeValue(playerMod, false)
                    true
                }
                playerMod.quantity > 1 -> {
                    ModEditor.decrementMod(playerMod)
                    true
                }
                else -> {
                    error { "Sell candidate has quantity < 1" }
                    false
                }
            }
        }
    }
}