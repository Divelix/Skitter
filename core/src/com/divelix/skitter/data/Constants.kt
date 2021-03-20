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

    // UI values
    const val DEFAULT_LABEL_SCALE = 0.1f
    const val UI_MARGIN = 12f
    const val UI_PADDING = 7f
    const val MOD_SIZE = 64f
    const val MOD_WIDTH = 64f // TODO remove
    const val MOD_HEIGHT = 64f // TODO remove
    const val SHIPS_TAB = "ShipsTab"
    const val GUNS_TAB = "GunsTab"
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

    // Styles
    const val STYLE_BOLD = "bold_label"
    const val STYLE_BOLD_ORANGE = "bold_orange_label"
    const val STYLE_EQUIP_CHOOSE = "equip_choose"
    const val STYLE_MOD_NAME = "mod_name"

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

    // Gameplay
//    const val BULLET_DEFAULT = "textures/gameplay/bullet_default.png"
//    const val GAMEPLAY_BG = "textures/gameplay/dark_honeycomb.png"

    // UI textures
    const val CARRIAGE = "textures/ui/carriage.png"
    const val AIM = "textures/ui/aim_64.png"
    const val EQUIP_ICON = "textures/ui/equip_icon.png"
    const val BATTLE_ICON = "textures/ui/battle_icon.png"
    const val MOD_ICON = "textures/ui/mod_icon.png"
    const val WHITE_CIRCLE = "textures/ui/white_circle.png"
    const val SHIP_ICON = "textures/ui/ship_icon.png"
    const val GUN_ICON = "textures/ui/gun_icon.png"
    const val APPLY_ICON = "textures/ui/apply_icon.png"
    const val HOME_ICON = "textures/ui/home_icon.png"
    const val RESTART_ICON = "textures/ui/restart_icon.png"
    const val UP_BTN = "textures/ui/up_btn.png"
    const val SELL_BTN = "textures/ui/sell_btn.png"
    const val PAUSE_BTN = "textures/ui/pause.png"
    const val MENU_PLAY = "textures/ui/menu_play.png"
    const val MENU_MOD = "textures/ui/menu_mod.png"
    const val MENU_EQUIP = "textures/ui/menu_equip.png"
    const val STAR = "textures/mods/star.png"

    // Fonts
    const val DIGITS_FNT = "fonts/digits.fnt"
    const val DIGITS_PNG = "fonts/digits.png"
    const val ROBOTO_LIGHT_TTF = "fonts/Roboto-Light.ttf"
    const val ROBOTO_BOLD_TTF = "fonts/Roboto-Bold.ttf"
    const val ROBOTO_LIGHT_FONT = "robotoLightFont"
    const val ROBOTO_BOLD_FONT = "robotoBoldFont"

    // Atlases
    const val ATLAS_SKIN = "textures/skins/skin.atlas"
    const val ATLAS_UI = "textures/atlases/ui.atlas"
    const val ATLAS_EQUIPS = "textures/atlases/equips.atlas"
    const val ATLAS_MODS = "textures/atlases/mods.atlas"
    const val ATLAS_GAMEPLAY = "textures/atlases/gameplay.atlas"
    const val ATLAS_ENEMIES = "textures/atlases/enemies.atlas"

    // Shaders
    const val VERTEX_SHADER = "shaders/shader.vsh"
    const val FRAGMENT_SHADER = "shaders/shader.fsh"

    // Mods
    const val MOD_SHIP_HEALTH = "textures/mods/mod_ship_health.png"
    const val MOD_SHIP_SPEED = "textures/mods/mod_ship_speed.png"
    const val MOD_SHIP_CHUBBER = "textures/mods/mod_ship_chubber.png"
    const val MOD_GUN_DAMAGE = "textures/mods/mod_gun_damage.png"
    const val MOD_GUN_CAPACITY = "textures/mods/mod_gun_capacity.png"
    const val MOD_GUN_RELOAD = "textures/mods/mod_gun_reload.png"
    const val MOD_GUN_SPEED = "textures/mods/mod_gun_speed.png"
    const val MOD_GUN_CRIT = "textures/mods/mod_gun_crit.png"
    const val MOD_GUN_CHANCE = "textures/mods/mod_gun_chance.png"

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

    // MISC
    const val LOREM_IPSUM = """Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud
exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in
reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint 
occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."""
}