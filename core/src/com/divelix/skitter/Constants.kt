package com.divelix.skitter

object Constants {
    const val TITLE = "Skitter"
    const val D_WIDTH = 350
    const val D_HEIGHT = 700
    const val WIDTH = 15f
//    const val HEIGHT = 1920
    const val PPM = 32f
    const val PTM = 1 / PPM
//    const val B2D_WIDTH = WIDTH * PTM
//    const val B2D_HEIGHT = HEIGHT * PTM
    const val B2D_FPS = 120f
    const val B2D_STEP_TIME = 1 / B2D_FPS
    const val BULLET_CRITICAL_DISTANCE_2 = 100f
    const val BULLET_SPEED = 40f
    const val DEFAULT_SLOW_RATE = 1f
    const val PLAYER_SIZE = 2f
    // Controls params
    const val CAMERA_RADIUS = 1f
    const val CAMERA_RADIUS_2 = CAMERA_RADIUS * CAMERA_RADIUS
    const val DEAD_BAND = 500f
    const val SPEED_LIMIT = 2f

    const val MOD_WIDTH = 64f
    const val MOD_HEIGHT = 64f

    const val LOADING_IMAGE = "green_square.png"
    const val UISKIN_ATLAS = "uiskin.atlas"
    const val DIGITS_FNT = "fonts/digits.fnt"
    const val DIGITS_PNG = "fonts/digits.png"
    const val PROXIMANOVA16_FNT = "fonts/ProximaNova16.fnt"
    const val PROXIMANOVA16_PNG = "fonts/ProximaNova16.png"
    const val PROXIMANOVA32_FNT = "fonts/ProximaNova32.fnt"
    const val PROXIMANOVA32_PNG = "fonts/ProximaNova32.png"
    const val PROXIMANOVA64_FNT = "fonts/ProximaNova64.fnt"
    const val PROXIMANOVA64_PNG = "fonts/ProximaNova64.png"
    const val VERTEX_SHADER = "shaders/shader.vsh"
    const val FRAGMENT_SHADER = "shaders/shader.fsh"
    const val WEAPON_ICON = "weapon-icon.png"
    const val PLAYER_DEFAULT = "player-default.png"
    const val ENEMY_DEFAULT = "enemy-default.png"
    const val BULLET_DEFAULT = "bullet-default.png"
    const val AIM = "aim-64.png"
    const val BACKGROUND_IMAGE = "sky_tex.png"
    const val SKIN_GRAD = "skin_grad.png"
    const val WHITE_CIRCLE = "skin_grad.png"
    const val BACK_BTN = "back_tex.png"
    const val APPLY_BTN = "apply_btn.png"
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
}