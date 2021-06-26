package com.divelix.skitter.data

import com.badlogic.gdx.Gdx

object Constants {
    const val TITLE = "Skitter"
    const val DESKTOP_WIDTH = 350
    const val DESKTOP_HEIGHT = 700
    const val STAGE_WIDTH = DESKTOP_WIDTH
    val aspectRatio = Gdx.graphics.width / Gdx.graphics.height.toFloat()
    val stageHeight = STAGE_WIDTH / aspectRatio
    const val WORLD_WIDTH = 15f
    const val PPM = 32f
    const val PTM = 1 / PPM
    const val B2D_FPS = 120f
    const val B2D_STEP_TIME = 1 / B2D_FPS
    const val BULLET_CRITICAL_DISTANCE_2 = 400f
    const val DEFAULT_SLOW_RATE = 1f
    const val PLAYER_SIZE = 2f

    // Data values
    const val EQUIP_MAX_LEVEL = 10
    const val MOD_MAX_LEVEL = 10
    const val MOD_MAX_QUANTITY = 100

    // UI values
    const val DEFAULT_LABEL_SCALE = 0.1f
    const val UI_MARGIN = 12f
    const val UI_PADDING = 7f
    const val MOD_SIZE = 64f
    const val SUIT_TABLE = "suit_table"
    const val STOCK_TABLE = "stock_table"

    // Colors
    const val ORANGE_COLOR = "orange"

    // Background textures
    const val BLACK_PIXEL_30 = "black_pixel_30"
    const val BLACK_PIXEL_50 = "black_pixel_50"
    const val BLACK_PIXEL_70 = "black_pixel_70"
    const val BLACK_PIXEL = "black_pixel"
    const val YELLOW_PIXEL = "yellow_pixel"
    const val WHITE_PIXEL = "white_pixel"
    const val LIGHT_GRAY_PIXEL = "light_gray"
    const val GRAY_PIXEL = "gray"
    const val DARK_GRAY_PIXEL = "dark_gray"
    const val RED_PIXEL = "red_pixel"
    const val GREEN_PIXEL = "green_pixel"
    const val BLUE_PIXEL = "blue_pixel"

    // Styles
    const val STYLE_BOLD = "bold_label"
    const val STYLE_BOLD_ORANGE = "bold_orange_label"
    const val STYLE_EQUIP_SPECS = "equip_specs"
    const val STYLE_EQUIP_CHOOSE = "equip_choose"
    const val STYLE_BLACK_LABEL = "black_label"
    const val STYLE_MOD_NAME = "mod_name"
    const val STYLE_MOD_LEVEL = "mod_level"
    const val STYLE_DAMAGE_LABEL = "damage_label"
    const val STYLE_EXIT_BTN = "exit_button"
    const val STYLE_RESUME_BTN = "resume_button"
    const val STYLE_RESTART_BTN = "restart_button"
    const val STYLE_NEXT_BTN = "next_button"

    // Control params
    const val CAMERA_RADIUS = 2f
    const val CAMERA_RADIUS_2 = CAMERA_RADIUS * CAMERA_RADIUS
    const val DEAD_BAND = 20f
    const val DEAD_BAND_2 = DEAD_BAND * DEAD_BAND
    const val MAX_TOUCHPAD_RADIUS = 100f
    const val MAX_TOUCHPAD_RADIUS_2 = MAX_TOUCHPAD_RADIUS * MAX_TOUCHPAD_RADIUS


    // Equips
    const val SHIP_DEFAULT = "textures/equips/ship_default.png"
    const val GUN_DEFAULT = "textures/equips/gun_default.png"
    const val GUN_SNIPER = "textures/equips/gun_sniper.png"

    // Fonts
    const val DIGITS_FNT = "fonts/digits.fnt"
    const val DIGITS_PNG = "fonts/digits.png"
    const val ROBOTO_LIGHT_TTF = "fonts/Roboto-Light.ttf"
    const val ROBOTO_BOLD_TTF = "fonts/Roboto-Bold.ttf"
    const val ROBOTO_LIGHT_FONT = "robotoLightFont"
    const val ROBOTO_BOLD_FONT = "robotoBoldFont"

    // Atlases
    const val ATLAS_SKIN = "textures/skins/skin.atlas"

    // Shaders
    const val VERTEX_SHADER = "shaders/shader.vsh"
    const val FRAGMENT_SHADER = "shaders/shader.fsh"

    // Audio
    const val HIT_SOUND = "audio/hit.wav"
    const val SHOT_SOUND = "audio/shot.wav"

    // MISC
    const val LOREM_IPSUM = """Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in
reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint 
occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."""
}