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
import ktx.log.debug
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
        manager.load<TextureAtlas>(Constants.ATLAS_SKIN) // TODO replace default skin
        Atlases.values().forEach {
            manager.load<TextureAtlas>("textures/atlases/" + it() + ".atlas")
        }
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
        skin = skin {
            Atlases.values().forEach {
                addRegions(manager.get("textures/atlases/" + it() + ".atlas"))
            }
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
            this[Constants.RED_PIXEL] = Texture(bgPixel.apply { setColor(Color(1f, 0f, 0f, 1f)); fill() })
            this[Constants.GREEN_PIXEL] = Texture(bgPixel.apply { setColor(Color(0f, 1f, 0f, 1f)); fill() })
            this[Constants.BLUE_PIXEL] = Texture(bgPixel.apply { setColor(Color(0f, 0f, 1f, 1f)); fill() })

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
            label(Constants.STYLE_EQUIP_SPECS, extend = defaultStyle) {
                fontColor = Color.WHITE
            }
            label(Constants.STYLE_BLACK_LABEL, extend = defaultStyle) {
                fontColor = Color.BLACK
            }
            label(Constants.STYLE_MOD_NAME, extend = defaultStyle) {
                fontColor = Color.YELLOW
            }
            label(Constants.STYLE_MOD_LEVEL, extend = defaultStyle) {
                fontColor = Color.GREEN
            }
            label(Constants.STYLE_DAMAGE_LABEL, extend = defaultStyle) {
                fontColor = Color.RED
            }
            textButton {
                font = manager.get(Constants.ROBOTO_LIGHT_FONT)
                font.data.setScale(0.2f)
                fontColor = Color.WHITE
                over = this@skin[Constants.RED_PIXEL]
                up = this@skin[Constants.GREEN_PIXEL]
                down = this@skin[Constants.BLUE_PIXEL]
            }
            imageButton(Constants.STYLE_EXIT_BTN) {
                imageUp = TextureRegionDrawable(this@skin.get<TextureRegion>(RegionName.EXIT_ICON()))
            }
            imageButton(Constants.STYLE_RESUME_BTN) {
                imageUp = TextureRegionDrawable(this@skin.get<TextureRegion>(RegionName.MENU_PLAY()))
            }
            imageButton(Constants.STYLE_RESTART_BTN) {
                imageUp = TextureRegionDrawable(this@skin.get<TextureRegion>(RegionName.RESTART_ICON()))
            }
            imageButton(Constants.STYLE_NEXT_BTN) {
                imageUp = TextureRegionDrawable(this@skin.get<TextureRegion>(RegionName.NEXT_ICON()))
            }
            scrollPane {}
            window {
                titleFontColor = Color.WHITE
                titleFont = manager.get(Constants.ROBOTO_LIGHT_FONT)
                background = TextureRegionDrawable(this@skin.get<Texture>(Constants.BLACK_PIXEL_30))
            }
            window(Constants.STYLE_EQUIP_CHOOSE, extend = defaultStyle) {
                background = TextureRegionDrawable(this@skin.get<Texture>(Constants.DARK_GRAY_PIXEL))
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