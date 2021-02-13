package com.divelix.skitter.utils

import com.divelix.skitter.data.EquipType
import com.divelix.skitter.data.RegionName

object RegionBinder {
    fun chooseEquipRegionName(type: EquipType, index: Int) = when (type) {
        EquipType.SHIP -> when (index) {
            1 -> RegionName.SHIP_DEFAULT
            2 -> RegionName.SHIP_TANK
            else -> throw Exception("no drawable for ship index = $index")
        }
        EquipType.GUN -> when (index) {
            1 -> RegionName.GUN_DEFAULT
            2 -> RegionName.GUN_SNIPER
            else -> throw Exception("no drawable for gun index = $index")
        }
    }
}