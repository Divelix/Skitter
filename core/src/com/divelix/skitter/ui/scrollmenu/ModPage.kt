package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.data.*
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.tabbedmenu.*
import com.divelix.skitter.ui.tabbedmenu.stockTable
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ktx.scene2d.*
import ktx.style.get

class ModPage(context: Context, val playerData: Player, val modsData: ModsData) : Page(context), ModSelector {
    override var selectedModView: ModView? = null

    init {
        val bigMod = bigMod(selectedModView)
        val tabbedMenu = TabbedMenu(gdxArrayOf(
                Tab(assets.manager.get(Constants.SHIP_ICON), stockTable(playerData.mods.ship, assets, ::selectMod)),
                Tab(assets.manager.get(Constants.GUN_ICON), stockTable(playerData.mods.gun, assets, ::selectMod))
        ))
        table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                right().pad(12f)
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                scaledLabel("2500", style = "mod-name")
            }.cell(fillX = true)
            row()
            table {
//                pad(Constants.UI_MARGIN)
                table {
                    image(TextureRegionDrawable(this@ModPage.assets.manager.get<Texture>(Constants.SELL_BTN))).cell(width = 76f, height = 76f)
                    row()
                    scaledLabel("1000").cell(padTop = Constants.UI_MARGIN)
                }
                table {
                    pad(Constants.UI_MARGIN)

                    // Big mod
//                    table {
//                        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
//                    }.cell(width = 150f, height = 150f)
                    container(bigMod) {
                        size(150f, 150f)
                    }
                    row()

                    // Scroll pane with description
                    table {
                        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                        scrollPane {
                            table {
                                pad(12f)
                                scaledLabel("Mod name")
                                row()
                                scaledLabel("spec1: 100\nspec2: 50\nspec3: 20\nspec4: 120") {
                                    wrap = true
                                    setAlignment(Align.center)
                                }
                            }
                        }.cell(width = 150f, height = 78f)
                    }
                }
                table {
                    image(TextureRegionDrawable(this@ModPage.assets.manager.get<Texture>(Constants.UP_BTN))).cell(width = 76f, height = 76f)
                    row()
                    scaledLabel("800").cell(padTop = Constants.UI_MARGIN)
                }
            }
            row()
            add(tabbedMenu)
        }
    }

    override fun update() {
//        nameLabel.setText(playerData.name)
    }
}