package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.data.Constants
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import ktx.actors.onClickEvent
import ktx.scene2d.*
import ktx.style.get

class InfoTable : Table(), KTable {

    private val equipName: Label
    private val description: Label
    private val equipIcon: Image
    private val specsNames: Label
    private val specsValues: Label

    init {
        pad(Constants.UI_PADDING)

        // Description
        table {
            this@InfoTable.equipName = scaledLabel("Equip name")
            row()
            scrollPane {
                this@InfoTable.description = scaledLabel(Constants.LOREM_IPSUM).apply {
                    wrap = true
                    setAlignment(Align.top)
                }
            }.cell(grow = true)
        }.cell(width = 92f, height = 100f, padRight = Constants.UI_PADDING)

        // Icon
        table {
            touchable = Touchable.enabled
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            this@InfoTable.equipIcon = image(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                    .apply { setScaling(Scaling.fit) }.cell(pad = Constants.UI_PADDING)
            onClickEvent { _ ->
//                this@InfoTable.switchEquipWindow()
            }
        }.cell(width = 100f, height = 100f)

        // Stats
        table {
            left()
            this@InfoTable.specsNames = scaledLabel("DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: ")
            this@InfoTable.specsValues = scaledLabel("100\n13\n0.5\n10\nx2.0\n20%")
        }.cell(width = 92f, height = 100f, padLeft = Constants.UI_PADDING)
    }
}