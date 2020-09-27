package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import ktx.actors.txt
import ktx.collections.gdxArrayOf

class GunTable(assets: Assets) : EquipTable(assets) {
    init {
        description.txt = "Gun description"
        equipIcon.drawable = TextureRegionDrawable(assets.manager.get<Texture>(Constants.GUN_DEFAULT))
        specsNames.txt = "DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: "
        specsValues.txt = "100\n13\n0.5\n10\nx2.0\n20%"
    }

    override fun makeEquipList(): Array<Pair<Texture, String>> {
        return gdxArrayOf(
                assets.manager.get<Texture>(Constants.GUN_DEFAULT) to "default gun",
                assets.manager.get<Texture>(Constants.GUN_SNIPER) to "sniper gun"
        )
    }
}