package com.divelix.skitter.ui.menu.scroll

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.divelix.skitter.data.*
import com.divelix.skitter.ui.menu.equip.EquipTable
import com.divelix.skitter.ui.menu.tabs.Tab
import com.divelix.skitter.ui.menu.tabs.TabbedMenu
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.table
import ktx.style.get

class EquipPage(context: Context, playerData: PlayerData, activePlayerData: ActivePlayerData) : Page(context) {
    private val shipTable by lazy { EquipTable(EquipType.SHIP, playerData, activePlayerData) }
    private val gunTable by lazy { EquipTable(EquipType.GUN, playerData, activePlayerData) }

    init {
        val tabbedMenu = TabbedMenu(gdxArrayOf(
                Tab(Scene2DSkin.defaultSkin[RegionName.SHIP_ICON()], shipTable),
                Tab(Scene2DSkin.defaultSkin[RegionName.SHIP_ICON()], gunTable)
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