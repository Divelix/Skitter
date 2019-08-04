package com.divelix.skitter

data class Mod(val name: ModName, val level: Int, val quantity: Int = 1)

enum class ModName {
    // Gun
    DAMAGE,
    RELOAD_SPEED,
    FIRE_DAMAGE,
    COLD_DAMAGE,

    // Ship
    HEALTH,
    SPEED,
    MANA
}