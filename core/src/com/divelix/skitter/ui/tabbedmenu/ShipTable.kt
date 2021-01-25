package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.PlayerData
import ktx.actors.txt
import ktx.collections.gdxArrayOf

class ShipTable(playerData: PlayerData, assets: Assets) : AEquipTable(playerData, assets) {
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

    override fun showSpecs() {
        TODO("Not yet implemented")
    }

    override fun showSuitMods() {
        // TODO add ::selectMod to ModView
//        playerData.activeEquips.ship.mods.forEachIndexed { index, modAlias ->
//            (suitTable.children[index] as Container<*>).actor = ModView(modAlias, assets)
//        }
    }

    override fun showStockMods() {
        // TODO add ::selectMod to ModView
//        playerData.mods.ship.forEachIndexed { index, modAlias ->
//            (stockTable.children[index] as Container<*>).actor = ModView(modAlias, assets)
//        }
    }
}