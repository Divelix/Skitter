package com.divelix.skitter.unittests.remotedata

import com.divelix.skitter.GdxTestRunner
import com.divelix.skitter.data.*
import com.divelix.skitter.data.binders.AliasBinder
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

    @Test
    fun `check addModToEquip adds mod only to present equip`() {
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)
        val equip = EquipAlias(EquipType.SHIP, 11)

        val playerData = PlayerData().apply { equips += equip.copy() }
        val playerDataEditor = PlayerDataEditor(playerData)
        val isAdded = playerDataEditor.addModToEquip(mod.copy(), equip.copy(index = 12))

        Assert.assertFalse(isAdded)
    }

    @Test
    fun `check removeModFromEquip removes mod`() {
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)
        val equip = EquipAlias()
        val expected = PlayerData().apply { equips += equip.copy() }

        val actual = PlayerData().apply { equips += equip.copy().apply { mods += mod.copy() } }
        val playerDataEditor = PlayerDataEditor(actual)
        playerDataEditor.removeModFromEquip(mod, equip.copy())

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `check removeModFromEquip won't remove mod in absent equip`() {
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)
        val equip = EquipAlias().apply { mods += mod.copy() }

        val playerData = PlayerData().apply { equips += equip.copy() }
        val playerDataEditor = PlayerDataEditor(playerData)
        val isRemoved = playerDataEditor.addModToEquip(mod.copy(), equip.copy())

        Assert.assertFalse(isRemoved)
    }

    @Test
    fun `check upgradeMod upgrades mod for coins`() {
        val arbitraryLargeNumber = 100000
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)
        val expectedPrice = arbitraryLargeNumber - AliasBinder.modsData.upgradePrices[mod.level-1]
        val expected = PlayerData(coins = expectedPrice).apply { mods += mod.copy(level = 6) }

        val actual = PlayerData(coins = arbitraryLargeNumber).apply { mods += mod.copy() }
        val playerDataEditor = PlayerDataEditor(actual)
        playerDataEditor.upgradeMod(mod.copy())

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `check upgradeMod won't upgrade mod if there is not enough coins`() {
        val arbitraryLargeNumber = 0
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)

        val playerData = PlayerData(coins = arbitraryLargeNumber).apply { mods += mod.copy() }
        val playerDataEditor = PlayerDataEditor(playerData)
        val isUpgraded = playerDataEditor.upgradeMod(mod.copy())

        Assert.assertFalse(isUpgraded)
    }

    @Test
    fun `check upgradeMod won't upgrade absent mod`() {
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)

        val playerDataEditor = PlayerDataEditor(PlayerData())
        val isUpgraded = playerDataEditor.upgradeMod(mod.copy())

        Assert.assertFalse(isUpgraded)
    }

    @Test
    fun `check upgradeMod works with multiple quantity`() {
        val arbitraryLargeNumber = 100000
        val mod = ModAlias(ModType.SHIP_MOD, 12, 1, 2)
        val expectedPrice = arbitraryLargeNumber - AliasBinder.modsData.upgradePrices[mod.level-1]
        val expected = PlayerData(coins = expectedPrice).apply {
            mods += mod.copy(level = 1, quantity = 1)
            mods += mod.copy(level = 2, quantity = 1)
        }

        val actual = PlayerData(coins = arbitraryLargeNumber).apply { mods += mod.copy() }
        val playerDataEditor = PlayerDataEditor(actual)
        playerDataEditor.upgradeMod(mod.copy())

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `check sellMod removes mod with single quantity`() {
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)
        val sellPrice = AliasBinder.modsData.sellPrices[mod.level-1]
        val expected = PlayerData(coins = sellPrice)

        val actual = PlayerData().apply { mods += mod.copy() }
        val playerDataEditor = PlayerDataEditor(actual)
        playerDataEditor.sellMod(mod.copy())

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `check sellMod decrements mod with multiple quantity`() {
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 2)
        val sellPrice = AliasBinder.modsData.sellPrices[mod.level-1]
        val expected = PlayerData(coins = sellPrice).apply { mods += mod.copy(quantity = mod.quantity-1) }

        val actual = PlayerData().apply { mods += mod.copy() }
        val playerDataEditor = PlayerDataEditor(actual)
        playerDataEditor.sellMod(mod.copy())

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `check sellMod fails for mod quantity less than 1`() {
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 0)

        val playerData = PlayerData().apply { mods += mod.copy() }
        val playerDataEditor = PlayerDataEditor(playerData)
        val isSold = playerDataEditor.sellMod(mod.copy())

        Assert.assertFalse(isSold)
    }
}