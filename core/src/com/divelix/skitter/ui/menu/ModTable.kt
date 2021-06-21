package com.divelix.skitter.ui.menu

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModAlias
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.scene2d.KTable

abstract class ModTable(
        private val canModifyPlayerData: Boolean,
        mods: GdxArray<ModAlias>,
        private val selectMod: (ModView) -> Unit
) : Table(), KTable {
    // TODO ponder on that line (lateinit is not cool here)
    lateinit var modAliases: GdxArray<ModAlias> private set

    init { setModAliases(mods) }

    abstract fun addMod(modAlias: ModAlias, modifyData: Boolean, needSelection: Boolean = true): Boolean

    abstract fun removeMod(modAlias: ModAlias, modifyData: Boolean)

    fun setModAliases(newModAliases: GdxArray<ModAlias>) {
        modAliases = if (canModifyPlayerData) {
            newModAliases
        } else {
            val replica = gdxArrayOf<ModAlias>()
            newModAliases.forEach { replica.add(it.copy()) }
            replica
        }
    }

    fun makeModView(modAlias: ModAlias) = ModView(modAlias, selectMod)

    fun makeEmptyCell() = Actor().apply { setSize(Constants.MOD_SIZE, Constants.MOD_SIZE) }

    fun addAllViews() = modAliases.forEach { addMod(it, false) }

    fun removeAllViews() = modAliases.forEach { removeMod(it, false) }
}