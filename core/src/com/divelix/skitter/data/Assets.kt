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

    lateinit var skin: Skin
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
        manager.load<TextureAtlas>(Constants.ATLAS_SKIN)
        Atlases.values().forEach {
            manager.load<TextureAtlas>("textures/atlases/" + it() + ".atlas")
        }

//        manager.load<Texture>(Constants.GAMEPLAY_BG)
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
//        manager.load<Texture>(Constants.BULLET_DEFAULT)
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
        val robotoLightFontParams = freeTypeFontParameters(Constants.ROBOTO_LIGHT_TTF) {
            size = 128
            magFilter = Texture.TextureFilter.Linear
            minFilter = Texture.TextureFilter.Linear
        }
        val robotoBoldFontParams = freeTypeFontParameters(Constants.ROBOTO_BOLD_TTF) {
            size = 128
            magFilter = Texture.TextureFilter.Linear
            minFilter = Texture.TextureFilter.Linear
        }
        manager.load<BitmapFont>(Constants.ROBOTO_LIGHT_FONT, robotoLightFontParams)
        manager.load<BitmapFont>(Constants.ROBOTO_BOLD_FONT, robotoBoldFontParams)
    }

    fun setup() {
        skin = skin(manager.get(Constants.ATLAS_SKIN)) {
            color(Constants.ORANGE_COLOR, 1f, 0.6f, 0f)
//            set("aim", manager.get<Texture>(Constants.AIM)) // works for Images, i.e. image("aim")
//            this["aim"] = manager.get<Texture>(Constants.AIM) // same with different syntax

            this[Constants.BLACK_PIXEL_30] = Texture(bgPixel.apply { setColor(Color(0f, 0f, 0f, 0.3f)); fill() })
            this[Constants.BLACK_PIXEL_50] = Texture(bgPixel.apply { setColor(Color(0f, 0f, 0f, 0.5f)); fill() })
            this[Constants.BLACK_PIXEL_70] = Texture(bgPixel.apply { setColor(Color(0f, 0f, 0f, 0.7f)); fill() })
            this[Constants.BLACK_PIXEL] = Texture(bgPixel.apply { setColor(Color(0f, 0f, 0f, 1f)); fill() })
            this[Constants.YELLOW_PIXEL] = Texture(bgPixel.apply { setColor(Color(1f, 1f, 0f, 1f)); fill() })
            this[Constants.WHITE_PIXEL] = Texture(bgPixel.apply { setColor(Color(1f, 1f, 1f, 1f)); fill() })
            this[Constants.LIGHT_GRAY_PIXEL] = Texture(bgPixel.apply { setColor(Color(.7f, .7f, .7f, 1f)); fill() })
            this[Constants.GRAY_PIXEL] = Texture(bgPixel.apply { setColor(Color(.3f, .3f, .3f, 1f)); fill() })
            this[Constants.DARK_GRAY_PIXEL] = Texture(bgPixel.apply { setColor(Color(.17f, .17f, .17f, 1f)); fill() })

            set(Constants.EQUIP_ICON, manager.get<Texture>(Constants.EQUIP_ICON))
            set(Constants.BATTLE_ICON, manager.get<Texture>(Constants.BATTLE_ICON))
            set(Constants.MOD_ICON, manager.get<Texture>(Constants.MOD_ICON))

            label {
                font = manager.get<BitmapFont>(Constants.ROBOTO_LIGHT_FONT).apply {
                    setUseIntegerPositions(false)
                }
                font.data.setScale(Constants.DEFAULT_LABEL_SCALE)
            }
            label(Constants.STYLE_BOLD) {
                font = manager.get<BitmapFont>(Constants.ROBOTO_BOLD_FONT).apply {
                    setUseIntegerPositions(false)
                }
                font.data.setScale(Constants.DEFAULT_LABEL_SCALE)
            }
            label(Constants.STYLE_BOLD_ORANGE, extend = Constants.STYLE_BOLD) {
                fontColor = this@skin[Constants.ORANGE_COLOR]
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
                font = manager.get<BitmapFont>(Constants.ROBOTO_LIGHT_FONT)
            }
            scrollPane {}
            window {
                titleFontColor = Color.RED
                titleFont = manager.get(Constants.ROBOTO_LIGHT_FONT)
                background = TextureRegionDrawable(this@skin.get<Texture>(Constants.BLACK_PIXEL_30))
            }
            window("equip-choose", extend = defaultStyle) {
                background = TextureRegionDrawable(this@skin.get<Texture>(Constants.DARK_GRAY_PIXEL))
            }

        }.apply {
            Atlases.values().forEach {
                addRegions(manager.get("textures/atlases/" + it() + ".atlas"))
            }

        }
        Scene2DSkin.defaultSkin = skin
        VisUI.load(skin)
//        VisUI.load() // loads default vis skin
        VisUI.setDefaultTitleAlign(Align.center)
    }

    override fun dispose() {
        VisUI.dispose()
        skin.dispose()
        digitsFont.dispose()
        manager.dispose()
        frameBuffer.dispose()
    }
}