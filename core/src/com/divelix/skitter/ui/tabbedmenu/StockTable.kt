package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModAlias
import ktx.actors.onClick
import ktx.scene2d.*
import ktx.style.get

fun stockTable(mods: Array<ModAlias>, assets: Assets): Table {
    return scene2d.table {
        pad(Constants.UI_MARGIN)

        // StockTable
        scrollPane {
            container {
                table {
                    pad(Constants.UI_PADDING)
                    defaults().pad(Constants.UI_PADDING)
                    // fill with mods
                    mods.forEachIndexed { i, modData ->
                        container(ModView(modData, assets))
                        if ((i+1) % 4 == 0) row()
                    }
                    // fill empty space
                    for (i in 1..(4 - mods.size % 4)) {
                        container(Actor().apply { setSize(Constants.MOD_SIZE, Constants.MOD_SIZE) }) {
                            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                        }
                    }
                }
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            }
        }
    }
}