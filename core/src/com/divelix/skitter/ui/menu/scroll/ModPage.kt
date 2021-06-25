package com.divelix.skitter.ui.menu.scroll

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
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
import ktx.inject.Context
import ktx.log.debug
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
    private val shipStockTable = StockTable(playerData.mods, playerData.mods.filter { it.type == ModType.SHIP_MOD }, ::selectMod, true).apply { padTop(Constants.UI_MARGIN); addAllViews() }
    private val gunStockTable = StockTable(playerData.mods, playerData.mods.filter { it.type == ModType.GUN_MOD }, ::selectMod, true).apply { padTop(Constants.UI_MARGIN); addAllViews() }
    private val tabbedMenu = TabbedMenu(gdxArrayOf(
            Tab(Scene2DSkin.defaultSkin[RegionName.SHIP_ICON()], shipStockTable),
            Tab(Scene2DSkin.defaultSkin[RegionName.GUN_ICON()], gunStockTable)
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
        val stockTable = when (modAlias.type) {
            ModType.SHIP_MOD -> shipStockTable
            ModType.GUN_MOD -> gunStockTable
        }
        if (modAlias.quantity > 1) {
            modAlias.quantity--
            modView.update()
        } else {
            stockTable.removeMod(modAlias, true)
        }
        selectMod(modView) // deactivates mod
        
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
        val upgradedModAlias = modAlias.copy(level = modAlias.level + 1, quantity = 1)
        if (modAlias.quantity == 1) {
            stockTable.removeMod(modAlias, true)
        } else {
            modAlias.quantity--
            modView.update()
        }
        stockTable.addMod(upgradedModAlias, true)

        playerData.coins -= AliasBinder.modsData.upgradePrices[modAlias.level - 1]
        coinsLabel.txt = playerData.coins.toString()
        reloadEquipsFor(modAlias.type)
    }
}