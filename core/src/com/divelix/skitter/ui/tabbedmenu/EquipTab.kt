package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
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
import ktx.style.get

abstract class EquipTab(val assets: Assets): Table(), KTable {
    val description: Label
    val equipIcon: Image

    init {
        padTop(Constants.UI_MARGIN)
        table {
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))

            // InfoTable
            table {
                pad(Constants.UI_PADDING)
                scrollPane {
                    this@EquipTab.description = scaledLabel(Constants.LOREM_IPSUM, 0.1f).apply {
                        wrap = true
                        setAlignment(Align.top)
                    }
                }.cell(width = 92f, height = 100f, padRight = Constants.UI_PADDING)
                table {
                    touchable = Touchable.enabled
                    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                    this@EquipTab.equipIcon = image(this@EquipTab.assets.manager.get<Texture>(Constants.SHIP_DEFAULT))
                            .apply { setScaling(Scaling.fit) }.cell(pad = 7f)
                    onClickEvent { event, actor ->
                        println("Equip icon clicked")
                        this@EquipTab.updateIcon()
                    }
                }.cell(width = 100f, height = 100f)
                table {
                    left()
                    table {
                        defaults().left()
                        scaledLabel("DAMAGE: ", Constants.SPECS_SCALE); row()
                        scaledLabel("CAPACITY: ", Constants.SPECS_SCALE); row()
                        scaledLabel("RELOAD: ", Constants.SPECS_SCALE); row()
                        scaledLabel("SPEED: ", Constants.SPECS_SCALE); row()
                        scaledLabel("CRITICAL: ", Constants.SPECS_SCALE); row()
                        scaledLabel("CHANCE: ", Constants.SPECS_SCALE); row()
                    }
                    table {
                        defaults().left()
                        scaledLabel("100", Constants.SPECS_SCALE); row()
                        scaledLabel("13", Constants.SPECS_SCALE); row()
                        scaledLabel("0.5", Constants.SPECS_SCALE); row()
                        scaledLabel("10", Constants.SPECS_SCALE); row()
                        scaledLabel("x2.0", Constants.SPECS_SCALE); row()
                        scaledLabel("20%", Constants.SPECS_SCALE); row()
                    }
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

    fun updateIcon() {
        equipIcon.drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("blueBg"))
    }
}