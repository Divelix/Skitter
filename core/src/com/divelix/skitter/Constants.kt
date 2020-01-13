package com.divelix.skitter

import com.badlogic.gdx.graphics.Color

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
    // Controls params
    const val CAMERA_RADIUS = 2f
    const val CAMERA_RADIUS_2 = CAMERA_RADIUS * CAMERA_RADIUS
    const val DEAD_BAND_2 = 500f
    const val MAX_TOUCHPAD_RADIUS = 170f
    const val MAX_TOUCHPAD_RADIUS_2 = MAX_TOUCHPAD_RADIUS * MAX_TOUCHPAD_RADIUS

    const val MOD_WIDTH = 64f
    const val MOD_HEIGHT = 64f

    const val UISKIN_ATLAS = "uiskin.atlas"
    const val DIGITS_FNT = "fonts/digits.fnt"
    const val DIGITS_PNG = "fonts/digits.png"
    const val ROBOTO_FONT = "fonts/Roboto-Light.ttf"
    const val ROBOTO_ALIAS_QUANTITY = "quantity"
    const val ROBOTO_ALIAS_DEFAULT = "small"
    const val ROBOTO_ALIAS_RELOAD = "big"
    const val VERTEX_SHADER = "shaders/shader.vsh"
    const val FRAGMENT_SHADER = "shaders/shader.fsh"
    const val BUCKET_ICON = "player-default.png"
    const val PISTOL_ICON = "pistol.png"
    const val CARRIAGE = "carriage.png"
    const val LOVER = "lover.png"
    const val SNIPER = "sniper.png"
    const val BULLET_DEFAULT = "bullet-default.png"
    const val AIM = "aim-64.png"
    const val WHITE_CIRCLE = "white_circle.png"
    const val BACKGROUND_IMAGE = "dark-honeycomb.png"
    const val SHIP_ICON = "ship-icon.png"
    const val GUN_ICON = "gun-icon.png"
    const val APPLY_ICON = "apply-icon.png"
    const val RESTART_ICON = "restart-icon.png"
    const val UP_BTN = "up-btn.png"
    const val SELL_BTN = "sell-btn.png"
    const val PAUSE_BTN = "pause.png"
    const val MENU_PLAY = "menu-play.png"
    const val MENU_MOD = "menu-mod.png"
    const val MENU_EQUIP = "menu-equip.png"
    const val MOD_SHIP_HEALTH = "mods/mod-ship-health.png"
    const val MOD_SHIP_SPEED = "mods/mod-ship-speed.png"
    const val MOD_SHIP_CHUBBER = "mods/mod-ship-chubber.png"
    const val MOD_GUN_DAMAGE = "mods/mod-gun-damage.png"
    const val MOD_GUN_CAPACITY = "mods/mod-gun-capacity.png"
    const val MOD_GUN_RELOAD = "mods/mod-gun-reload.png"
    const val MOD_GUN_SPEED = "mods/mod-gun-speed.png"
    const val MOD_GUN_CRIT = "mods/mod-gun-crit.png"
    const val MOD_GUN_CHANCE = "mods/mod-gun-chance.png"
    const val STAR = "star.png"

    const val HIT_SOUND = "audio/hit.wav"
    const val SHOT_SOUND = "audio/shot.wav"

    const val PLAYER_FILE = "json/player_data.json"
    const val MODS_FILE = "json/mods.json"
    const val SHIPS_FILE = "json/ships.json"
    const val GUNS_FILE = "json/guns.json"
    const val ENEMIES_FILE = "json/enemies.json"

    const val SHIPS_TAB = "ShipsTab"
    const val GUNS_TAB = "GunsTab"
}