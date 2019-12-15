package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.EquipTable
import com.divelix.skitter.utils.TopViewport
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.vis.table

class EquipScreen(val game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    val ships: Table
    val guns: Table
    lateinit var shipTab: Container<NavButton>
    lateinit var gunTab: Container<NavButton>
    lateinit var content: Container<Table>

    init {
        ships = EquipTable(Constants.SHIPS_TAB, assets)
        guns = EquipTable(Constants.GUNS_TAB, assets)
        stage += table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                shipTab = container(NavButton(Constants.SHIPS_TAB, true))
                gunTab = container(NavButton(Constants.GUNS_TAB))
            }
            row()
            content = container(ships)
        }
        val handler = object: InputAdapter() {
            override fun keyUp(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK -> game.screen = MenuScreen(game)
//                    Input.Keys.TAB -> switchTab()
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(handler, stage)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.6f, 0.5f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    inner class NavButton(val tabName: String, active: Boolean = false): Group() {
        val upColor = Color(0f, 0f, 0f, 0.2f)
        val downColor = Color(0f, 0f, 0f, 0f)
        val bg: Image
        val texture: Texture
        val icon: Image
        val btnSize = Vector2(Constants.D_WIDTH / 2f, 50f)
        val iconSize = 40f

        init {
            setSize(btnSize.x, btnSize.y)
            // Background
            val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
            val upDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(upColor); fill() }))
            val downDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(downColor); fill() }))
            val activeDrawable = if (active) downDrawable else upDrawable
            bg = Image(activeDrawable).apply { setFillParent(true) }
            // Icon
            texture = when(tabName) {
                Constants.SHIPS_TAB -> assets.manager.get<Texture>(Constants.SHIP_ICON)
                Constants.GUNS_TAB -> assets.manager.get<Texture>(Constants.GUN_ICON)
                else -> error {"No texture for tab"}
            }
            icon = Image(texture).apply {
                setSize(iconSize, iconSize)
                setPosition(btnSize.x/2f - width/2f, btnSize.y/2f - height/2f)
            }
            addActor(bg)
            addActor(icon)
            addListener(object: ClickListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    if (tabName == content.actor.name) return super.touchDown(event, x, y, pointer, button)
                    when (tabName) {
                        Constants.SHIPS_TAB -> {
                            shipTab.actor.bg.drawable = downDrawable
                            gunTab.actor.bg.drawable = upDrawable
                            content.actor = ships
                        }
                        Constants.GUNS_TAB -> {
                            gunTab.actor.bg.drawable = downDrawable
                            shipTab.actor.bg.drawable = upDrawable
                            content.actor = guns
                        }
                    }
                    return super.touchDown(event, x, y, pointer, button)
                }
            })
        }


    }
}