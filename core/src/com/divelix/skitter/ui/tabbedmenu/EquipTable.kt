package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.container
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import ktx.actors.onClickEvent
import ktx.scene2d.*
import ktx.scene2d.vis.visWindow
import ktx.style.get

abstract class EquipTable(val assets: Assets): Table(), KTable {
    val description: Label
    val equipIcon: Image
    val specsNames: Label
    val specsValues: Label
    val equipWindow by lazy { makeEquipWindow() }

    init {
        padTop(Constants.UI_MARGIN)

        // Top table
        table {
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))

            // InfoTable
            table {
                pad(Constants.UI_PADDING)
                scrollPane {
                    this@EquipTable.description = scaledLabel(Constants.LOREM_IPSUM).apply {
                        wrap = true
                        setAlignment(Align.top)
                    }
                }.cell(width = 92f, height = 100f, padRight = Constants.UI_PADDING)
                table {
                    touchable = Touchable.enabled
                    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                    this@EquipTable.equipIcon = image(Scene2DSkin.defaultSkin.get<Texture>("blueBg"))
                            .apply { setScaling(Scaling.fit) }.cell(pad = 7f)
                    onClickEvent { event, actor ->
                        println("Equip icon clicked")
                        this@EquipTable.switchEquipWindow()
                    }
                }.cell(width = 100f, height = 100f)
                table {
                    left()
                    this@EquipTable.specsNames = scaledLabel("DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: ")
                    this@EquipTable.specsValues = scaledLabel("100\n13\n0.5\n10\nx2.0\n20%")
                }.cell(width = 92f, height = 100f, padLeft = Constants.UI_PADDING)
            }

            row()

            // SuitTable
            table {
                pad(Constants.UI_PADDING)
                defaults().pad(Constants.UI_PADDING)
                for (i in 1..8) {
                    container(Actor().apply { setSize(64f, 64f) }) {
                        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
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
                container(
                        table {
                            pad(Constants.UI_PADDING)
                            defaults().pad(Constants.UI_PADDING)
                            for (i in 1..20) {
                                container(Actor().apply { setSize(Constants.MOD_SIZE, Constants.MOD_SIZE) }) {
                                    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                                }
                                if (i % 4 == 0) row()
                            }
                        }
                ) {
                    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                }
            }
        }
    }

    private fun makeEquipWindow(): Window {
        val window = scene2d.visWindow("Choose equip") {
            isVisible = false
            padTop(50f)
            centerWindow()
            scrollPane {
                setScrollingDisabled(true, false)
                table {
                    for (i in 1..10) {
                        label("Label #$i")
                        row()
                    }
                }
            }
        }
        stage.addActor(window)
        return window
    }

    private fun switchEquipWindow() {
        equipWindow.isVisible = !equipWindow.isVisible
    }
}