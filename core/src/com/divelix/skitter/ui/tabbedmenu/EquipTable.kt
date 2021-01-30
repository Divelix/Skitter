package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.data.*
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.scrollmenu.ModSelector
import ktx.actors.onClick
import ktx.actors.onClickEvent
import ktx.actors.txt
import ktx.collections.*
import ktx.log.debug
import ktx.scene2d.*
import ktx.style.get

class EquipTable(
        private val equipType: EquipType,
        private val playerData: PlayerData,
        private val equipsData: EquipsData
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
    private val equipName: Label
    private val description: Label
    private val equipIcon: Image
    private val specsNames: Label
    private val specsValues: Label
    private val equipMap by lazy { makeEquipMap() }
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
            table {
                pad(Constants.UI_PADDING)

                // Description
                table {
                    this@EquipTable.equipName = scaledLabel("Equip name")
                    row()
                    scrollPane {
                        this@EquipTable.description = scaledLabel(Constants.LOREM_IPSUM).apply {
                            wrap = true
                            setAlignment(Align.top)
                        }
                    }.cell(grow = true)
                }.cell(width = 92f, height = 100f, padRight = Constants.UI_PADDING)

                // Icon
                table {
                    touchable = Touchable.enabled
                    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                    this@EquipTable.equipIcon = image(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                            .apply { setScaling(Scaling.fit) }.cell(pad = Constants.UI_PADDING)
                    onClickEvent { _ ->
                        this@EquipTable.switchEquipWindow()
                    }
                }.cell(width = 100f, height = 100f)

                // Stats
                table {
                    left()
                    this@EquipTable.specsNames = scaledLabel("DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: ")
                    this@EquipTable.specsValues = scaledLabel("100\n13\n0.5\n10\nx2.0\n20%")
                }.cell(width = 92f, height = 100f, padLeft = Constants.UI_PADDING)
            }.cell(padTop = Constants.UI_PADDING, padLeft = Constants.UI_PADDING, padBottom = 0f, padRight = Constants.UI_PADDING)

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
        onClick {
            debug { "action button click" }
        }
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
                this@EquipTable.equipMap.forEach { (equipAlias, _) ->
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
                            val regionName = this@EquipTable.chooseEquipRegionName(equipAlias.type, equipAlias.index)
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
//        onClick {
//            debug { "Empty mod was clicked" }
//            this@EquipTable.selectedModView.let { selected ->
//                if (selected != null) {
//                    val selectedContainer = selected.parent as Container<*>
//                    (this.parent as Container<*>).actor = selected
//                    selectedContainer.actor = this
//                    selected.deactivate()
//                    this@EquipTable.selectedModView = null
//                }
//            }
//        }
    }

    // fill info and suit tables with chosen equip data
    private fun setActiveEquip(equipAlias: EquipAlias) {
        val equip = equipMap[equipAlias]

        //fill info table
        equipName.txt = equip.name
        description.txt = equip.description
        val regionName = chooseEquipRegionName(equip.type, equip.index)
        equipIcon.drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<TextureRegion>(regionName()))
        val idx = equipAlias.level - 1
        when (val specs = equip.specs) {
            is ShipSpecs -> {
                specsNames.txt = "HEALTH: \nSPEED: "
                specsValues.txt = "${specs.health[idx]}\n${specs.speed[idx]}"
            }
            is GunSpecs -> {
                specsNames.txt = "DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: "
                specsValues.txt = "${specs.damage[idx]}\n${specs.capacity[idx]}\n" +
                        "${specs.reload[idx]}\n${specs.speed[idx]}\n" +
                        "${specs.crit[idx]}\n${specs.chance[idx]}"
            }
        }

        // clear suit table
        suitTable.children.forEach {
            (it as Container<*>).actor = makeEmptyCell()
        }
        // fill suit table with active equip mods
        equipAlias.mods.forEachIndexed { i, modAlias ->
            (suitTable.children[i] as Container<*>).actor = makeModView(modAlias)
        }

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

    private fun makeEquipMap(): GdxMap<EquipAlias, Equip> {
        val equipMap = GdxMap<EquipAlias, Equip>()
        val equips = equipsData.equips.filter { it.type == equipType }
        playerData.equips.filter { it.type == equipType }.forEach { equipAlias ->
            equipMap.put(equipAlias, equips.single { it.index == equipAlias.index })

        }
        return equipMap
    }

    private fun switchEquipWindow() {
        equipWindow.isVisible = !equipWindow.isVisible
    }

    private fun chooseEquipRegionName(type: EquipType, index: Int) = when (type) {
        EquipType.SHIP -> when (index) {
            1 -> RegionName.SHIP_DEFAULT
            2 -> RegionName.SHIP_TANK
            else -> throw Exception("no drawable for ship index = $index")
        }
        EquipType.GUN -> when (index) {
            1 -> RegionName.GUN_DEFAULT
            2 -> RegionName.GUN_SNIPER
            else -> throw Exception("no drawable for gun index = $index")
        }
    }
}