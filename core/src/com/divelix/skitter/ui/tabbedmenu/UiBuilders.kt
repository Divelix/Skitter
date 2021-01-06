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
import ktx.scene2d.vis.visImage
import ktx.style.get

fun bigMod(selectedMod: ModView?): Table = scene2d.table {
    setFillParent(true)
    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.YELLOW_PIXEL))

    table {
        bottom().left()
        pad(5f)
        defaults().pad(2f)
        for (i in 1..10) {
            image(TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))) { it.size(10f) }
        }
    }
}

fun stockTable(mods: Array<ModAlias>, assets: Assets, selectMod: (ModView) -> Unit): Table = scene2d.table {
    pad(Constants.UI_MARGIN)

    // StockTable
    scrollPane {
        container {
            table {
                pad(Constants.UI_PADDING)
                defaults().pad(Constants.UI_PADDING)
                // fill with mods
                mods.forEachIndexed { i, modData ->
                    container(ModView(modData, assets, selectMod))
                    if ((i + 1) % 4 == 0) row()
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