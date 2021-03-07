package com.divelix.skitter.ui.menu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModAlias
import ktx.actors.txt
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.container
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.style.get
import ktx.collections.*
import ktx.log.debug

class StockTable(
        private val modAliases: Array<ModAlias>,
        selectMod: (ModView) -> Unit
) : ModTable(selectMod) {
    val tableWithMods: Table

    init {
        scrollPane {
            setScrollingDisabled(true, false)
            setScrollbarsVisible(false)
            container {
                // StockTable
                this@StockTable.tableWithMods = table {
                    name = Constants.STOCK_TABLE
                    pad(Constants.UI_PADDING)
                    defaults().pad(Constants.UI_PADDING)
                    // fill with empty cells
                    for (i in 1..16) {
                        container(this@StockTable.makeEmptyCell()) {
                            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                        }
                        if (i % 4 == 0) row()
                    }
                }
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            }
        }
        reload()
    }

    fun reload() {
        clearAll()
        modAliases.forEach { addModView(it) }
    }

    fun addMod(modAlias: ModAlias): ModView {
        modAliases.add(modAlias)
        val targetContainer = tableWithMods.children.first { (it as Container<*>).actor !is ModView } as Container<*>
        val newModView = makeModView(modAlias)
        targetContainer.actor = newModView
        return newModView
    }

    override fun addModView(modAlias: ModAlias) {
        val targetContainer = tableWithMods.children.first { (it as Container<*>).actor !is ModView } as Container<*>
        val newModView = makeModView(modAlias)
        targetContainer.actor = newModView
    }

    override fun removeModView(modAlias: ModAlias) {
        val modView = tableWithMods.children
                .filter { (it as Container<*>).actor is ModView }
                .map { (it as Container<*>).actor as ModView }
                .single { it.modAlias.index == modAlias.index && it.modAlias.level == modAlias.level }
        if (modView.modAlias.quantity > 1) {
            modView.modAlias.quantity--
            modView.update()
        } else {
            modAliases.removeValue(modAlias, false)
            (modView.parent as Container<*>).actor = makeEmptyCell()
        }
    }

    override fun clearAll() {
        tableWithMods.children.forEach { (it as Container<*>).actor = makeEmptyCell() }
    }

    fun tryMerge(modView: ModView): ModView {
        val modAlias = modView.modAlias
        val mergeCandidates = modAliases
                .filter { it.type == modAlias.type && it.index == modAlias.index && it.level == modAlias.level }
        if (mergeCandidates.size == 1) return modView
        val overallQuantity = mergeCandidates
                .map { it.quantity }
                .reduce { overallQuantity, quantity -> overallQuantity + quantity }
        modAliases.removeAll(mergeCandidates)
        val resultModAlias = ModAlias(modAlias.type, modAlias.index, modAlias.level, overallQuantity)
        modAliases.add(resultModAlias)
        return makeModView(resultModAlias)
    }
}