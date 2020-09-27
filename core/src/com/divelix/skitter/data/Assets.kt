package com.divelix.skitter.data

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
import ktx.scene2d.Scene2DSkin
import ktx.style.*

//creation of a singleton
class Assets: Disposable {

    var manager: AssetManager = AssetManager()
        private set

    lateinit var uiSkin: Skin
    lateinit var digitsFont: BitmapFont

    val bgColor = Color(0x684BA6FF)
    val bgPixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)

    val frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)

    fun loadSplashAssets() {
        manager.load<Texture>(Constants.DIGITS_PNG)
        manager.finishLoading()
        digitsFont = BitmapFont(file(Constants.DIGITS_FNT), TextureRegion(manager.get<Texture>(Constants.DIGITS_PNG)))
    }

    fun loadAssets() {
        manager.load<TextureAtlas>(Constants.ATLAS_UI)
        manager.load<Texture>(Constants.BACKGROUND_IMAGE)
        manager.load<Texture>(Constants.EQUIP_ICON)
        manager.load<Texture>(Constants.BATTLE_ICON)
        manager.load<Texture>(Constants.MOD_ICON)
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
        manager.load<Texture>(Constants.GUN_SNIPER)
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
        val defaultFontParams = freeTypeFontParameters(Constants.ROBOTO_FONT) {
            size = 128
            magFilter = Texture.TextureFilter.Linear
            minFilter = Texture.TextureFilter.Linear
        }
        manager.load<BitmapFont>(Constants.ROBOTO_ALIAS_DEFAULT, defaultFontParams)
    }

    fun setup() {
        uiSkin = skin(manager.get(Constants.ATLAS_UI)) {
//            color("pinky", 0.7f, 0f, 1f)
//            set("aim", manager.get<Texture>(Constants.AIM)) // works for Images, i.e. image("aim")
//            this["aim"] = manager.get<Texture>(Constants.AIM) // same with different syntax

            this[Constants.BLACK_COLOR_30] = Texture(bgPixel.apply { setColor(Color(0f, 0f, 0f, 0.3f)); fill() })
            this[Constants.BLACK_COLOR_50] = Texture(bgPixel.apply { setColor(Color(0f, 0f, 0f, 0.5f)); fill() })
            this[Constants.BLACK_COLOR_70] = Texture(bgPixel.apply { setColor(Color(0f, 0f, 0f, 0.7f)); fill() })
            this[Constants.RED_COLOR_30] = Texture(bgPixel.apply { setColor(Color(1f, 0f, 0f, 0.3f)); fill() })
            this[Constants.GREEN_COLOR_30] = Texture(bgPixel.apply { setColor(Color(0f, 1f, 0f, 0.3f)); fill() })
            this[Constants.BLUE_COLOR_30] = Texture(bgPixel.apply { setColor(Color(0f, 0f, 1f, 0.3f)); fill() })

            set(Constants.EQUIP_ICON, manager.get<Texture>(Constants.EQUIP_ICON))
            set(Constants.BATTLE_ICON, manager.get<Texture>(Constants.BATTLE_ICON))
            set(Constants.MOD_ICON, manager.get<Texture>(Constants.MOD_ICON))

            label {
                font = manager.get<BitmapFont>(Constants.ROBOTO_ALIAS_DEFAULT).apply {
                    setUseIntegerPositions(false)
                }
                fontColor = Color.WHITE
            }
            label("equip-specs", extend = defaultStyle) {
                fontColor = Color.WHITE
            }
            label("black", extend = defaultStyle) {
                fontColor = Color.BLACK
            }
            label("mod-name", extend = defaultStyle) {
                fontColor = Color.YELLOW
            }
            label("mod-level", extend = defaultStyle) {
                fontColor = Color.GREEN
            }
            label("damage-label", extend = defaultStyle) {
                fontColor = Color.RED
            }
            textButton {
                font = manager.get<BitmapFont>(Constants.ROBOTO_ALIAS_DEFAULT)
                font.data.setScale(Constants.DEFAULT_LABEL_SCALE)
            }
            scrollPane {}
            window {
                titleFontColor = Color.RED
                titleFont = manager.get(Constants.ROBOTO_ALIAS_DEFAULT)
                background = TextureRegionDrawable(this@skin.get<Texture>(Constants.BLACK_COLOR_30))
            }
            window("equip-choose", extend = defaultStyle) {
                background = TextureRegionDrawable(this@skin.get<Texture>(Constants.BLACK_COLOR_70))
            }

        }
        Scene2DSkin.defaultSkin = uiSkin
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