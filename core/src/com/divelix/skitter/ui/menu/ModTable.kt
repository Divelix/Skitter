package com.divelix.skitter.ui.menu

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModAlias
import ktx.scene2d.KTable

open class ModTable(private val selectMod: (ModView) -> Unit) : Table(), KTable {

    fun makeModView(modAlias: ModAlias) = ModView(modAlias, selectMod)

    fun makeEmptyCell() = Actor().apply {
        setSize(Constants.MOD_SIZE, Constants.MOD_SIZE)
    }
}