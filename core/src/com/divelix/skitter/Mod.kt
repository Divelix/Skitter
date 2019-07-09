package com.divelix.skitter

data class Mod(val type: ModType, val level: Int, val quantity: Int = 1)

enum class ModType {
    EMPTY,

    // Gun
    DAMAGE,
    ATTACK_SPEED,
    FIRE_DAMAGE,
    COLD_DAMAGE,

    // Ship
    HEALTH,
    SPEED,
    MANA
}