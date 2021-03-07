package com.divelix.skitter.ui.menu.equip

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.EquipAlias
import com.divelix.skitter.data.ModAlias
import com.divelix.skitter.ui.menu.ModTable
import com.divelix.skitter.ui.menu.ModView
import ktx.actors.txt
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.container
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.style.get
import ktx.collections.*

class EquipStockTable(
        selectMod: (ModView) -> Unit
) : ModTable(selectMod) {
    private var modAliases: Array<ModAlias> = gdxArrayOf()
    private val tableWithMods: Table

    init {
        scrollPane {
            setScrollingDisabled(true, false)
            setScrollbarsVisible(false)
            container {
                // StockTable
                this@EquipStockTable.tableWithMods = table {
                    name = Constants.STOCK_TABLE
                    pad(Constants.UI_PADDING)
                    defaults().pad(Constants.UI_PADDING)
                    // fill with empty cells
                    for (i in 1..16) {
                        container(this@EquipStockTable.makeEmptyCell()) {
                            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                        }
                        if (i % 4 == 0) row()
                    }
                }
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            }
        }
    }

    fun setFreshReplica(replicaMods: GdxArray<ModAlias>) {
        clearAll()
        modAliases = replicaMods
        modAliases.forEach { addModView(it) }
    }

    fun addMod(modAlias: ModAlias) {
        // try merge mods
        val mergeCandidate = modAliases.singleOrNull { it.index == modAlias.index && it.level == modAlias.level }
        if (mergeCandidate == null) {
            modAliases.add(modAlias)
            addModView(modAlias)
        } else {
            val modView = tableWithMods.children
                    .map { (it as Container<*>).actor }
                    .filter { it is ModView }
                    .map { it as ModView }
                    .single { it.modAlias.index == mergeCandidate.index && it.modAlias.level == mergeCandidate.level }

            modView.modAlias.quantity++
            modView.update()
        }
    }

    fun removeMod(modAlias: ModAlias) {
        modAliases.removeValue(modAlias, false)
        removeModView(modAlias)
    }

    override fun addModView(modAlias: ModAlias) {
        val targetContainer = tableWithMods.children.first { (it as Container<*>).actor !is ModView } as Container<*>
        targetContainer.actor = makeModView(modAlias)
    }

    //  if quantity > 1 {quantity--} else {remove}
    override fun removeModView(modAlias: ModAlias) {
        val modView = tableWithMods.children
                .filter { (it as Container<*>).actor is ModView }
                .map { (it as Container<*>).actor as ModView }
                .single { it.modAlias.index == modAlias.index && it.modAlias.level == modAlias.level }
        if (modView.modAlias.quantity > 1) {
            modView.modAlias.quantity--
            modView.update()
        } else {
            (modView.parent as Container<*>).actor = makeEmptyCell()
        }
    }

    override fun clearAll() {
        tableWithMods.children.forEach { (it as Container<*>).actor = makeEmptyCell() }
    }

    fun subtractEquipMods(equipAlias: EquipAlias) {
        equipAlias.mods.forEach { removeModView(it) }
    }
}