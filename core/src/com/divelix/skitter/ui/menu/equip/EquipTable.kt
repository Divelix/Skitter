package com.divelix.skitter.ui.menu.equip

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.*
import com.divelix.skitter.ui.menu.scroll.ModSelector
import com.divelix.skitter.ui.menu.ModView
import com.divelix.skitter.ui.menu.StockTable
import ktx.collections.*
import ktx.scene2d.*
import ktx.style.get

class EquipTable(
        private val equipType: EquipType,
        private val playerData: PlayerData,
        private val activePlayerData: ActivePlayerData
) : Table(), KTable, ModSelector {
    override var selectedModView: ModView? = null
        set(value) {
            field = value
            actionButton.showFor(value)
        }

    private var selectedEquipAlias: EquipAlias = fetchActiveEquipAlias(equipType)
        set(value) {
            field = value
            setActiveEquip(value)
        }

    private val modType = when (equipType) {
        EquipType.SHIP -> ModType.SHIP_MOD
        EquipType.GUN -> ModType.GUN_MOD
    }
    private val infoTable by lazy { InfoTable(activePlayerData, ::showEquipWindow) }
    private val suitTable by lazy { SuitTable(true, selectedEquipAlias.mods, ::selectMod) }
    private val stockTable by lazy { StockTable(false, playerData.mods, playerData.mods.filter { it.type == modType }, ::selectMod) }
    private val equipWindow by lazy { EquipWindow(playerData.equips.filter { it.type == equipType }, ::chooseEquip).apply { this@EquipTable.stage.addActor(this) } }
    private val actionButton by lazy { ActionButton(::onActionButtonClick) }

    init {
        padTop(Constants.UI_MARGIN)

        // Top table
        table {
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))

            // InfoTable
            add(this@EquipTable.infoTable)

            row()

            // SuitTable
            add(this@EquipTable.suitTable)
        }

        row()

        // Action button
        add(actionButton)

        row()

        // StockTable
        add(stockTable)

        setActiveEquip(selectedEquipAlias)
    }

    private fun fetchActiveEquipAlias(equipType: EquipType) = when (equipType) {
        EquipType.SHIP -> playerData.equips.single { it.type == EquipType.SHIP && it.index == playerData.activeEquips.shipIndex }
        EquipType.GUN -> playerData.equips.single { it.type == EquipType.GUN && it.index == playerData.activeEquips.gunIndex }
    }

    private fun onActionButtonClick() {
        require(selectedModView != null) { "selectedModView is null while action button click" }
        val modView = selectedModView as ModView
        val modAlias = modView.modAlias

        val targetTable = when (modView.parent.parent.name) {
            Constants.SUIT_TABLE -> stockTable
            Constants.STOCK_TABLE -> suitTable
            else -> throw Exception("Can't find ModView's parent table name")
        }
        if (targetTable == suitTable) {
            if (suitTable.addMod(modAlias, true))
                stockTable.subtractOneFromSimilarTo(modAlias)
        } else {
            stockTable.addMod(modAlias, true)
            suitTable.removeMod(modAlias, true)
        }
        // Update stats in InfoTable
        infoTable.setInfo(selectedEquipAlias)

        modView.deactivate()
        selectedModView = null
    }

    private fun chooseEquip(equipAlias: EquipAlias) {
        selectedEquipAlias = equipAlias
        hideEquipWindow()
    }

    // fill info and suit tables with chosen equip data
    private fun setActiveEquip(equipAlias: EquipAlias) {
        // fill info table
        infoTable.setInfo(equipAlias)

        // fill suitTable
        suitTable.removeAllViews()
        suitTable.setModAliases(equipAlias.mods)
        suitTable.addAllViews()

        // fill stockTable
        stockTable.removeAllViews()
        val stockMods = playerData.mods.filter {it.type == modType}
        val stockMinusSuit = subtractSuitFromStock(stockMods, equipAlias.mods)
        stockTable.setModAliases(stockMinusSuit)
        stockTable.addAllViews()

        // update active equip in PlayerData
        when (equipAlias.type) {
            EquipType.SHIP -> playerData.activeEquips.shipIndex = equipAlias.index
            EquipType.GUN -> playerData.activeEquips.gunIndex = equipAlias.index
        }
    }

    private fun subtractSuitFromStock(stockMods: GdxArray<ModAlias>, suitMods: GdxArray<ModAlias>): GdxArray<ModAlias> {
        val result = GdxArray<ModAlias>()
        stockMods.forEach { stock ->
            val isInSuit = suitMods.any { it.index == stock.index && it.level == stock.level }
            if (isInSuit) {
                if (stock.quantity > 1)
                    result.add(stock.copy(quantity = stock.quantity - 1))
            } else {
                result.add(stock.copy())
            }
        }
        return result
    }

    private fun showEquipWindow() {
        equipWindow.isVisible = true
    }

    private fun hideEquipWindow() {
        equipWindow.isVisible = false
    }

    fun reload() {
        setBestMods()
        setActiveEquip(selectedEquipAlias)
    }

    private fun setBestMods() {
        selectedEquipAlias.mods.forEach { suitMod ->
            val maxLevelMod = playerData.mods
                    .filter { it.type == suitMod.type && it.index == suitMod.index }
                    .maxByOrNull { it.level }
            if (maxLevelMod != null) suitMod.level = maxLevelMod.level
        }
    }
}