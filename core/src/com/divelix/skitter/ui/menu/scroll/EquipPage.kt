package com.divelix.skitter.ui.menu.scroll

import com.divelix.skitter.data.*
import com.divelix.skitter.ui.menu.equip.EquipTable
import com.divelix.skitter.ui.menu.tabs.Tab
import com.divelix.skitter.ui.menu.tabs.TabbedMenu
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ktx.scene2d.table

class EquipPage(context: Context, playerData: PlayerData, activePlayerData: ActivePlayerData) : Page(context) {
    private val shipTable by lazy { EquipTable(EquipType.SHIP, playerData, activePlayerData) }
    private val gunTable by lazy { EquipTable(EquipType.GUN, playerData, activePlayerData) }

    init {
        val tabbedMenu = TabbedMenu(gdxArrayOf(
                Tab(assets.manager.get(Constants.SHIP_ICON), shipTable),
                Tab(assets.manager.get(Constants.GUN_ICON), gunTable)
        ))
        table {
            setFillParent(true)
            top()
            add(tabbedMenu)
        }
    }

    fun reloadForModType(modType: ModType) = when (modType) {
        ModType.SHIP_MOD ->  shipTable.reload()
        ModType.GUN_MOD ->  gunTable.reload()
    }
}