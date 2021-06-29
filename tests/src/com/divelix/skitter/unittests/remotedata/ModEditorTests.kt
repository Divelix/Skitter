package com.divelix.skitter.unittests.remotedata

import com.divelix.skitter.GdxTestRunner
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModAlias
import com.divelix.skitter.data.editors.ModEditor
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(GdxTestRunner::class) // for `error {"..."}` messages
class ModEditorTests {

    @Test
    fun `check if upgradeMod don't exceed max level`() {
        val mod = ModAlias(level = Constants.MOD_MAX_LEVEL)
        val isAllowed = ModEditor.upgradeMod(mod)
        Assert.assertFalse(isAllowed)
    }

    @Test
    fun `check if upgradeMod don't affect multiple mods`() {
        val mod = ModAlias(quantity = 2)
        val isAllowed = ModEditor.upgradeMod(mod)
        Assert.assertFalse(isAllowed)
    }

    @Test
    fun `check if incrementMod don't exceed max quantity`() {
        val mod = ModAlias(quantity = Constants.MOD_MAX_QUANTITY)
        val isAllowed = ModEditor.incrementMod(mod)
        Assert.assertFalse(isAllowed)
    }

    @Test
    fun `check if decrementMod don't subtract from 1`() {
        val mod = ModAlias(quantity = 1)
        val isAllowed = ModEditor.decrementMod(mod)
        Assert.assertFalse(isAllowed)
    }
}