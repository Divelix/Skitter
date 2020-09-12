package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.graphics.Texture
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.PlayerData
import com.divelix.skitter.ui.tabbedmenu.GunTab
import com.divelix.skitter.ui.tabbedmenu.Tab
import com.divelix.skitter.ui.tabbedmenu.ShipTab
import com.divelix.skitter.ui.tabbedmenu.TabbedMenu
import ktx.collections.gdxArrayOf
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class EquipPage(val playerData: PlayerData, assets: Assets) : Page() {
//    val nameLabel: Label

    init {
        val rootTable = scene2d.table {
            setFillParent(true)
            top()
//            label("Player name = ")
//            nameLabel = label(playerData.name)
            val tabbedMenu = TabbedMenu(gdxArrayOf(
                    Tab(assets.manager.get<Texture>(Constants.SHIP_ICON), ShipTab(assets)),
                    Tab(assets.manager.get<Texture>(Constants.GUN_ICON), GunTab(assets)),
                    Tab(assets.manager.get<Texture>(Constants.MOD_GUN_CAPACITY), scene2d.table { label("third") })
            ))
            add(tabbedMenu)
        }
        addActor(rootTable)
    }

    override fun update() {
//        nameLabel.setText(playerData.name)
    }
}