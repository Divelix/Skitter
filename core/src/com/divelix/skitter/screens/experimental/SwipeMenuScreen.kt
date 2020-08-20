package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.container
import com.divelix.skitter.image
import com.divelix.skitter.utils.TopViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisScrollPane
import ktx.actors.onClickEvent
import ktx.actors.plusAssign
import ktx.actors.txt
import ktx.app.KtxScreen
import ktx.assets.toLocalFile
import ktx.style.defaultStyle
import ktx.collections.*
import ktx.inject.Context
import ktx.json.fromJson
import ktx.scene2d.*
import ktx.scene2d.vis.visTable
import ktx.style.get

class SwipeMenuScreen(game: Main) : KtxScreen {
    val context = game.getContext()
    val batch = context.inject<SpriteBatch>()
    val assets = context.inject<Assets>()
    val json = context.inject<Json>()
    private val aspectRatio = Gdx.graphics.height.toFloat() / Gdx.graphics.width
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_WIDTH * aspectRatio), batch)

    val swipeMenu: SwipeMenu

    init {
        val file = "json/swipe.json".toLocalFile()
        val printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }
        val icon1 = IconData("first", Constants.SHIP_ICON)
        val icon2 = IconData("second", Constants.GUN_ICON)
        val icon3 = IconData("third", Constants.BATTLE_ICON)
        val iconContainer = IconsContainer("container", gdxArrayOf(icon1, icon2, icon3))
        file.writeString(json.prettyPrint(iconContainer, printSettings), false)

        swipeMenu = SwipeMenu(gdxArrayOf(
                Constants.EQUIP_ICON to PageOne(context),
                Constants.BATTLE_ICON to PageTwo(context),
                Constants.MOD_ICON to PageThree(context)))
        stage += swipeMenu
        stage.isDebugAll = true
        val handler = object : InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.NUM_1 -> swipeMenu.scrollPane.scrollX = 0f
                    Input.Keys.NUM_2 -> swipeMenu.scrollPane.scrollX = 350f
                    Input.Keys.NUM_3 -> swipeMenu.scrollPane.scrollX = 700f
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(stage, handler)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(assets.BG_COLOR.r, assets.BG_COLOR.g, assets.BG_COLOR.b, assets.BG_COLOR.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
    }
}

class SwipeMenu(items: Array<Pair<String, Page>>) : Group() {
    val scrollPane: ScrollPane
    val bottomNav: BottomNav

    init {
        setSize(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat())
        val (names, pages) = items.unzip()
        scrollPane = scene2d.scrollPane {
            setFillParent(true)
            setScrollingDisabled(false, true)
            setOverscroll(false, false)
            setScrollbarsVisible(false)
            setFlickScroll(false)
            table {
                pages.forEach { container(it) }
            }
        }
        bottomNav = BottomNav(names.toGdxArray())

        addActor(scrollPane)
        addActor(bottomNav)
    }

    inner class BottomNav(iconDrawableNames: Array<String>) : Group() {
        init {
            width = Constants.D_WIDTH.toFloat()
            height = 50f
            val content = scene2d.table {
                setFillParent(true)
                defaults().expand()
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                iconDrawableNames.forEachIndexed { index, name ->
                    image(name).cell(width = 40f, height = 40f)
                            .onClickEvent { event, actor ->
                                println("[EVENT = $event; ACTOR = $actor]")
                                scrollPane.scrollX = index * Constants.D_WIDTH.toFloat()
                            }
                }
            }
            addActor(content)
        }
    }
}

private data class IconsContainer(var name: String = "defaultContainer", var icons: Array<IconData> = Array())
private data class IconData(var name: String = "noname", var path: String = "no path")

abstract class Page(val context: Context) : Group() {
    val assets = context.inject<Assets>()
    val json = context.inject<Json>()

    init {
        width = Constants.D_WIDTH.toFloat()
        height = Constants.D_HEIGHT.toFloat()
    }
}

class PageOne(context: Context) : Page(context) {
    init {
        val file = "json/swipe.json".toLocalFile()
        val iconsContainer = json.fromJson<IconsContainer>(file)
        val content = scene2d.table {
            setFillParent(true)
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("redBg"))
            image(assets.manager.get<Texture>(iconsContainer.icons[0].path)).cell(width = 100f, height = 100f)
        }
        addActor(content)
    }
}

class PageTwo(context: Context) : Page(context) {
    init {
        val file = "json/swipe.json".toLocalFile()
        val iconsContainer = json.fromJson<IconsContainer>(file)
        val content = scene2d.table {
            setFillParent(true)
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("greenBg"))
            image(assets.manager.get<Texture>(iconsContainer.icons[1].path)).cell(width = 100f, height = 100f)
        }
        addActor(content)
    }
}

class PageThree(context: Context) : Page(context) {
    init {
        val file = "json/swipe.json".toLocalFile()
        val iconsContainer = json.fromJson<IconsContainer>(file)
        val content = scene2d.table {
            setFillParent(true)
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("blueBg"))
            image(assets.manager.get<Texture>(iconsContainer.icons[2].path)).cell(width = 100f, height = 100f)
        }
        addActor(content)
    }
}