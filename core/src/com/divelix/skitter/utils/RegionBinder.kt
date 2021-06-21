package com.divelix.skitter.utils

import com.divelix.skitter.data.Enemy
import com.divelix.skitter.data.EquipType
import com.divelix.skitter.data.ModType
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

    fun chooseModRegionName(type: ModType, index: Int) = when(type) {
        ModType.SHIP_MOD -> when (index) {
            1 -> RegionName.MOD_SHIP_HEALTH()
            2 -> RegionName.MOD_SHIP_SPEED()
            3 -> RegionName.MOD_SHIP_CHUBBER()
            else -> RegionName.STAR()
        }
        ModType.GUN_MOD -> when (index) {
            1 -> RegionName.MOD_GUN_DAMAGE()
            2 -> RegionName.MOD_GUN_CAPACITY()
            3 -> RegionName.MOD_GUN_RELOAD()
            4 -> RegionName.MOD_GUN_SPEED()
            5 -> RegionName.MOD_GUN_CRIT()
            6 -> RegionName.MOD_GUN_CHANCE()
            else -> RegionName.STAR()
        }
    }

    fun chooseEnemyRegionName(enemyType: Enemy) = when (enemyType) {
        Enemy.AGENT -> RegionName.AGENT()
        Enemy.JUMPER -> RegionName.JUMPER()
        Enemy.SNIPER -> RegionName.SNIPER_BASE()
        Enemy.WOMB -> RegionName.WOMB()
        Enemy.KID -> RegionName.KID()
        Enemy.RADIAL -> RegionName.RADIAL()
    }
}