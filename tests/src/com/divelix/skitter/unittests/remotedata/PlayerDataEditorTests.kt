package com.divelix.skitter.unittests.remotedata

import com.divelix.skitter.GdxTestRunner
import com.divelix.skitter.data.*
import com.divelix.skitter.data.editors.PlayerDataEditor
import ktx.collections.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(GdxTestRunner::class)
class PlayerDataEditorTests {

    @Test
    fun `setActiveShip should fail if ship is not in PlayerData equips`() {
        val activeShipIndex = 100500
        val playerDataEditor = PlayerDataEditor(PlayerData())
        val isSet = playerDataEditor.setActiveShip(activeShipIndex)
        Assert.assertFalse(isSet)
    }

    @Test
    fun `setActiveGun should fail if gun is not in PlayerData equips`() {
        val activeGunIndex = 100500
        val playerDataEditor = PlayerDataEditor(PlayerData())
        val isSet = playerDataEditor.setActiveGun(activeGunIndex)
        Assert.assertFalse(isSet)
    }

    @Test
    fun `check addModToEquip adds mod`() {
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)
        val equip = EquipAlias().apply { mods += mod.copy() }
        val expected = PlayerData().apply { equips += equip.copy() }

        val actual = PlayerData().apply { equips += EquipAlias() }
        val playerDataEditor = PlayerDataEditor(actual)
        playerDataEditor.addModToEquip(mod, EquipAlias())

        Assert.assertEquals(expected, actual)
    }

//    @Test
//    fun `check addModToEquip adds mod`() {
//        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)
//        val equip = EquipAlias(EquipType.SHIP, 3, 2)
//
//        val expectedEquip = equip.copy().apply { mods.add(mod.copy()) }
//        val expected = PlayerData().apply { equips += expectedEquip }
//
//        val playerDataEditor = PlayerDataEditor(PlayerData().apply { equips += equip.copy() })
//        val actual = playerDataEditor.playerData
//        playerDataEditor.addModToPlayerEquip(mod.copy(), equip.copy())
//        Assert.assertEquals(expected, actual)
//    }
//
//    @Test
//    fun `check addModToEquip merge mods capability`() {
//        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)
//        val equip = EquipAlias(EquipType.SHIP, 3, 2, gdxArrayOf(mod.copy()))
//
//        val expectedEquip = equip.copy().apply { mods.first().quantity++ }
//        val expected = PlayerData().apply { equips += expectedEquip }
//
//        val playerDataEditor = PlayerDataEditor(PlayerData().apply { equips += equip.copy() })
//        val actual = playerDataEditor.playerData
//        playerDataEditor.addModToPlayerEquip(mod.copy(), equip.copy())
//        Assert.assertEquals(expected, actual)
//
//
//    }
}