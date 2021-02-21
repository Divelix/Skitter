package com.divelix.skitter.ui.menu.mod

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModAlias
import com.divelix.skitter.data.ModType
import com.divelix.skitter.ui.menu.ModTable
import com.divelix.skitter.ui.menu.ModView
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.container
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.style.get

class ModStockTable(
        val modType: ModType,
        val modAliases: Array<ModAlias>,
        selectMod: (ModView) -> Unit
): ModTable(selectMod) {
    val tableWithMods: Table

    init {
        pad(Constants.UI_MARGIN)

        scrollPane {
            setScrollingDisabled(true, false)
            setScrollbarsVisible(false)
            container {
                // StockTable
                this@ModStockTable.tableWithMods = table {
                    pad(Constants.UI_PADDING)
                    defaults().pad(Constants.UI_PADDING)
                    // fill with empty cells
                    for (i in 1..16) {
                        container(this@ModStockTable.makeEmptyCell()) {
                            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                        }
                        if (i % 4 == 0) row()
                    }
                }
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            }
        }
        reloadMods()
    }

    fun reloadMods() {
        tableWithMods.children.forEach { (it as Container<*>).actor = makeEmptyCell() }
        val filteredModAliases = modAliases.filter { it.type == modType }
        filteredModAliases.forEachIndexed { i, modAlias ->
            (tableWithMods.children[i] as Container<*>).actor = makeModView(modAlias)
        }
    }
}