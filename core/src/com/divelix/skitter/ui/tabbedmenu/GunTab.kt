package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import ktx.actors.txt

class GunTab(assets: Assets) : EquipTab(assets) {
    init {
        description.txt = "Gun description"
        equipIcon.drawable = TextureRegionDrawable(assets.manager.get<Texture>(Constants.GUN_DEFAULT))
        specsNames.txt = "DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: "
        specsValues.txt = "100\n13\n0.5\n10\nx2.0\n20%"
    }
}