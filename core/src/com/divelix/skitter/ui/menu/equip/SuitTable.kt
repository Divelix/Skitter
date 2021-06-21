package com.divelix.skitter.ui.menu.equip

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModAlias
import com.divelix.skitter.ui.menu.ModTable
import com.divelix.skitter.ui.menu.ModView
import ktx.collections.GdxArray
import ktx.collections.filter
import ktx.collections.map
import ktx.log.debug
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.container
import ktx.style.get

// Contain unique mods (by type and index)
class SuitTable(
        canModifyPlayerData: Boolean,
        modAliases: GdxArray<ModAlias>,
        selectMod: (ModView) -> Unit
) : ModTable(canModifyPlayerData, modAliases, selectMod) {

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

    override fun addMod(modAlias: ModAlias, modifyData: Boolean, needSelection: Boolean): Boolean {
        if (modifyData) {
            val overlap = modAliases.firstOrNull { it.index == modAlias.index }
            if (overlap == null) {
                modAliases.add(modAlias.copy(quantity = 1).apply { addView(this) })
            } else {
                debug { "SuitTable already has such mod" }
                return false
            }
        } else {
            addView(modAlias)
        }
        return true
    }

    private fun addView(modAlias: ModAlias) {
        val targetContainer = children.first { (it as Container<*>).actor !is ModView } as Container<*>
        targetContainer.actor = makeModView(modAlias)
    }

    override fun removeMod(modAlias: ModAlias, modifyData: Boolean) {
        val modView = children
                .filter { (it as Container<*>).actor is ModView }
                .map { (it as Container<*>).actor as ModView }
                .singleOrNull { it.modAlias.index == modAlias.index && it.modAlias.level == modAlias.level }
        if (modView != null) (modView.parent as Container<*>).actor = makeEmptyCell()
        if (modifyData) modAliases.removeValue(modAlias, false)
    }
}