package com.divelix.skitter.unittests.remotedata

import com.divelix.skitter.GdxTestRunner
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.EquipAlias
import com.divelix.skitter.data.ModAlias
import com.divelix.skitter.data.ModType
import com.divelix.skitter.data.editors.EquipEditor
import ktx.collections.plusAssign
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(GdxTestRunner::class) // for `error {"..."}` messages
class EquipEditorTests {

    @Test
    fun `check if upgradeEquip didn't exceed max level`() {
        val equip = EquipAlias(level = Constants.EQUIP_MAX_LEVEL)
        val isExceed = EquipEditor.upgradeEquip(equip)
        Assert.assertFalse(isExceed)
    }

    @Test
    fun `check if addModToEquip adds mod`() {
        val mod = ModAlias()
        val expected = EquipAlias().apply { mods += mod.copy() }

        val actual = EquipAlias()
        EquipEditor.addModToEquip(mod.copy(), actual)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `check if addModToEquip check duplicate`() {
        val mod = ModAlias(ModType.SHIP_MOD, 12, 5, 1)
        val expected = EquipAlias().apply { mods += mod.copy() }

        val actual = expected.copy()
        val isSet = EquipEditor.addModToEquip(mod.copy(), actual)

        Assert.assertFalse(isSet)
    }

    @Test
    fun `check if removeModFromEquip can remove mod`() {
        val mod = ModAlias()
        val expected = EquipAlias()

        val actual = EquipAlias().apply { mods += mod.copy() }
        EquipEditor.removeModFromEquip(mod.copy(), actual)

        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `check if removeModFromEquip returns false if it can't remove mod`() {
        val mod = ModAlias()
        val isRemoved = EquipEditor.removeModFromEquip(mod.copy(), EquipAlias())
        Assert.assertFalse(isRemoved)
    }
}