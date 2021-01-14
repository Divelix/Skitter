package com.divelix.skitter.data

import java.util.*

enum class RegionName {
    //    UI
    AIM_64,
    ANTI_SEAMLESS,
    APPLY_ICON,
    BATTLE_ICON,
    CARRIAGE,
    CARRIAGE_SHADOW,
    EQUIP_ICON,
    GUN_ICON,
    HOME_ICON,
    MENU_EQUIP,
    MENU_MOD,
    MENU_PLAY,
    MENU_ICON,
    PAUSE,
    RESTART_ICON,
    SELL_BTN,
    SHIP_ICON,
    UP_BTN,
    WHITE_CIRCLE,

    //    Equips
    SHIP_DEFAULT,
    SHIP_TANK,
    GUN_DEFAULT,
    GUN_SNIPER,

    //    Mods
    MOD_GUN_CAPACITY,
    MOD_GUN_CHANCE,
    MOD_GUN_CRIT,
    MOD_GUN_DAMAGE,
    MOD_GUN_RELOAD,
    MOD_GUN_SPEED,
    MOD_SHIP_CHUBBER,
    MOD_SHIP_HEALTH,
    MOD_SHIP_SPEED,
    STAR,

    //    Gameplay
    BULLET_DEFAULT,
    DARK_HONEYCOMB,

    //    Enemies
    AGENT,
    JUMPER,
    KID,
    RADIAL,
    SNIPER_BASE,
    SNIPER_TOWER,
    WOMB;

    operator fun invoke() = toString().toLowerCase(Locale.ROOT)
}
