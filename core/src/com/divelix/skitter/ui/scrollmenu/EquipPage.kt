package com.divelix.skitter.ui.scrollmenu

import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.EquipType
import com.divelix.skitter.data.EquipsData
import com.divelix.skitter.data.Player
import com.divelix.skitter.ui.tabbedmenu.*
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ktx.scene2d.table

class EquipPage(context: Context, val playerData: Player, val equipsData: EquipsData) : Page(context), ModSelector {
    override var selectedModView: ModView? = null

    init {
        val tabbedMenu = TabbedMenu(gdxArrayOf(
                Tab(assets.manager.get(Constants.SHIP_ICON), EquipTable(EquipType.SHIP, playerData, equipsData)),
                Tab(assets.manager.get(Constants.GUN_ICON), EquipTable(EquipType.GUN, playerData, equipsData))
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