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
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
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
import ktx.style.defaultStyle
import ktx.collections.*
import ktx.scene2d.*
import ktx.scene2d.vis.visTable
import ktx.style.get

class SwipeMenuScreen(game: Main) : KtxScreen {
    val context = game.getContext()
    val batch = context.inject<SpriteBatch>()
    val assets = context.inject<Assets>()
    private val aspectRatio = Gdx.graphics.height.toFloat() / Gdx.graphics.width
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_WIDTH * aspectRatio), batch)

    val bgPixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    val redBg = TextureRegionDrawable(Texture(bgPixel.apply { setColor(Color(1f, 0f, 0f, 0.3f)); fill() }))
    val greenBg = TextureRegionDrawable(Texture(bgPixel.apply { setColor(Color(0f, 1f, 0f, 0.3f)); fill() }))
    val blueBg = TextureRegionDrawable(Texture(bgPixel.apply { setColor(Color(0f, 0f, 1f, 0.3f)); fill() }))

    val swipeMenu: SwipeMenu

    init {
        swipeMenu = SwipeMenu(gdxArrayOf(
                Constants.EQUIP_ICON to Page(redBg),
                Constants.BATTLE_ICON to Page(greenBg),
                Constants.MOD_ICON to Page(blueBg)))
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
        swipeMenu.bottomNav.y = 0f
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
        bottomNav = BottomNav(names.toGdxArray(), scrollPane)

        addActor(scrollPane)
        addActor(bottomNav)
    }
}

class Page(val bg: Drawable) : Group() {
    init {
        width = Constants.D_WIDTH.toFloat()
        height = Constants.D_HEIGHT.toFloat()
        val content = scene2d.table {
            setFillParent(true)
            background = bg
        }
        addActor(content)
    }
}

class BottomNav(iconDrawableNames: Array<String>, scrollPane: ScrollPane) : Group() {
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