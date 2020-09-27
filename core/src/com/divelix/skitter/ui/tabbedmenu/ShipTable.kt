package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import ktx.actors.txt
import ktx.collections.gdxArrayOf

class ShipTable(assets: Assets) : EquipTable(assets) {
    init {
        description.txt = "Ship description"
        equipIcon.drawable = TextureRegionDrawable(assets.manager.get<Texture>(Constants.SHIP_DEFAULT))
        specsNames.txt = "HEALTH: \nARMOR: \nENERGY: "
        specsValues.txt = "100\n50\n200"
    }

    override fun makeEquipList(): Array<Pair<Texture, String>> {
        return gdxArrayOf(
                assets.manager.get<Texture>(Constants.SHIP_DEFAULT) to "ship gun"
        )
    }
}