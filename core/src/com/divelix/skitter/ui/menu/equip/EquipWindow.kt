package com.divelix.skitter.ui.menu.equip

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.EquipAlias
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.data.binders.RegionBinder
import ktx.actors.onClickEvent
import ktx.scene2d.*
import ktx.style.get

class EquipWindow(
        equips: Array<EquipAlias>,
        chooseEquip: (EquipAlias) -> Unit
) : Window("", Scene2DSkin.defaultSkin, Constants.STYLE_EQUIP_CHOOSE), KTable {

    init {
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
                equips.forEach { equipAlias ->
                    table {
                        left()
                        touchable = Touchable.enabled
                        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.GRAY_PIXEL))
                        onClickEvent { _ ->
                            chooseEquip(equipAlias)
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
                            scaledLabel("A bunch of text that describes the equip shortly") {
                                wrap = true
                                setAlignment(Align.top)
                            }.cell(grow = true)
                        }.cell(grow = true, padTop = 6f, padBottom = 6f)

                        // Stats
                        table {
//                            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                            left()
                            scaledLabel("DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: ")
                            scaledLabel("100\n13\n0.5\n10\nx2.0\n20%")
                        }.cell(pad = 6f)
                    }.cell(width = 302f, height = 100f)
                    row()
                }
            }
        }

        // close window if clicked outside of it
        addListener(object: InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (x < 0 || x > width || y < 0 || y > height) {
                    isVisible = false
                    event.cancel()
                    return true
                }
                return false
            }
        })
    }
}