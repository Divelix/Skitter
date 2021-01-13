package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.Drawables
import com.divelix.skitter.data.ModsData
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.tabbedmenu.ModView
import ktx.actors.txt
import ktx.scene2d.KTable
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.table
import ktx.style.get

class ShowcaseTable(val modsData: ModsData): Table(), KTable {
    private val bigModTable by lazy { BigModTable(modsData.mods) }

    private val sellPriceLabel: Label
    private val upgradePriceLabel: Label

    init {
        // Sell button
        table {
            image(TextureRegionDrawable(Scene2DSkin.defaultSkin.get<TextureRegion>(Drawables.SELL_BTN()))).cell(width = 76f, height = 76f)
            row()
            this@ShowcaseTable.sellPriceLabel = scaledLabel("1000").cell(padTop = Constants.UI_MARGIN)
        }

        // Big mod
        add(this@ShowcaseTable.bigModTable)

        // Upgrade button
        table {
            image(TextureRegionDrawable(Scene2DSkin.defaultSkin.get<TextureRegion>(Drawables.UP_BTN()))).cell(width = 76f, height = 76f)
            row()
            this@ShowcaseTable.upgradePriceLabel = scaledLabel("800").cell(padTop = Constants.UI_MARGIN)
        }
    }

    fun setMod(modView: ModView?) {
        if (modView != null) {
            bigModTable.setMod(modView)
            sellPriceLabel.txt = "${modsData.sellPrices[modView.modAlias.level-1]}"
            upgradePriceLabel.txt = "${modsData.upgradePrices[modView.modAlias.level-1]}"
        } else {
            bigModTable.clearMod()
            sellPriceLabel.txt = ""
            upgradePriceLabel.txt = ""
        }
    }
}