package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.utils.TopViewport
import ktx.actors.onClickEvent
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.scene2d.*
import ktx.style.get
import com.divelix.skitter.container

class TestUIScreen(val game: Main) : KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)
    val content = Container<Table>()

    init {
        val contentTable1 = TrialContent(assets)
        val contentTable2 = scene2d.table { label("second") }
        val contentTable3 = scene2d.table { label("third") }
//        val tabbedMenu = TabbedMenu(context, gdxArrayOf(
//                Constants.SHIP_ICON to scene2d.table { label("Ship tab") },
//                Constants.GUN_ICON to scene2d.table { label("Gun tab") }
//        ))
//        stage += tabbedMenu
        stage.isDebugAll = true
        stage += scene2d.table {
            setFillParent(true)
            top()

//            val tabbedMenu = TabbedMenu(gdxArrayOf(
//                    Tab(assets.manager.get<Texture>(Constants.SHIP_ICON), contentTable1),
//                    Tab(assets.manager.get<Texture>(Constants.GUN_ICON), contentTable2),
//                    Tab(assets.manager.get<Texture>(Constants.MOD_GUN_CAPACITY), contentTable3)
//            ))
//            add(tabbedMenu)

            defaults().growX()
            table {
                touchable = Touchable.enabled
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                image(assets.manager.get<Texture>(Constants.SHIP_ICON))
                        .apply { setScaling(Scaling.fit) }
                        .cell(width = 50f, height = 50f, pad = 8f)
            }.onClickEvent { event, actor ->
                actor.background = null
                content.actor = contentTable1
            }

            table {
                touchable = Touchable.enabled
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                image(assets.manager.get<Texture>(Constants.GUN_ICON))
                        .apply { setScaling(Scaling.fit) }
                        .cell(width = 50f, height = 50f, pad = 8f)
            }.onClickEvent { event, actor ->
                actor.background = null
                content.actor = contentTable2
            }

            table {
                touchable = Touchable.enabled
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                image(assets.manager.get<Texture>(Constants.MOD_GUN_CAPACITY))
                        .apply { setScaling(Scaling.fit) }
                        .cell(width = 50f, height = 50f, pad = 8f)
            }.onClickEvent { event, actor ->
                actor.background = null
                content.actor = contentTable3
            }
            row()
            add(content).colspan(3)
        }
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.6f, 0.5f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, false)
    }

    companion object {
        private val TAG = TestUIScreen::class.simpleName!!
    }
}

class Tab(iconTexture: Texture, val contentTable: Table): Table() {
    init {
        touchable = Touchable.enabled
        add(Image(iconTexture).apply { setScaling(Scaling.fit) }).size(50f).pad(8f)
        onClickEvent { event, actor -> actor.background = null }
    }
}

class TabbedMenu(tabs: Array<Tab>): Table() {
    private val bgDrawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
    private var activeTab = tabs[0]
    private val content = Container<Table>(activeTab.contentTable)

    init {
        defaults().growX()
        tabs.forEach { add(it.apply { background = bgDrawable }) }
        row()
        add(content).colspan(tabs.size)
        // define initial active tab
        switchTo(tabs[0])
    }

    fun switchTo(tab: Tab) {
        activeTab.background = bgDrawable
        activeTab = tab
        activeTab.background = null
        content.actor = activeTab.contentTable
    }
}


class TrialContent(assets: Assets): Table() {
    init {
        val topPart = scene2d.table {
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
            label("A")
//            container(scaledLabel("Description", 0.3f))
            image(assets.manager.get<Texture>(Constants.GUN_DEFAULT))
//            container(scaledLabel("DAMAGE: 100", 0.3f))
            label("B")
        }
        val c = scene2d.container(topPart) { pad(12f) }
        add(c)
    }
}