package com.divelix.skitter.ui.menu.equip

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.data.*
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.menu.scroll.ModSelector
import com.divelix.skitter.ui.menu.ModView
import com.divelix.skitter.utils.AliasBinder
import com.divelix.skitter.utils.RegionBinder
import ktx.actors.onClick
import ktx.actors.onClickEvent
import ktx.actors.txt
import ktx.collections.*
import ktx.log.debug
import ktx.scene2d.*
import ktx.style.get

class EquipTable(
        private val equipType: EquipType,
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
    private val infoTable = InfoTable(::switchEquipWindow)

    private val equipWindow by lazy { makeEquipWindow() }
    private val suitTable: Table
    private val stockTable: Table
    private val actionButton by lazy { makeActionButton() }

    init {
        padTop(Constants.UI_MARGIN)

        // Top table
        table {
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))

            // InfoTable
            add(this@EquipTable.infoTable)

            row()

            // SuitTable
            this@EquipTable.suitTable = table {
                name = Constants.SUIT_TABLE
                pad(0f, Constants.UI_PADDING, Constants.UI_PADDING, Constants.UI_PADDING)
                defaults().pad(Constants.UI_PADDING)
                for (i in 1..8) {
                    container(this@EquipTable.makeEmptyCell()) {
                        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                    }
                    if (i % 4 == 0) row()
                }
            }
        }
        row()

        // Action button
        add(actionButton)

        row()

        // Bottom table
        table {
            scrollPane {
                container {
                    // StockTable
                    this@EquipTable.stockTable = table {
                        name = Constants.STOCK_TABLE
                        pad(Constants.UI_PADDING)
                        defaults().pad(Constants.UI_PADDING)
                        // fill with empty cells
                        for (i in 1..16) {
                            container(this@EquipTable.makeEmptyCell()) {
                                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                            }
                            if (i % 4 == 0) row()
                        }
                    }
                    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                }
            }
        }
        fillStockTable()
        setActiveEquip(selectedEquipAlias)
    }

    private fun makeActionButton() = scene2d.container {
        isVisible = false
        size(326f, 50f)
        touchable = Touchable.enabled
        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
        actor = Image(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.MOVE_UP_ICON()))
                .apply {
                    setScaling(Scaling.fit)
                }
        onClick { this@EquipTable.onActiveButtonClick() }
    }

    private fun onActiveButtonClick() {
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

    private fun makeEquipWindow() = scene2d.window("", Constants.STYLE_EQUIP_CHOOSE) {
        isVisible = false
        isModal = true
        val windowHeight = Constants.stageHeight - 192f - 50f - 12f
        setSize(326f, windowHeight)
        setPosition((Constants.STAGE_WIDTH - width) / 2f, Constants.stageHeight - height - 192f - 14f)
        top()
        scrollPane {
            setScrollingDisabled(true, false)
            table {
                top()
                defaults().padTop(12f)
                this@EquipTable.playerData.equips.forEach { equipAlias ->
                    table {
                        left()
                        touchable = Touchable.enabled
                        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.GRAY_PIXEL))
                        onClickEvent { _ ->
                            this@EquipTable.selectedEquipAlias = equipAlias
                            this@EquipTable.switchEquipWindow()
                        }

                        // Icon
                        table {
                            val regionName = RegionBinder.chooseEquipRegionName(equipAlias.type, equipAlias.index)
                            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<TextureRegion>(Constants.LIGHT_GRAY_PIXEL))
                            image(Scene2DSkin.defaultSkin.get<TextureRegion>(regionName())).apply { setScaling(Scaling.fit) }
                        }.cell(width = 88f, height = 88f, pad = 6f)

                        // Description
                        table {
                            debug = true
                            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                            label("DEFAULT EQUIP", Constants.STYLE_BOLD_ORANGE)
                            row()
                            label("A bunch of text that describes the equip shortly") {
                                wrap = true
                                setAlignment(Align.top)
                            }.cell(grow = true)
                        }.cell(grow = true, padTop = 6f, padBottom = 6f)

                        // Stats
                        table {
//                                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                            left()
                            scaledLabel("DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: ")
                            scaledLabel("100\n13\n0.5\n10\nx2.0\n20%")
                        }.cell(pad = 6f)
                    }.cell(width = 302f, height = 100f)
                    row()
                }
            }
        }
    }.apply { this@EquipTable.stage.addActor(this) }

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

    private fun switchEquipWindow() {
        equipWindow.isVisible = !equipWindow.isVisible
    }
}