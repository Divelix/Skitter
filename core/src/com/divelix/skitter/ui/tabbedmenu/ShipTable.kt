package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import ktx.actors.txt

class ShipTable(assets: Assets) : EquipTable(assets) {
    init {
        description.txt = "Ship description"
        equipIcon.drawable = TextureRegionDrawable(assets.manager.get<Texture>(Constants.SHIP_DEFAULT))
        specsNames.txt = "HEALTH: \nARMOR: \nENERGY: "
        specsValues.txt = "100\n50\n200"
    }
}