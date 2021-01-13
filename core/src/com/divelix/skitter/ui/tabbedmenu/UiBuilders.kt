package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.data.*
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import ktx.actors.onClick
import ktx.actors.onClickEvent
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

fun stockTable(modType: ModType, mods: Array<ModAlias>, selectMod: (ModView) -> Unit): Table = scene2d.table {
    pad(Constants.UI_MARGIN)

    // StockTable
    scrollPane {
        setScrollingDisabled(true, false)
        setScrollbarsVisible(false)
        container {
            table {
                pad(Constants.UI_PADDING)
                defaults().pad(Constants.UI_PADDING)
                // fill with mods
                val filteredMods = mods.filter { it.type == modType }
                filteredMods.forEachIndexed { i, modData ->
                    container(ModView(modData, selectMod))
                    if ((i + 1) % 4 == 0) row()
                }
                // fill row with empty cells
                for (i in 1..(4 - filteredMods.size % 4)) {
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