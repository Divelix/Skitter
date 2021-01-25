package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.PlayerData
import ktx.actors.txt
import ktx.collections.gdxArrayOf

class GunTable(playerData: PlayerData, assets: Assets) : AEquipTable(playerData, assets) {
    init {
        description.txt = "Gun description"
        equipIcon.drawable = TextureRegionDrawable(assets.manager.get<Texture>(Constants.GUN_DEFAULT))
        specsNames.txt = "DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: "
        specsValues.txt = "0\n0\n0\n0\nx1\n0%"
    }

    override fun makeEquipList(): Array<Pair<Texture, String>> {
        return gdxArrayOf(
                assets.manager.get<Texture>(Constants.GUN_DEFAULT) to "default gun",
                assets.manager.get<Texture>(Constants.GUN_SNIPER) to "sniper gun"
        )
    }

    override fun showSpecs() {
        TODO("Not yet implemented")
    }

    override fun showSuitMods() {
        // TODO add ::selectMod to ModView
//        playerData.activeEquips.gun.mods.forEachIndexed { index, modAlias ->
//            (suitTable.children[index] as Container<*>).actor = ModView(modAlias, assets)
//        }
    }

    override fun showStockMods() {
        // TODO add ::selectMod to ModView
//        playerData.mods.gun.forEachIndexed { index, modAlias ->
//            (stockTable.children[index] as Container<*>).actor = ModView(modAlias, assets)
//        }
    }
}