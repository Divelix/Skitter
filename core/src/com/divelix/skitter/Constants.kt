package com.divelix.skitter

object Constants {
    const val TITLE = "Skitter"
    const val D_WIDTH = 350
    const val D_HEIGHT = 700
    const val WIDTH = 15f
    const val HEIGHT = WIDTH * 2f
//    const val HEIGHT = 1920
    const val PPM = 32f
    const val PTM = 1 / PPM
//    const val B2D_WIDTH = WIDTH * PTM
//    const val B2D_HEIGHT = HEIGHT * PTM
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
    const val ROBOTO_ALIAS_DEFAULT = "small"
    const val ROBOTO_ALIAS_RELOAD = "big"
    const val VERTEX_SHADER = "shaders/shader.vsh"
    const val FRAGMENT_SHADER = "shaders/shader.fsh"
    const val SHIPS_ICON = "ships-icon.png"
    const val GUNS_ICON = "guns-icon.png"
    const val MODS_ICON = "mods-icon.png"
    const val PLAYER_DEFAULT = "player-default.png"
    const val ENEMY_DEFAULT = "enemy-default.png"
    const val BULLET_DEFAULT = "bullet-default.png"
    const val AIM = "aim-64.png"
    const val BACKGROUND_IMAGE = "dark-honeycomb.png"
    const val APPLY_BTN = "apply_btn.png"
    const val UP_BTN = "up_btn.png"
    const val SELL_BTN = "sell_btn.png"
    const val PAUSE_BTN = "pause.png"
    const val RIFLE = "rifle.png"
    const val UZI = "uzi.png"
    const val MOD_GLOW = "mods/mod-glow.png"
    const val MOD_RELOAD_SPEED = "mods/reload-speed.png"
    const val MOD_BULLET_SPEED = "mods/bullet-speed.png"
    const val MOD_COLD_DAMAGE = "mods/cold-damage.png"
    const val MOD_DAMAGE = "mods/damage.png"
    const val MOD_FIRE_DAMAGE = "mods/fire-damage.png"
    const val MOD_HEALTH = "mods/health.png"
    const val MOD_MANA = "mods/mana.png"

    const val HIT_SOUND = "audio/hit.wav"
    const val SHOT_SOUND = "audio/shot.wav"
}