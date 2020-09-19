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
import ktx.inject.Context
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class EquipPage(val playerData: PlayerData, context: Context) : Page(context) {

    init {
        table {
            setFillParent(true)
            top()
            val tabbedMenu = TabbedMenu(gdxArrayOf(
                    Tab(this@EquipPage.assets.manager.get<Texture>(Constants.SHIP_ICON), ShipTab(this@EquipPage.assets)),
                    Tab(this@EquipPage.assets.manager.get<Texture>(Constants.GUN_ICON), GunTab(this@EquipPage.assets))
            ))
            add(tabbedMenu)
        }
    }

    override fun update() {
//        nameLabel.setText(playerData.name)
    }
}