package com.divelix.skitter.ui.menu.equip

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Constants
import ktx.scene2d.KTable
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.container
import ktx.style.get

class SuitTable(makeEmptyCell: () -> Actor): Table(), KTable {

    init {
        name = Constants.SUIT_TABLE
        pad(0f, Constants.UI_PADDING, Constants.UI_PADDING, Constants.UI_PADDING)
        defaults().pad(Constants.UI_PADDING)
        for (i in 1..8) {
            container(makeEmptyCell()) {
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            }
            if (i % 4 == 0) row()
        }
    }
}