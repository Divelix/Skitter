package com.divelix.skitter.unittests.misc

import com.divelix.skitter.data.EquipAlias
import com.divelix.skitter.data.EquipType
import com.divelix.skitter.data.ModAlias
import com.divelix.skitter.data.ModType
import ktx.collections.gdxArrayOf
import org.junit.Assert
import org.junit.Test

class ArrayTests {

    @Test
    fun `Check identity removal`() {
        val actual = EquipAlias(EquipType.SHIP, 1, 1, gdxArrayOf(
                ModAlias(ModType.SHIP_MOD, 1, 1),
                ModAlias(ModType.SHIP_MOD, 2, 1),
                ModAlias(ModType.SHIP_MOD, 3, 1)
        ))

        actual.mods.removeValue(ModAlias(ModType.SHIP_MOD, 3, 1), false)

        val expected = EquipAlias(EquipType.SHIP, 1, 1, gdxArrayOf(
                ModAlias(ModType.SHIP_MOD, 1, 1),
                ModAlias(ModType.SHIP_MOD, 2, 1)
        ))
        Assert.assertEquals(expected, actual)
    }
}