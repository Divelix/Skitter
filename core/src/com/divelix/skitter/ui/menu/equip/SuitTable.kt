package com.divelix.skitter.ui.menu.equip

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.EquipAlias
import com.divelix.skitter.data.ModAlias
import com.divelix.skitter.ui.menu.ModTable
import com.divelix.skitter.ui.menu.ModView
import ktx.collections.GdxArray
import ktx.collections.filter
import ktx.collections.gdxArrayOf
import ktx.collections.map
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.container
import ktx.style.get

// Contain unique mods (by type and index)
class SuitTable(selectMod: (ModView) -> Unit): ModTable(selectMod) {
    var modAliases: GdxArray<ModAlias> = gdxArrayOf()

    init {
        name = Constants.SUIT_TABLE
        pad(0f, Constants.UI_PADDING, Constants.UI_PADDING, Constants.UI_PADDING)
        defaults().pad(Constants.UI_PADDING)
        for (i in 1..8) {
            container(makeEmptyCell()) {
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            }
            if (i % 4 == 0) row()
        }
    }

    fun reloadFor(equipAlias: EquipAlias) {
        clearAll()
        modAliases = equipAlias.mods
        modAliases.forEach { addModView(it) }
    }

    fun addMod(modAlias: ModAlias): Boolean {
        // Check mod index duplicate
        val overlapModAlias = children
                .filter { (it as Container<*>).actor is ModView }
                .map { ((it as Container<*>).actor as ModView).modAlias }
                .singleOrNull {
                    it.type == modAlias.type && it.index == modAlias.index
                }
        if (overlapModAlias == null) {
            val newModAlias = modAlias.copy(quantity = 1)
            modAliases.add(newModAlias)
            addModView(newModAlias)
        } else {
            ktx.log.debug { "SuitTable already has such mod" }
            return false
        }
        return true
    }

    fun removeMod(modAlias: ModAlias) {
        modAliases.removeValue(modAlias, false)
        removeModView(modAlias)
    }

    override fun addModView(modAlias: ModAlias) {
        val targetContainer = children.first { (it as Container<*>).actor !is ModView } as Container<*>
        targetContainer.actor = makeModView(modAlias)
    }

    override fun removeModView(modAlias: ModAlias) {
        val modView = children
                .filter { (it as Container<*>).actor is ModView }
                .map { (it as Container<*>).actor as ModView }
                .single { it.modAlias.index == modAlias.index && it.modAlias.level == modAlias.level }

        (modView.parent as Container<*>).actor = makeEmptyCell()
    }

    override fun clearAll() {
        children.forEach { (it as Container<*>).actor = makeEmptyCell() }
    }
}