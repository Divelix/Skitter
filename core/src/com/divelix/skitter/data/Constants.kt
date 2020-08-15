package com.divelix.skitter.data

object Constants {
    const val TITLE = "Skitter"
    const val D_WIDTH = 350
    const val D_HEIGHT = 700
    const val WIDTH = 15f
    const val HEIGHT = WIDTH * 2f
//    const val HEIGHT = 1920
    const val PPM = 32f
    const val PTM = 1 / PPM
    const val B2D_FPS = 120f
    const val B2D_STEP_TIME = 1 / B2D_FPS
    const val BULLET_CRITICAL_DISTANCE_2 = 400f
    const val DEFAULT_SLOW_RATE = 1f
    const val PLAYER_SIZE = 2f

    // Control params
    const val CAMERA_RADIUS = 2f
    const val CAMERA_RADIUS_2 = CAMERA_RADIUS * CAMERA_RADIUS
    const val DEAD_BAND = 20f
    const val DEAD_BAND_2 = DEAD_BAND * DEAD_BAND
    const val MAX_TOUCHPAD_RADIUS = 100f
    const val MAX_TOUCHPAD_RADIUS_2 = MAX_TOUCHPAD_RADIUS * MAX_TOUCHPAD_RADIUS

    const val MOD_WIDTH = 64f
    const val MOD_HEIGHT = 64f
    const val SHIPS_TAB = "ShipsTab"
    const val GUNS_TAB = "GunsTab"
    const val ROBOTO_ALIAS_QUANTITY = "quantity"
    const val ROBOTO_ALIAS_DEFAULT = "small"
    const val ROBOTO_ALIAS_RELOAD = "big"

    // Gameplay
    const val SHIP_DEFAULT = "textures/gameplay/ship-default.png"
    const val GUN_DEFAULT = "textures/gameplay/gun-default.png"
    const val BULLET_DEFAULT = "textures/gameplay/bullet-default.png"
    const val BACKGROUND_IMAGE = "textures/gameplay/dark-honeycomb.png"

    // UI
    const val CARRIAGE = "textures/ui/carriage.png"
    const val AIM = "textures/ui/aim-64.png"
    const val EQUIP_ICON = "textures/ui/equip-icon.png"
    const val BATTLE_ICON = "textures/ui/battle-icon.png"
    const val MOD_ICON = "textures/ui/mod-icon.png"
    const val WHITE_CIRCLE = "textures/ui/white_circle.png"
    const val SHIP_ICON = "textures/ui/ship-icon.png"
    const val GUN_ICON = "textures/ui/gun-icon.png"
    const val APPLY_ICON = "textures/ui/apply-icon.png"
    const val HOME_ICON = "textures/ui/home-icon.png"
    const val RESTART_ICON = "textures/ui/restart-icon.png"
    const val UP_BTN = "textures/ui/up-btn.png"
    const val SELL_BTN = "textures/ui/sell-btn.png"
    const val PAUSE_BTN = "textures/ui/pause.png"
    const val MENU_PLAY = "textures/ui/menu-play.png"
    const val MENU_MOD = "textures/ui/menu-mod.png"
    const val MENU_EQUIP = "textures/ui/menu-equip.png"
    const val STAR = "textures/ui/star.png"

    // Fonts
    const val DIGITS_FNT = "fonts/digits.fnt"
    const val DIGITS_PNG = "fonts/digits.png"
    const val ROBOTO_FONT = "fonts/Roboto-Light.ttf"

    // Skins
    const val ATLAS_UI = "skins/uiskin.atlas"

    // Shaders
    const val VERTEX_SHADER = "shaders/shader.vsh"
    const val FRAGMENT_SHADER = "shaders/shader.fsh"

    // Mods
    const val MOD_SHIP_HEALTH = "textures/mods/mod-ship-health.png"
    const val MOD_SHIP_SPEED = "textures/mods/mod-ship-speed.png"
    const val MOD_SHIP_CHUBBER = "textures/mods/mod-ship-chubber.png"
    const val MOD_GUN_DAMAGE = "textures/mods/mod-gun-damage.png"
    const val MOD_GUN_CAPACITY = "textures/mods/mod-gun-capacity.png"
    const val MOD_GUN_RELOAD = "textures/mods/mod-gun-reload.png"
    const val MOD_GUN_SPEED = "textures/mods/mod-gun-speed.png"
    const val MOD_GUN_CRIT = "textures/mods/mod-gun-crit.png"
    const val MOD_GUN_CHANCE = "textures/mods/mod-gun-chance.png"

    // Enemies
    const val AGENT = "textures/enemies/agent.png"
    const val SNIPER_BASE = "textures/enemies/sniper_base.png"
    const val SNIPER_TOWER = "textures/enemies/sniper_tower.png"
    const val WOMB = "textures/enemies/womb.png"
    const val KID = "textures/enemies/kid.png"
    const val RADIAL = "textures/enemies/radial.png"
    const val JUMPER = "textures/enemies/jumper.png"

    // Audio
    const val HIT_SOUND = "audio/hit.wav"
    const val SHOT_SOUND = "audio/shot.wav"

    // Json
    const val PLAYER_FILE = "json/player_data.json"
    const val MODS_FILE = "json/mods.json"
    const val SHIPS_FILE = "json/ships.json"
    const val GUNS_FILE = "json/guns.json"
    const val ENEMIES_FILE = "json/enemies.json"
}