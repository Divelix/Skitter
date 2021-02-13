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

class StockTable(makeEmptyCell: () -> Actor): Table(), KTable {

    init {
        name = Constants.STOCK_TABLE
        pad(Constants.UI_PADDING)
        defaults().pad(Constants.UI_PADDING)
        // fill with empty cells
        for (i in 1..16) {
            container(makeEmptyCell()) {
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            }
            if (i % 4 == 0) row()
        }
    }
}