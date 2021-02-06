package com.divelix.skitter.ui.scrollmenu

import com.divelix.skitter.data.*
import com.divelix.skitter.ui.tabbedmenu.*
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ktx.scene2d.table

class EquipPage(context: Context, playerData: PlayerData) : Page(context) {

    init {
        val tabbedMenu = TabbedMenu(gdxArrayOf(
                Tab(assets.manager.get(Constants.SHIP_ICON), EquipTable(EquipType.SHIP, playerData)),
                Tab(assets.manager.get(Constants.GUN_ICON), EquipTable(EquipType.GUN, playerData))
        ))
        table {
            setFillParent(true)
            top()
            add(tabbedMenu)
        }
    }

    override fun update() {
//        nameLabel.setText(playerData.name)
    }
}