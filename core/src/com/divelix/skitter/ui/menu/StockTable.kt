package com.divelix.skitter.ui.menu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModAlias
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.container
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.style.get
import ktx.collections.*

// contains all available mods to player
class StockTable(
        canModifyPlayerData: Boolean,
        modAliases: GdxArray<ModAlias>,
        val selectMod: (ModView) -> Unit
) : ModTable(canModifyPlayerData, modAliases, selectMod) {
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
//        addAll()
    }

    override fun addMod(modAlias: ModAlias, modifyData: Boolean): Boolean {
        val resultModAlias = if (modifyData) {
            // find mod aliases with same index and level
            val mergeCandidate = modAliases
                    .singleOrNull { it.index == modAlias.index && it.level == modAlias.level }
            val mergedModAlias = if (mergeCandidate == null) {
                modAlias
            } else {
                val oldModQuantity = mergeCandidate.quantity // save mod quantity before it will be removed
                removeMod(mergeCandidate, true) // remove duplicate
                ModAlias(modAlias.type, modAlias.index, modAlias.level, oldModQuantity + modAlias.quantity)
            }
            modAliases.add(mergedModAlias)
            mergedModAlias
        } else {
            modAlias
        }
        val targetContainer = tableWithMods.children.first { (it as Container<*>).actor !is ModView } as Container<*>
        targetContainer.actor = makeModView(resultModAlias).apply { selectMod(this) }
        return true
    }


    override fun removeMod(modAlias: ModAlias, modifyData: Boolean) {
        val modView = tableWithMods.children
                .filter { (it as Container<*>).actor is ModView }
                .map { (it as Container<*>).actor as ModView }
                .singleOrNull { it.modAlias.index == modAlias.index && it.modAlias.level == modAlias.level }
        if (modView != null) {
            if (modifyData) modAliases.removeValue(modAlias, false)
            (modView.parent as Container<*>).actor = makeEmptyCell()
        }
    }

    fun subtractOneFromSimilarTo(other: ModAlias) {
        val modAlias = modAliases.singleOrNull { it.index == other.index && it.level == other.level }
                ?: return
        if (modAlias.quantity > 1) {
            modAlias.quantity--
            tableWithMods.children
                    .filter { (it as Container<*>).actor is ModView }
                    .map { (it as Container<*>).actor as ModView }
                    .single { it.modAlias.index == modAlias.index && it.modAlias.level == modAlias.level }
                    .update()
        } else {
            removeMod(modAlias, true)
        }
    }
}