package com.divelix.skitter

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Disposable
import com.kotcrab.vis.ui.VisUI
import ktx.assets.*
import ktx.freetype.freeTypeFontParameters
import ktx.freetype.registerFreeTypeFontLoaders
import ktx.style.*

//creation of a singleton
class Assets: Disposable {

    var manager: AssetManager = AssetManager()
        private set

    lateinit var uiSkin: Skin
    lateinit var digitsFont: BitmapFont

    val BG_COLOR = Color(0x684BA6FF)
    val UI_COLOR = Color(0f, 0f, 0f, 0.3f)
    val DOWN_COLOR = Color(0f, 0f, 0f, 0.5f)
    val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
    val bgDrawable = TextureRegionDrawable(Texture(bgPixel.apply {setColor(UI_COLOR); fill()}))
    val downDrawable = TextureRegionDrawable(Texture(bgPixel.apply {setColor(DOWN_COLOR); fill()}))

    val frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)

    fun loadSplashAssets() {
        manager.load<Texture>(Constants.DIGITS_PNG)
        manager.finishLoading()
        digitsFont = BitmapFont(file(Constants.DIGITS_FNT), TextureRegion(manager.get<Texture>(Constants.DIGITS_PNG)))
    }

    fun loadAssets() {
        manager.load<TextureAtlas>(Constants.ATLAS_UI)
        manager.load<Texture>(Constants.BACKGROUND_IMAGE)
        manager.load<Texture>(Constants.SHIP_ICON)
        manager.load<Texture>(Constants.GUN_ICON)
        manager.load<Texture>(Constants.APPLY_ICON)
        manager.load<Texture>(Constants.HOME_ICON)
        manager.load<Texture>(Constants.RESTART_ICON)
        manager.load<Texture>(Constants.UP_BTN)
        manager.load<Texture>(Constants.SELL_BTN)
        manager.load<Texture>(Constants.PAUSE_BTN)
        manager.load<Texture>(Constants.SHIP_DEFAULT)
        manager.load<Texture>(Constants.GUN_DEFAULT)
        manager.load<Texture>(Constants.CARRIAGE)
        manager.load<Texture>(Constants.AGENT)
        manager.load<Texture>(Constants.SNIPER_BASE)
        manager.load<Texture>(Constants.SNIPER_TOWER)
        manager.load<Texture>(Constants.WOMB)
        manager.load<Texture>(Constants.KID)
        manager.load<Texture>(Constants.RADIAL)
        manager.load<Texture>(Constants.JUMPER)
        manager.load<Texture>(Constants.BULLET_DEFAULT)
        manager.load<Texture>(Constants.AIM)
        manager.load<Texture>(Constants.WHITE_CIRCLE)
        manager.load<Texture>(Constants.MENU_PLAY)
        manager.load<Texture>(Constants.MENU_MOD)
        manager.load<Texture>(Constants.MENU_EQUIP)
        manager.load<Texture>(Constants.MOD_SHIP_HEALTH)
        manager.load<Texture>(Constants.MOD_SHIP_SPEED)
        manager.load<Texture>(Constants.MOD_SHIP_CHUBBER)
        manager.load<Texture>(Constants.MOD_GUN_DAMAGE)
        manager.load<Texture>(Constants.MOD_GUN_CAPACITY)
        manager.load<Texture>(Constants.MOD_GUN_RELOAD)
        manager.load<Texture>(Constants.MOD_GUN_SPEED)
        manager.load<Texture>(Constants.MOD_GUN_CRIT)
        manager.load<Texture>(Constants.MOD_GUN_CHANCE)
        manager.load<Texture>(Constants.STAR)
        manager.load<Sound>(Constants.HIT_SOUND)
        manager.load<Sound>(Constants.SHOT_SOUND)
        manager.registerFreeTypeFontLoaders(replaceDefaultBitmapFontLoader = true)
        val fontParams48 = freeTypeFontParameters(Constants.ROBOTO_FONT) {
            size = 48
        }
        val fontParams32 = freeTypeFontParameters(Constants.ROBOTO_FONT) {
            size = 32
        }
        val fontParams64 = freeTypeFontParameters(Constants.ROBOTO_FONT) {
            size = 64
            characters = "0123456789"
            magFilter = Texture.TextureFilter.Linear
            minFilter = Texture.TextureFilter.Linear
        }
        manager.load<BitmapFont>(Constants.ROBOTO_ALIAS_QUANTITY, fontParams48)
        manager.load<BitmapFont>(Constants.ROBOTO_ALIAS_DEFAULT, fontParams32)
        manager.load<BitmapFont>(Constants.ROBOTO_ALIAS_RELOAD, fontParams64)
    }

    fun setup() {
        uiSkin = skin(manager.get(Constants.ATLAS_UI)) {
            color("pinky", 0.7f, 0f, 1f)
            label {
                font = manager.get(Constants.ROBOTO_ALIAS_DEFAULT)
                fontColor = Color.WHITE
            }
            label("score-label", extend = defaultStyle) {
                fontColor = Color.BLACK
            }
            label("mod-name", extend = defaultStyle) {
                fontColor = Color.YELLOW
            }
            label("mod-level", extend = defaultStyle) {
                fontColor = Color.GREEN
            }
            label("equip-specs") {
                font = manager.get<BitmapFont>(Constants.ROBOTO_ALIAS_QUANTITY)
                fontColor = Color.WHITE
            }
            label("reload-label") {
                font = manager.get<BitmapFont>(Constants.ROBOTO_ALIAS_RELOAD)
                fontColor = Color.BLACK
            }
            label("damage-label", extend = "reload-label") {
                fontColor = Color.WHITE
            }
            button {
                up = it["button"]
                down = it["button-blue"]
            }
            list {
                font = manager.get(Constants.ROBOTO_ALIAS_DEFAULT)
                fontColorSelected = Color.BLACK
                fontColorUnselected = Color.WHITE
                background = it["select-box-list-bg"]
                selection = it["list-selection"]
            }
            visTextButton {
                focusBorder = it["border"]
                down = it["button-down"]
                up = it["button"]
                over = it["button-over"]
                disabled = it["button"]
                font = manager.get<BitmapFont>(Constants.ROBOTO_ALIAS_DEFAULT)
                fontColor = Color.WHITE
                disabledFontColor = Color.GRAY
            }
            slider("default-horizontal") {
                background = it["slider"]
                knob = it["slider-knob"]
                knobOver = it["slider-knob-over"]
                knobDown = it["slider-knob-down"]
                disabledKnob = it["slider-knob-disabled"]
            }
            slider("default-vertical", extend = "default-horizontal") {
                background = it["slider-vertical"]
            }
            scrollPane {}
            window {
                titleFontColor = Color.RED
                titleFont = manager.get(Constants.ROBOTO_ALIAS_DEFAULT)
                background = bgDrawable
            }

        }
        VisUI.load(uiSkin)
//        VisUI.load() // loads default vis skin
        VisUI.setDefaultTitleAlign(Align.center)
    }

    override fun dispose() {
        VisUI.dispose()
        uiSkin.dispose()
        digitsFont.dispose()
        manager.dispose()
        frameBuffer.dispose()
    }
}