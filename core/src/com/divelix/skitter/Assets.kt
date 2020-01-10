package com.divelix.skitter

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Window
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
    //    lateinit var font: BitmapFont
    lateinit var digitsFont: BitmapFont

    val BG_COLOR = Color(0x684BA6FF)
    val UI_COLOR = Color(0f, 0f, 0f, 0.3f)
    val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
    val bgDrawable = TextureRegionDrawable(Texture(bgPixel.apply {setColor(UI_COLOR); fill()}))

    fun loadSplashAssets() {
        manager.load<Texture>(Constants.DIGITS_PNG)
        manager.finishLoading()
        digitsFont = BitmapFont(file(Constants.DIGITS_FNT), TextureRegion(manager.get<Texture>(Constants.DIGITS_PNG)))
    }

    fun loadAssets() {
        manager.load<TextureAtlas>(Constants.UISKIN_ATLAS)
        manager.load<Texture>(Constants.BACKGROUND_IMAGE)
        manager.load<Texture>(Constants.SHIP_ICON)
        manager.load<Texture>(Constants.GUN_ICON)
        manager.load<Texture>(Constants.APPLY_ICON)
        manager.load<Texture>(Constants.UP_BTN)
        manager.load<Texture>(Constants.SELL_BTN)
        manager.load<Texture>(Constants.PAUSE_BTN)
        manager.load<Texture>(Constants.BUCKET_ICON)
        manager.load<Texture>(Constants.PISTOL_ICON)
        manager.load<Texture>(Constants.CARRIAGE)
        manager.load<Texture>(Constants.ENEMY_DEFAULT)
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
        val fontParams12 = freeTypeFontParameters(Constants.ROBOTO_FONT) {
            size = 48
        }
        val fontParams16 = freeTypeFontParameters(Constants.ROBOTO_FONT) {
            size = 16
        }
        val fontParams32 = freeTypeFontParameters(Constants.ROBOTO_FONT) {
            size = 64
            characters = "0123456789"
            magFilter = Texture.TextureFilter.Linear
            minFilter = Texture.TextureFilter.Linear
        }
        manager.load<BitmapFont>(Constants.ROBOTO_ALIAS_QUANTITY, fontParams12)
        manager.load<BitmapFont>(Constants.ROBOTO_ALIAS_DEFAULT, fontParams16)
        manager.load<BitmapFont>(Constants.ROBOTO_ALIAS_RELOAD, fontParams32)
    }

    fun setup() {
        uiSkin = skin(manager.get(Constants.UISKIN_ATLAS)) {
            color("pinky", 0.7f, 0f, 1f)
            label {
                font = manager.get<BitmapFont>(Constants.ROBOTO_ALIAS_DEFAULT)
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
            button {
                up = it["button"]
                down = it["button-blue"]
            }
            list {
                font = manager.get<BitmapFont>(Constants.ROBOTO_ALIAS_DEFAULT)
                fontColorSelected = Color.BLACK
                fontColorUnselected = Color.WHITE
                background = it["select-box-list-bg"]
                selection = it["list-selection"]
            }
            val btn = visTextButton {
                up = it["button"]
                font = manager.get<BitmapFont>(Constants.ROBOTO_ALIAS_DEFAULT)
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
                titleFont = manager.get<BitmapFont>(Constants.ROBOTO_ALIAS_DEFAULT)
                background = bgDrawable
            }
            tabbedPane("vertical") {
                background = it["window-border-bg"]
                separatorBar = it["separator"]
                buttonStyle = btn
                draggable = false
                vertical = true
            }
            sizes {
                borderSize = 1f
            }
            visImageButton {
                up = it["button"]
                down = it["button-down"]
            }
            visImageButton("close-active-tab", extend = defaultStyle)
            visImageButton("close", extend = defaultStyle)

        }
        VisUI.load(uiSkin)
//        VisUI.load() //TODO test tabs and delete
        VisUI.setDefaultTitleAlign(Align.center)
//        font = manager.get("myFont.ttf")
//        val fontMap = ObjectMap<String, Any>()
//        fontMap.put("default-font", font)
//        val skinParameter = SkinLoader.SkinParameter(fontMap)
//        manager.load(Resources.UISKIN, Skin::class.java, skinParameter)
//        manager.finishLoading()
//        uiSkin = manager.get(Resources.UISKIN)
    }

    override fun dispose() {
        VisUI.dispose()
        uiSkin.dispose()
//        font.dispose()
        digitsFont.dispose()
        manager.dispose()
    }
}