package com.divelix.skitter

data class Mod(val name: ModName, val level: Int, val quantity: Int = 1)

enum class ModName {
    // Gun
    DAMAGE,
    RELOAD_SPEED,
    BULLET_SPEED,
    CRIT_CHANCE,
    CRIT_MULT,
    FIRE_DAMAGE,
    COLD_DAMAGE,

    // Ship
    HEALTH,
    SPEED,
    MANA
}