package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.container
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import ktx.actors.onClickEvent
import ktx.actors.txt
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.style.get

class ShipTab(assets: Assets) : EquipTab(assets) {
    init {
        description.txt = "Ship description"
        equipIcon.drawable = TextureRegionDrawable(assets.manager.get<Texture>(Constants.SHIP_DEFAULT))
        specsNames.txt = "HEALTH: \nARMOR: \nENERGY: "
        specsValues.txt = "100\n50\n200"
    }
}