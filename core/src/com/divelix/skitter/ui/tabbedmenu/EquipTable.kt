package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.data.*
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.scrollmenu.ModSelector
import ktx.actors.onClickEvent
import ktx.actors.txt
import ktx.collections.gdxArrayOf
import ktx.scene2d.*
import ktx.style.get

class EquipTable(val equipType: EquipType, playerData: Player, val equipsData: EquipsData) : Table(), KTable, ModSelector {
    override var selectedModView: ModView? = null
    val modType = when (equipType) {
        EquipType.SHIP -> ModType.SHIP_MOD
        EquipType.GUN -> ModType.GUN_MOD
    }
    val description: Label
    val equipIcon: Image
    val specsNames: Label
    val specsValues: Label
    val equipList by lazy { makeEquipList() }
    val equipWindow by lazy { makeEquipWindow() }
    val suitTable: Table
    val stockTable: Table

    init {
        padTop(Constants.UI_MARGIN)

        // Top table
        table {
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))

            // InfoTable
            table {
                pad(Constants.UI_PADDING)

                // Description
                scrollPane {
                    this@EquipTable.description = scaledLabel(Constants.LOREM_IPSUM).apply {
                        wrap = true
                        setAlignment(Align.top)
                    }
                }.cell(width = 92f, height = 100f, padRight = Constants.UI_PADDING)

                // Icon
                table {
                    touchable = Touchable.enabled
                    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                    this@EquipTable.equipIcon = image(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                            .apply { setScaling(Scaling.fit) }.cell(pad = Constants.UI_PADDING)
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
                pad(0f, Constants.UI_PADDING, Constants.UI_PADDING, Constants.UI_PADDING)
                defaults().pad(Constants.UI_PADDING)
                for (i in 1..8) {
                    container(Actor().apply { setSize(64f, 64f) }) {
                        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                    }
                    if (i % 4 == 0) row()
                }
            }
        }
        row()

        // Bottom table
        table {
            pad(Constants.UI_MARGIN)

            // StockTable
            scrollPane {
                container {
                    this@EquipTable.stockTable = table {
                        pad(Constants.UI_PADDING)
                        defaults().pad(Constants.UI_PADDING)
                        // fill with mods
                        val modAliases = playerData.mods.filter { it.type == this@EquipTable.modType }
                        modAliases.forEachIndexed { i, modAlias ->
                            container(this@EquipTable.makeModView(modAlias))
                            if ((i + 1) % 4 == 0) row()
                        }
                        // fill row with empty cells
                        for (i in 1..(4 - modAliases.size % 4)) {
                            container(Actor().apply { setSize(Constants.MOD_SIZE, Constants.MOD_SIZE) }) {
                                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                            }
                        }
                        row()
                        // fill additional rows with empty cells
                        for (i in 1..8) {
                            container(Actor().apply { setSize(Constants.MOD_SIZE, Constants.MOD_SIZE) }) {
                                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                            }
                            if (i % 4 == 0) row()
                        }
                    }
                    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                }
            }
        }
        setEquip(
                when (equipType) {
                    EquipType.SHIP -> playerData.activeEquips.ship
                    EquipType.GUN -> playerData.activeEquips.gun
                }
        )
    }

    private fun makeModView(modAlias: ModAlias) = ModView(modAlias, ::selectMod)

    private fun setEquip(equipAlias: EquipAlias) {
        val equip = equipsData.equips.single { it.type == equipType && it.index == equipAlias.index }
        description.txt = equip.description
        val regionName = when (equip.type) {
            EquipType.SHIP -> when (equip.index) {
                1 -> RegionName.SHIP_DEFAULT
                2 -> RegionName.SHIP_TANK
                else -> throw Exception("no drawable for ship index = ${equip.index}")
            }
            EquipType.GUN -> when (equip.index) {
                1 -> RegionName.GUN_DEFAULT
                2 -> RegionName.GUN_SNIPER
                else -> throw Exception("no drawable for gun index = ${equip.index}")
            }
        }
        equipIcon.drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<TextureRegion>(regionName()))
        val i = equipAlias.level - 1
        when (equip.specs) {
            is ShipSpecs -> {
                specsNames.txt = "HEALTH: \nSPEED: "
                specsValues.txt ="${equip.specs.health[i]}\n${equip.specs.speed[i]}"
            }
            is GunSpecs -> {
                specsNames.txt = "DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: "
                specsValues.txt ="${equip.specs.damage[i]}\n${equip.specs.capacity[i]}\n" +
                        "${equip.specs.reload[i]}\n${equip.specs.speed[i]}\n" +
                        "${equip.specs.crit[i]}\n${equip.specs.chance[i]}"
            }
        }
        equipAlias.mods.forEachIndexed { i, modAlias ->
            (suitTable.children[i] as Container<*>).actor = makeModView(modAlias)
        }
    }

    private fun makeEquipWindow(): Window {
        val window = scene2d.window("", "equip-choose") {
            isVisible = false
            val windowHeight = Constants.stageHeight - 192f - 50f - 12f
            setSize(326f, windowHeight)
            setPosition((Constants.STAGE_WIDTH - width) / 2f, Constants.stageHeight - height - 192f - 14f)
            top()
            scrollPane {
                setScrollingDisabled(true, false)
                table {
                    top()
                    defaults().padTop(12f)
                    for (equip in this@EquipTable.equipList) {
                        table {
                            left()
                            touchable = Touchable.enabled
                            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.GRAY_PIXEL))

                            // Icon
                            table {
//                                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.LIGHT_GRAY_PIXEL))
                                image(equip.first).apply { setScaling(Scaling.fit) }
                                onClickEvent { event ->
                                    println("Equip item clicked")
                                    this@EquipTable.switchEquipWindow()
                                }
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
        }
        stage.addActor(window)
        return window
    }

    private fun makeEquipList(): Array<Pair<TextureRegion, String>> {
        return gdxArrayOf(
                Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.GUN_DEFAULT()) to "default gun",
                Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.GUN_SNIPER()) to "sniper gun"
        )
    }

    private fun switchEquipWindow() {
        equipWindow.isVisible = !equipWindow.isVisible
    }
}