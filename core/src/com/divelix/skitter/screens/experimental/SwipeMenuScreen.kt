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
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.container
import com.divelix.skitter.utils.TopViewport
import com.kotcrab.vis.ui.VisUI
import com.kotcrab.vis.ui.widget.VisScrollPane
import ktx.actors.plusAssign
import ktx.actors.txt
import ktx.app.KtxScreen
import ktx.style.defaultStyle
import ktx.collections.*
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.scene2d.vis.visTable

class SwipeMenuScreen(game: Main): KtxScreen {
    val context = game.getContext()
    val batch = context.inject<SpriteBatch>()
    val assets = context.inject<Assets>()
    private val aspectRatio = Gdx.graphics.height.toFloat() / Gdx.graphics.width
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_WIDTH * aspectRatio), batch)

    private val infoLabel: Label

    val bgPixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    val redBg = TextureRegionDrawable(Texture(bgPixel.apply { setColor(Color(1f, 0f, 0f, 0.3f)); fill() }))
    val greenBg = TextureRegionDrawable(Texture(bgPixel.apply { setColor(Color(0f, 1f, 0f, 0.3f)); fill() }))
    val blueBg = TextureRegionDrawable(Texture(bgPixel.apply { setColor(Color(0f, 0f, 1f, 0.3f)); fill() }))

    val scrollP: ScrollPane

    init {
        scrollP = scene2d.scrollPane {
            setFillParent(true)
            setScrollingDisabled(false, true)
            setOverscroll(false, false)
            setScrollbarsVisible(false)
            setFlickScroll(false)
            table {
                container(Page(redBg))
                container(Page(greenBg))
                container(Page(blueBg))
            }
        }
        infoLabel = Label("Scroll: ${scrollP.scrollX}", VisUI.getSkin())
        stage += scrollP
        stage += infoLabel
        stage.isDebugAll = true
        val handler = object: InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.NUM_1 -> scrollP.scrollX = 0f
                    Input.Keys.NUM_2 -> scrollP.scrollX = 350f
                    Input.Keys.NUM_3 -> scrollP.scrollX = 700f
                    Input.Keys.NUM_4 -> println("QWERTYUIOP")
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

        infoLabel.txt = "Scroll: ${scrollP.scrollX}"
        infoLabel.pack()
        stage.act()
        stage.draw()
    }
}

class Page(val bg: Drawable): Group() {
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

class PagedScrollPane(val pages: Array<Table>): VisScrollPane(null, defaultStyle) {
    val bigTable: Table
    var isFlinged = false

    init {
        setScrollingDisabled(false, true)
        setOverscroll(false, false)
        setScrollbarsVisible(false)
        bigTable = scene2d.table {
            pages.forEach { add(it) }
        }
        super.setActor(bigTable)
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (isFlinged && isFlinging) {
            isFlinged = false
            println("ACTION")
            scrollFor(velocityX)
        } else {
            if (!isFlinging) isFlinged = true
        }
    }

    fun scrollFor(flingVelocity: Float) {
        val currentPageIndex = MathUtils.floor(scrollX / width)
        var desiredPageIndex = currentPageIndex
        if (flingVelocity < 0f) {
            // move to right closest
            desiredPageIndex++
        } else {
            // move to left closest
            desiredPageIndex--
        }
        println("desired page:$desiredPageIndex")
        scrollX = desiredPageIndex * width
    }


}