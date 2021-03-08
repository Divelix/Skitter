package com.divelix.skitter.ui.menu.scroll

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.SnapshotArray
import com.divelix.skitter.data.*
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.menu.ModView
import com.divelix.skitter.ui.menu.StockTable
import com.divelix.skitter.ui.menu.mod.ShowcaseTable
import com.divelix.skitter.ui.menu.tabs.Tab
import com.divelix.skitter.ui.menu.tabs.TabbedMenu
import com.divelix.skitter.utils.AliasBinder
import ktx.actors.txt
import ktx.collections.filter
import ktx.collections.gdxArrayOf
import ktx.collections.removeAll
import ktx.inject.Context
import ktx.scene2d.*
import ktx.style.get

class ModPage(context: Context, val playerData: PlayerData, val reloadEquipsFor: (ModType) -> Unit) : Page(context), ModSelector {
    override var selectedModView: ModView? = null
        set(value) {
            field = value
            showcaseTable.setMod(value)
        }
    private val coinsLabel: Label
    private val showcaseTable by lazy { ShowcaseTable(::sellMod, ::upgradeMod) }
    private val shipStockTable = StockTable(true, playerData.mods.filter { it.type == ModType.SHIP_MOD }, ::selectMod).apply { padTop(Constants.UI_MARGIN) }
    private val gunStockTable = StockTable(true, playerData.mods.filter { it.type == ModType.GUN_MOD }, ::selectMod).apply { padTop(Constants.UI_MARGIN) }
    private val tabbedMenu = TabbedMenu(gdxArrayOf(
            Tab(assets.manager.get(Constants.SHIP_ICON), shipStockTable),
            Tab(assets.manager.get(Constants.GUN_ICON), gunStockTable)
    ))

    init {
        table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                right().pad(12f)
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                this@ModPage.coinsLabel = scaledLabel(this@ModPage.playerData.coins.toString(), style = Constants.STYLE_MOD_NAME)
            }.cell(fillX = true)
            row()
            add(this@ModPage.showcaseTable)
            row()
            add(this@ModPage.tabbedMenu)
        }
    }

    private fun sellMod() {
        if (selectedModView == null) return
        val modView = selectedModView as ModView
        val modAlias = modView.modAlias
        if (modAlias.quantity > 1) {
            modAlias.quantity--
            modView.update()
        } else {
            when (modAlias.type) {
                ModType.SHIP_MOD -> shipStockTable.removeMod(modAlias, true)
                ModType.GUN_MOD -> gunStockTable.removeMod(modAlias, true)
            }
            selectMod(modView) // deactivates mod
        }
        playerData.coins += AliasBinder.modsData.sellPrices[modAlias.level - 1]
        coinsLabel.txt = playerData.coins.toString()
        reloadEquipsFor(modAlias.type)
    }

    private fun upgradeMod() {
        if (selectedModView == null) return
        val modView = selectedModView as ModView
        val modAlias = modView.modAlias
        val stockTable = when (modAlias.type) {
            ModType.SHIP_MOD -> shipStockTable
            ModType.GUN_MOD -> gunStockTable
        }
        if (modAlias.level == 10) return
        if (modAlias.quantity == 1) {
            modAlias.level++
            modView.update()
            //TODO need to merge here
            selectMod(modView)
        } else {
            modAlias.quantity--
            modView.update()
            stockTable.addMod(modAlias.copy(level = modAlias.level + 1, quantity = 1), true)
            //TODO need to merge here
            //TODO need to select here
        }
        playerData.coins -= AliasBinder.modsData.upgradePrices[modAlias.level - 1]
        coinsLabel.txt = playerData.coins.toString()
        upgradeModInEquips(modAlias)
        reloadEquipsFor(modAlias.type)
    }

    //TODO test this
    private fun upgradeModInEquips(modAlias: ModAlias) {
        playerData.equips.forEach { equip ->
            val upgradeCandidate = equip.mods.singleOrNull { it.type == modAlias.type && it.index == modAlias.index }
            if (upgradeCandidate != null && upgradeCandidate.level < modAlias.level) upgradeCandidate.level = modAlias.level
        }
    }

    // find and select upgraded mod among new ModViews
    private fun selectModViewFor(modAlias: ModAlias) {
        val newModView = when (modAlias.type) {
            ModType.SHIP_MOD -> findModView(modAlias, (tabbedMenu.tabs[0].contentTable as StockTable).tableWithMods.children)
            ModType.GUN_MOD -> findModView(modAlias, (tabbedMenu.tabs[1].contentTable as StockTable).tableWithMods.children)
        }
        selectMod(newModView)
    }

    // find ModView in array of actors via given ModAlias
    private fun findModView(modAlias: ModAlias, actors: SnapshotArray<Actor>) = actors
            .filter { (it as Container<*>).actor is ModView }
            .map { (it as Container<*>).actor as ModView }
            .single {
                it.modAlias.type == modAlias.type &&
                        it.modAlias.index == modAlias.index &&
                        it.modAlias.level == modAlias.level &&
                        it.modAlias.quantity == modAlias.quantity
            }

    // merge mods in player's data for modAlias
    private fun mergeModsFor(modAlias: ModAlias): ModAlias {
        val mergeCandidates = playerData.mods
                .filter { it.type == modAlias.type && it.index == modAlias.index && it.level == modAlias.level }
        if (mergeCandidates.size == 1) return modAlias
        val overallQuantity = mergeCandidates
                .map { it.quantity }
                .reduce { overallQuantity, quantity -> overallQuantity + quantity }
        playerData.mods.removeAll(mergeCandidates)
        val resultModAlias = ModAlias(modAlias.type, modAlias.index, modAlias.level, overallQuantity)
        playerData.mods.add(resultModAlias)
        return resultModAlias
    }
}