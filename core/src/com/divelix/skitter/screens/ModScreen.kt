package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.ObjectMap
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.Mod
import com.divelix.skitter.ui.ModIcon
import com.divelix.skitter.ui.TabbedBar
import com.divelix.skitter.utils.TopViewport
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.collections.gdxMapOf
import ktx.vis.table

class ModScreen(val game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    private val bigMod = BigMod(ModIcon(Mod(1001, "HEALTH", 5), assets))
    lateinit var descriptionLabel: VisLabel
    val tabs = gdxMapOf<String, Table>()

    private val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
    private val bgDrawable = TextureRegionDrawable(Texture(bgPixel.apply {setColor(Constants.UI_COLOR); fill()}))

    init {
        val shipTable = table {
            setFillParent(true)
            label("ship")
        }
        val gunTable = table {
            setFillParent(true)
            label("gun")
        }
        tabs.put(Constants.SHIPS_TAB, shipTable)
        tabs.put(Constants.GUNS_TAB, gunTable)

        stage += table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                right().pad(12f)
                background = bgDrawable
                label("2500")
            }.cell(fillX = true)
            row()
            table {
                pad(12f)
                image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.SELL_BTN))).cell(width = 76f, height = 76f)
                table {
                    pad(0f, 12f, 0f, 12f)
                    add(bigMod)
                    row()
                    table {
                        background = bgDrawable
                        scrollPane(
                                table {
                                    pad(12f)
                                    descriptionLabel = label("dsfsdf dsfs d sdf sd fsdf sfdf sdfsd sdf hsdhsi hu dhsui hduh sduish udh sudh iush ids").apply {
                                        setWrap(true)
                                        setAlignment(Align.left)
                                    }.cell(width = 126f) // width may be any value
                                }
                        ).cell(width = 150f, height = 78f)
                    }
                }
                image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.UP_BTN))).cell(width = 76f, height = 76f)
            }
            row()
            add(TabbedBar(tabs, assets)).growX()
        }
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(Constants.BG_COLOR.r, Constants.BG_COLOR.g, Constants.BG_COLOR.b, Constants.BG_COLOR.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    inner class BigMod(val modIcon: ModIcon): Group() {
        private val iconHeight = 75f

        init {
            setSize(150f, 150f)
            val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            val bgDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(modIcon.bgColor); fill()}))
            val lvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(modIcon.lvlColor); fill()}))
            val noLvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(modIcon.noLvlColor); fill()}))

            val bg = Image(bgDrawable).apply { setFillParent(true) }
            val texture: Texture = assets.manager.get(modIcon.textureName)
            val aspectRatio = texture.width.toFloat() / texture.height.toFloat()
            val icon = Image(texture).apply {
                setSize(iconHeight * aspectRatio, iconHeight)
                setPosition((this@BigMod.width - width) / 2f, (this@BigMod.height - height) / 2f)
            }
            val levelBars = table {
                bottom().left()
                pad(5f)
                defaults().pad(2f)
                for (i in 1..10) {
                    image(if (i <= modIcon.mod.level) lvlDrawable else noLvlDrawable) {it.size(10f)}
                }
            }

            addActor(bg)
            addActor(icon)
            addActor(levelBars)
        }
    }
}