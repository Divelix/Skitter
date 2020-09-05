package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
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
import ktx.collections.gdxArrayOf

class TestUIScreen(val game: Main) : KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(TopViewport(Constants.STAGE_WIDTH.toFloat(), Constants.STAGE_HEIGHT.toFloat()), batch)
    val content = Container<Table>()

    init {
//        stage.isDebugAll = true
        stage += scene2d.table {
            setFillParent(true)
            top()

            val tabbedMenu = TabbedMenu(gdxArrayOf(
                    Tab(assets.manager.get<Texture>(Constants.SHIP_ICON),        TrialContent(assets)),
                    Tab(assets.manager.get<Texture>(Constants.GUN_ICON),         scene2d.table { label("second") }),
                    Tab(assets.manager.get<Texture>(Constants.MOD_GUN_CAPACITY), scene2d.table { label("third") })
            ))
            add(tabbedMenu)
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
        tabs.forEach {
            add(it.apply {
                background = bgDrawable
                onClickEvent { event, actor -> switchTo(it) }
            })
        }
        row()
        add(content).colspan(tabs.size).width(Constants.STAGE_WIDTH.toFloat())
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
        padTop(12f)
        val topPart = scene2d.table {
            pad(7f)
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
            table {// info table
                pad(7f)
                scrollPane {
                    scaledLabel(Constants.LOREM_IPSUM, 0.1f).apply {
                        wrap = true
                        setAlignment(Align.top)
                    }
                }.cell(width = 92f, height = 100f, padRight = 7f)
                table {
                    touchable = Touchable.enabled
                    background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                    image(assets.manager.get<Texture>(Constants.GUN_DEFAULT)).apply { setScaling(Scaling.fit) }.cell(pad = 7f)
                    onClickEvent { event, actor -> println("Equip icon clicked") }
                }.cell(width = 100f, height = 100f)
                table {
                    padLeft(7f)
                    table {
                        defaults().left()
                        scaledLabel("DAMAGE: ", Constants.SPECS_SCALE); row()
                        scaledLabel("CAPACITY: ", Constants.SPECS_SCALE); row()
                        scaledLabel("RELOAD: ", Constants.SPECS_SCALE); row()
                        scaledLabel("SPEED: ", Constants.SPECS_SCALE); row()
                        scaledLabel("CRITICAL: ", Constants.SPECS_SCALE); row()
                        scaledLabel("CHANCE: ", Constants.SPECS_SCALE); row()
                    }
                    table {
                        defaults().left()
                        scaledLabel("100", Constants.SPECS_SCALE); row()
                        scaledLabel("13", Constants.SPECS_SCALE); row()
                        scaledLabel("0.5", Constants.SPECS_SCALE); row()
                        scaledLabel("10", Constants.SPECS_SCALE); row()
                        scaledLabel("x2.0", Constants.SPECS_SCALE); row()
                        scaledLabel("20%", Constants.SPECS_SCALE); row()
                    }
                }
            }
        }
        add(topPart)
    }
}