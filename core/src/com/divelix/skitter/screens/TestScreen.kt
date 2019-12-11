package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.Mod
import com.divelix.skitter.utils.TopViewport
import com.kotcrab.vis.ui.widget.VisLabel
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.vis.table

class TestScreen(val game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    init {
        val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha).apply {
            setColor(Color(0f, 0f, 0f, 0.3f))
            fill()
        }
        val bgDrawable = TextureRegionDrawable(Texture(bgPixel))
        val rootTable = table {
            setFillParent(true)
            top()
            padTop(12f)
            table {
                name = "TopPart"
                debugAll()
                background = bgDrawable
                table {
                    pad(14f, 14f, 7f, 14f)
                    label("Description")
                    image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.PLAYER_DEFAULT))) { cell ->
                        cell.size(64f, 64f)
                    }
                    label("Specs")
                }
                row()
                table {
                    debugAll()
                    pad(7f)
                    defaults().pad(7f)
                    container(EmptyMod())
                    container(EmptyMod())
                    container(EmptyMod())
                    container(EmptyMod())
                    row()
                    container(EmptyMod())
                    container(EmptyMod())
                    container(EmptyMod())
                    container(EmptyMod())
                }
            }
            row()
            table {
                name = "BotPart"
                padTop(14f)
                table {
                    background = bgDrawable // lifehack for margin
                    debugAll()
                    pad(7f)
                    defaults().pad(7f)
                    container(EmptyMod())
                    container(ModIcon(Mod(1, "DAMAGE", 7, 12), assets))
                    container(EmptyMod())
                    container(EmptyMod())
                    row()
                    container(EmptyMod())
                    container(EmptyMod())
                    container(EmptyMod())
                    container(EmptyMod())
                    row()
                    container(EmptyMod())
                    container(EmptyMod())
                    container(EmptyMod())
                    container(EmptyMod())
                    row()
                    container(EmptyMod())
                    container(EmptyMod())
                    container(EmptyMod())
                    container(EmptyMod())
                }
            }
        }
        stage += rootTable
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

    inner class ModIcon(val mod: Mod, val assets: Assets): Group() {
        private val iconSize = Constants.MOD_WIDTH / 2f
        private val bgColor = Color(1f, 1f, 0f, 1f)
        private val lvlColor = Color(0f, 0f, 0f, 1f)
        private val noLvlColor = Color(1f, 1f, 1f, 1f)

        init {
            touchable = Touchable.enabled
            setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT)

            val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            val bgDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(bgColor); fill()}))
            val lvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(lvlColor); fill()}))
            val noLvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(noLvlColor); fill()}))

            val bg = Image(bgDrawable).apply { setFillParent(true) }
            val texture: Texture = when(mod.index) {
                1 -> assets.manager.get(Constants.STAR)
                else -> assets.manager.get(Constants.BACKGROUND_IMAGE)
            }
            val icon = Image(texture).apply {
                setSize(iconSize, iconSize)
                setPosition((this@ModIcon.width - width) / 2f, (this@ModIcon.height - height) / 2f)
            }
            val quantityLabel = VisLabel("${mod.quantity}", "score-label").apply {
                setPosition(this@ModIcon.width - width, this@ModIcon.height - height)
                touchable = Touchable.disabled
            }
            val levelBars = table {
                bottom().left()
                pad(2f)
                defaults().pad(1f)
                for (i in 1..10) {
                    image(if (i <= mod.level) lvlDrawable else noLvlDrawable) {it.size(4f)}
                }
            }

            addActor(bg)
            addActor(icon)
            addActor(quantityLabel)
            addActor(levelBars)
        }
    }

    inner class EmptyMod: Group() {
        private val bgColor = Color(0f, 0f, 0f, 0.3f)

        init {
            setSize(64f, 64f)
            val pixel = Pixmap(1, 1, Pixmap.Format.Alpha)
            val bgDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(bgColor); fill()}))
            val img = Image(bgDrawable).apply { setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT) }
            addActor(img)
        }
    }
}