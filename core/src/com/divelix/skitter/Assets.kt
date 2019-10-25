package com.divelix.skitter

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Disposable
import com.kotcrab.vis.ui.VisUI
import ktx.assets.*
import ktx.freetype.loadFreeTypeFont
import ktx.freetype.registerFreeTypeFontLoaders
import ktx.style.*

//creation of a singleton
class Assets: Disposable {

    var manager: AssetManager = AssetManager()
        private set

    lateinit var uiSkin: Skin
    //    lateinit var font: BitmapFont
    lateinit var digitsFont: BitmapFont
    lateinit var windowStyle: Window.WindowStyle // TODO ugly shit, delete later

    fun loadSplashAssets() {
        manager.load<Texture>(Constants.LOADING_IMAGE)
        manager.load<Texture>(Constants.DIGITS_PNG)
        manager.finishLoading()
        digitsFont = BitmapFont(file(Constants.DIGITS_FNT), TextureRegion(manager.get<Texture>(Constants.DIGITS_PNG)))
    }

    fun loadAssets() {
        manager.load<TextureAtlas>(Constants.UISKIN_ATLAS)
        manager.load<Texture>(Constants.BACKGROUND_IMAGE)
        manager.load<Texture>(Constants.SKIN_GRAD)
        manager.load<Texture>(Constants.BACK_BTN)
        manager.load<Texture>(Constants.APPLY_BTN)
        manager.load<Texture>(Constants.UP_BTN)
        manager.load<Texture>(Constants.SELL_BTN)
        manager.load<Texture>(Constants.WEAPON_ICON)
        manager.load<Texture>(Constants.SHIPS_ICON)
        manager.load<Texture>(Constants.GUNS_ICON)
        manager.load<Texture>(Constants.MODS_ICON)
        manager.load<Texture>(Constants.PLAYER_DEFAULT)
        manager.load<Texture>(Constants.ENEMY_DEFAULT)
        manager.load<Texture>(Constants.BULLET_DEFAULT)
        manager.load<Texture>(Constants.AIM)
        manager.load<Texture>(Constants.RIFLE)
        manager.load<Texture>(Constants.UZI)
        manager.load<Texture>(Constants.MOD_GLOW)
        manager.load<Texture>(Constants.MOD_RELOAD_SPEED)
        manager.load<Texture>(Constants.MOD_BULLET_SPEED)
        manager.load<Texture>(Constants.MOD_COLD_DAMAGE)
        manager.load<Texture>(Constants.MOD_DAMAGE)
        manager.load<Texture>(Constants.MOD_FIRE_DAMAGE)
        manager.load<Texture>(Constants.MOD_HEALTH)
        manager.load<Texture>(Constants.MOD_MANA)
        manager.load<Sound>(Constants.HIT_SOUND)
        manager.registerFreeTypeFontLoaders(replaceDefaultBitmapFontLoader = true)
        manager.loadFreeTypeFont(Constants.ROBOTO_FONT) {
            size = 16
        }
//        loadFonts() //TODO try move to the top
    }

//    private fun loadFonts() {
//        val resolver = InternalFileHandleResolver()
//        manager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
//        manager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))
//        val fontLP = FreetypeFontLoader.FreeTypeFontLoaderParameter()
//        fontLP.fontFileName = Resources.MAIN_FONT
////        fontLP.fontParameters.size = 80
//        fontLP.fontParameters.size = 20
//        manager.load("myFont.ttf", BitmapFont::class.java, fontLP)
//    }

    fun setup() {
        uiSkin = skin(manager.get(Constants.UISKIN_ATLAS)) {
            color("pinky", 0.7f, 0f, 1f)
            label {
                font = manager.get<BitmapFont>(Constants.ROBOTO_FONT)
                fontColor = Color.WHITE
            }
            label("mod-level", extend = defaultStyle) {
                fontColor = Color.GREEN
            }
            label("mod-quantity", extend = defaultStyle) {
                fontColor = Color.YELLOW
            }
            button {
                up = it["button"]
                down = it["button-blue"]
            }
            list {
                font = BitmapFont()
                fontColorSelected = Color.BLACK
                fontColorUnselected = Color.WHITE
                background = it["select-box-list-bg"]
                selection = it["list-selection"]
            }
            val btn = visTextButton {
                up = it["button"]
            }
            textButton {  }
            scrollPane {}
            windowStyle = window {
                titleFont = manager.get<BitmapFont>(Constants.ROBOTO_FONT)
                background = TextureRegionDrawable(manager.get<Texture>(Constants.BACKGROUND_IMAGE))
            }
            tabbedPane {
                background = it["window-border-bg"]
                draggable = false
                separatorBar = it["separator"]
                vertical = true
                buttonStyle = btn
            }
            sizes {
                borderSize = 1f
            }
            visImageButton {  }

        }
        VisUI.load(uiSkin)
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