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
        private val playerData: PlayerData
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
    private val infoTable by lazy { InfoTable(::showEquipWindow) }
    private val suitTable by lazy { SuitTable(true, selectedEquipAlias.mods, ::selectMod) }
    private val stockTable by lazy { StockTable(false, playerData.mods.filter { it.type == modType }, ::selectMod) }
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
        // fill stockTable
        stockTable.removeAll()
        stockTable.setModAliases(playerData.mods.filter { it.type == modType })
        stockTable.addAll()

        // subtract equip mods from stockTable
        equipAlias.mods.forEach { stockTable.subtractOneFromSimilarTo(it) }

        // fill suitTable
        suitTable.addAllFor(equipAlias)

        // fill info table
        infoTable.setInfo(equipAlias)

        // update active equip in PlayerData
        when (equipAlias.type) {
            EquipType.SHIP -> playerData.activeEquips.shipIndex = equipAlias.index
            EquipType.GUN -> playerData.activeEquips.gunIndex = equipAlias.index
        }
    }

    private fun showEquipWindow() {
        equipWindow.isVisible = true
    }

    private fun hideEquipWindow() {
        equipWindow.isVisible = false
    }

    fun reload() {
        setActiveEquip(selectedEquipAlias)
    }
}