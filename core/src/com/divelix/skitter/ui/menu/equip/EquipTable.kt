package com.divelix.skitter.ui.menu.equip

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.*
import com.divelix.skitter.ui.menu.scroll.ModSelector
import com.divelix.skitter.ui.menu.ModView
import com.divelix.skitter.utils.AliasBinder
import ktx.collections.*
import ktx.log.debug
import ktx.scene2d.*
import ktx.style.get

class EquipTable(
        equipType: EquipType,
        private val playerData: PlayerData
) : Table(), KTable, ModSelector {
    override var selectedModView: ModView? = null
        set(value) {
            field = value
            actionButton.isVisible = true
            when (value?.parent?.parent?.name) {
                Constants.SUIT_TABLE -> {
                    (actionButton.actor as Image).drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.MOVE_DOWN_ICON()))
                }
                Constants.STOCK_TABLE -> {
                    (actionButton.actor as Image).drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.MOVE_UP_ICON()))
                }
                else -> actionButton.isVisible = false
            }
        }
    var selectedEquipAlias: EquipAlias = when (equipType) {
        EquipType.SHIP -> playerData.equips.single { it.type == EquipType.SHIP && it.index == playerData.activeEquips.shipIndex }
        EquipType.GUN -> playerData.equips.single { it.type == EquipType.GUN && it.index == playerData.activeEquips.gunIndex }
    }
        set(value) {
            field = value
            setActiveEquip(value)
        }

    private val modType = when (equipType) {
        EquipType.SHIP -> ModType.SHIP_MOD
        EquipType.GUN -> ModType.GUN_MOD
    }
    private val infoTable by lazy { InfoTable(::showEquipWindow) }
    private val suitTable by lazy { SuitTable(::makeEmptyCell) }
    private val stockTable by lazy { StockTable(::makeEmptyCell) }
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

        // Bottom table
        table {
            scrollPane {
                // StockTable
                container(this@EquipTable.stockTable) {
                    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                }
            }
        }
        fillStockTable()
        setActiveEquip(selectedEquipAlias)
    }

    private fun onActionButtonClick() {
        require(selectedModView != null) { "selectedModView == null" }
        val modView = selectedModView as ModView

        val targetTable = when (modView.parent.parent.name) {
            Constants.SUIT_TABLE -> stockTable
            Constants.STOCK_TABLE -> suitTable
            else -> throw Exception("Can't find name ModView parent table")
        }
        if (targetTable == suitTable) {
            // Check mod index duplicate
            val overlapModAlias = suitTable.children
                    .filter { (it as Container<*>).actor is ModView }
                    .map { ((it as Container<*>).actor as ModView).modAlias }
                    .singleOrNull {
                        it.type == modView.modAlias.type && it.index == modView.modAlias.index
                    }
            if (overlapModAlias == null) {
                selectedEquipAlias.mods.add(modView.modAlias)
            } else {
                debug { "SuitTable already has such mod" }
                return
            }
        } else {
            selectedEquipAlias.mods.removeValue(modView.modAlias, false)
        }
        // Move ModView in UI
        val selectedContainer = modView.parent as Container<*>
        val targetContainer = targetTable.children.first { (it as Container<*>).actor !is ModView } as Container<*>
        targetContainer.actor = modView
        selectedContainer.actor = makeEmptyCell()
        modView.deactivate()
        selectedModView = null
    }

    private fun chooseEquip(equipAlias: EquipAlias) {
        selectedEquipAlias = equipAlias
        hideEquipWindow()
    }

    // separate method creation motivated by impossibility to pass ::selectMod inside DSL scope
    private fun makeModView(modAlias: ModAlias) = ModView(modAlias, ::selectMod)

    private fun makeEmptyCell() = Actor().apply {
        setSize(Constants.MOD_SIZE, Constants.MOD_SIZE)
    }

    // fill info and suit tables with chosen equip data
    private fun setActiveEquip(equipAlias: EquipAlias) {
        val equip = AliasBinder.getEquip(equipAlias)

        // clear suit table
        suitTable.children.forEach {
            (it as Container<*>).actor = makeEmptyCell()
        }
        // fill suit table with active equip mods
        equipAlias.mods.forEachIndexed { i, modAlias ->
            (suitTable.children[i] as Container<*>).actor = makeModView(modAlias)
            // TODO make specs
        }

        // fill info table
        infoTable.setInfo(equipAlias)

        // update PlayerData
        when (equipAlias.type) {
            EquipType.SHIP -> playerData.activeEquips.shipIndex = equipAlias.index
            EquipType.GUN -> playerData.activeEquips.gunIndex = equipAlias.index
        }
    }

    private fun fillStockTable() {
        val modAliases = playerData.mods.filter { it.type == modType }
        modAliases.forEachIndexed { i, modAlias ->
            (stockTable.children[i] as Container<*>).actor = makeModView(modAlias)
        }
    }

    private fun showEquipWindow() {
        equipWindow.isVisible = true
    }

    private fun hideEquipWindow() {
        equipWindow.isVisible = false
    }
}