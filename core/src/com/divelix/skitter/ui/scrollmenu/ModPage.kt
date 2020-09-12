package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.PlayerData
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.tabbedmenu.GunTab
import com.divelix.skitter.ui.tabbedmenu.ShipTab
import com.divelix.skitter.ui.tabbedmenu.Tab
import com.divelix.skitter.ui.tabbedmenu.TabbedMenu
import ktx.actors.onClickEvent
import ktx.collections.gdxArrayOf
import ktx.scene2d.*
import ktx.style.get

class ModPage(val playerData: PlayerData, assets: Assets) : Page() {

    init {
        table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                right().pad(12f)
                background = assets.bgDrawable
                scaledLabel("2500", Constants.SPECS_SCALE, "mod-name")
            }.cell(fillX = true)
            row()
            table {
//                pad(Constants.UI_MARGIN)
                table {
                    image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.SELL_BTN))).cell(width = 76f, height = 76f)
                    row()
                    scaledLabel("1000", Constants.SPECS_SCALE).cell(padTop = Constants.UI_MARGIN)
                }
                table {
                    pad(Constants.UI_MARGIN)

                    // Big mod
                    table {
                        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                    }.cell(width = 150f, height = 150f)
                    row()

                    // Scroll pane with description
                    table {
                        background = assets.bgDrawable
                        scrollPane {
                            table {
                                pad(12f)
                                scaledLabel("Mod name", Constants.SPECS_SCALE)
                                row()
                                scaledLabel("spec1: 100\nspec2: 50\nspec3: 20\nspec4: 120", Constants.SPECS_SCALE) {
                                    wrap = true
                                    setAlignment(Align.center)
                                }
                            }
                        }.cell(width = 150f, height = 78f)
                    }
                }
                table {
                    image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.UP_BTN))).cell(width = 76f, height = 76f)
                    row()
                    scaledLabel("800", Constants.SPECS_SCALE).cell(padTop = Constants.UI_MARGIN)
                }
            }
            row()
            val tabbedMenu = TabbedMenu(gdxArrayOf(
                    Tab(assets.manager.get<Texture>(Constants.MOD_GUN_CRIT), scene2d.table { label("one") }),
                    Tab(assets.manager.get<Texture>(Constants.MOD_GUN_CAPACITY), scene2d.table { label("two") })
            ))
            add(tabbedMenu)
        }
    }

    override fun update() {
//        nameLabel.setText(playerData.name)
    }
}