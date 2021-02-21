package com.divelix.skitter.ui.menu.scroll

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.*
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.menu.ModView
import com.divelix.skitter.ui.menu.mod.ModStockTable
import com.divelix.skitter.ui.menu.mod.ShowcaseTable
import com.divelix.skitter.ui.menu.tabs.Tab
import com.divelix.skitter.ui.menu.tabs.TabbedMenu
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ktx.scene2d.*
import ktx.style.get

class ModPage(context: Context, val playerData: PlayerData) : Page(context), ModSelector {
    override var selectedModView: ModView? = null
        set(value) {
            field = value
            showcaseTable.setMod(value)
        }
    private val showcaseTable by lazy { ShowcaseTable(::sellMod, ::upgradeMod) }
    private val tabbedMenu = TabbedMenu(gdxArrayOf(
            Tab(assets.manager.get(Constants.SHIP_ICON), ModStockTable(ModType.SHIP_MOD, playerData.mods, ::selectMod)),
            Tab(assets.manager.get(Constants.GUN_ICON), ModStockTable(ModType.GUN_MOD, playerData.mods, ::selectMod))
    ))

    init {
        table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                right().pad(12f)
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                scaledLabel(this@ModPage.playerData.coins.toString(), style = Constants.STYLE_MOD_NAME)
            }.cell(fillX = true)
            row()
            add(this@ModPage.showcaseTable)
            row()
            add(this@ModPage.tabbedMenu)
        }
    }

    override fun update() {
        // TODO update UI
    }

    private fun sellMod() {
        if (selectedModView == null) return
        val modView = selectedModView as ModView
        val modAlias = modView.modAlias
        if (modAlias.quantity > 1) {
            modAlias.quantity--
        } else {
            playerData.mods.removeValue(modView.modAlias, false)
            selectMod(modView) // deactivates mod
        }
        updateStockTableFor(modAlias.type)
    }

    private fun upgradeMod() {
        if (selectedModView == null) return
        val modView = selectedModView as ModView
        val modAlias = modView.modAlias
        modAlias.level++
        updateStockTableFor(modAlias.type)
        val newModView = when (modAlias.type) { //TODO find elegant solution to this
            ModType.SHIP_MOD -> {
                (tabbedMenu.tabs[0].contentTable as ModStockTable).tableWithMods.children
                        .filter { (it as Container<*>).actor is ModView }
                        .map { (it as Container<*>).actor as ModView }
                        .single {
                            it.modAlias.type == modAlias.type &&
                                    it.modAlias.index == modAlias.index &&
                                    it.modAlias.level == modAlias.level
                        }
            }
            ModType.GUN_MOD -> {
                (tabbedMenu.tabs[1].contentTable as ModStockTable).tableWithMods.children
                        .filter { (it as Container<*>).actor is ModView }
                        .map { (it as Container<*>).actor as ModView }
                        .single {
                            it.modAlias.type == modAlias.type &&
                                    it.modAlias.index == modAlias.index &&
                                    it.modAlias.level == modAlias.level
                        }
            }
        }
        selectMod(newModView)
    }

    private fun updateStockTableFor(modType: ModType) {
        when (modType) {
            ModType.SHIP_MOD -> (tabbedMenu.tabs[0].contentTable as ModStockTable).reloadMods()
            ModType.GUN_MOD -> (tabbedMenu.tabs[1].contentTable as ModStockTable).reloadMods()
        }
    }
}