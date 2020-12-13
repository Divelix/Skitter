package com.divelix.skitter.ui.scrollmenu

import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.Player
import com.divelix.skitter.ui.tabbedmenu.GunTable
import com.divelix.skitter.ui.tabbedmenu.Tab
import com.divelix.skitter.ui.tabbedmenu.ShipTable
import com.divelix.skitter.ui.tabbedmenu.TabbedMenu
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ktx.scene2d.table

class EquipPage(val playerData: Player, context: Context) : Page(context) {

    init {
        table {
            setFillParent(true)
            top()
            val tabbedMenu = TabbedMenu(gdxArrayOf(
                    Tab(
                            this@EquipPage.assets.manager.get(Constants.SHIP_ICON),
                            ShipTable(this@EquipPage.playerData, this@EquipPage.assets)
                    ),
                    Tab(
                            this@EquipPage.assets.manager.get(Constants.GUN_ICON),
                            GunTable(this@EquipPage.playerData, this@EquipPage.assets)
                    )
            ))
            add(tabbedMenu)
        }
    }

    override fun update() {
//        nameLabel.setText(playerData.name)
    }
}