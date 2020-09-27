package com.divelix.skitter.ui.menu

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
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.JsonWriter
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.screens.MenuScreen
import com.divelix.skitter.utils.TopViewport
import ktx.app.KtxScreen
import ktx.assets.toInternalFile
import ktx.assets.toLocalFile
import ktx.log.info

abstract class EditScreen(val game: Main): KtxScreen {
    val context = game.getContext()
    val batch = context.inject<SpriteBatch>()
    val assets = context.inject<Assets>()
    val aspectRatio = Gdx.graphics.height.toFloat() /Gdx.graphics.width
    val stage = Stage(TopViewport(Constants.STAGE_WIDTH.toFloat(), Constants.STAGE_WIDTH * aspectRatio), batch)
    val reader = JsonReader()
    val playerFile = Constants.PLAYER_FILE.toLocalFile()
    val playerData: JsonValue = reader.parse(playerFile)
    val modsData = reader.parse(Constants.MODS_FILE.toInternalFile())

    val tabbedBar = TabbedBar(assets)
    val applyBtn = ApplyBtn(0f,  0f)
    val carriage = Image(assets.manager.get<Texture>(Constants.CARRIAGE)).apply { touchable = Touchable.disabled }
    val carriageBorderWidth = 7f
    var activeMod: ModIcon? = null
    var activeModContainer: Container<*>? = null

    init {
        val handler = object: InputAdapter() {
            override fun keyUp(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK -> {
                        updatePlayerJson()
                        game.screen = MenuScreen(game)
                    }
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(handler, stage)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(assets.bgColor.r, assets.bgColor.g, assets.bgColor.b, assets.bgColor.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
//        applyBtn.setPosition(0f, Constants.D_HEIGHT - height.toFloat())
    }

    fun makeStageListener(): InputListener {
        return object: ClickListener() {
            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                super.touchUp(event, x, y, pointer, button)
                val actor = stage.hit(x, y, true) ?: return
                when (actor) {
                    is ModIcon -> processModIcon(actor)
                    is EmptyModIcon -> processEmptyMod(actor)
                    is TabbedBar.Tab -> {
                        deselect()
                        tabbedBar.makeActive(actor)
                    }
                }
            }
        }
    }

    abstract fun processModIcon(modIcon: ModIcon)
    abstract fun processEmptyMod(emptyMod: EmptyModIcon)
    abstract fun updateUI()

    open fun updatePlayerJson() {
        playerFile.writeString(playerData.prettyPrint(JsonWriter.OutputType.json, 100), false)
        info { "player_data.json was updated" }
    }

    open fun deselect() {
        activeMod = null
        activeModContainer = null
        carriage.setPosition(-carriage.width, -carriage.height)
    }

    inner class ApplyBtn(x: Float, y: Float): Group() {
        val bgColor = Color(0f, 0f, 0f, 1f)
        val btnSize = Vector2(60f, 60f)
        val iconWidth = 50f

        init {
            setPosition(x, y)
            setSize(btnSize.x, btnSize.y)
            // Background
            val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
            val bgDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(bgColor); fill() }))
            val bg = Image(bgDrawable).apply { setFillParent(true) }
            // Icon
            val texture = assets.manager.get<Texture>(Constants.APPLY_ICON)
            val aspectRatio = texture.width.toFloat() / texture.height.toFloat()
            val icon = Image(texture).apply {
                setSize(iconWidth, iconWidth / aspectRatio)
                setPosition(btnSize.x / 2f - width / 2f, btnSize.y / 2f - height / 2f)
            }
            addActor(bg)
            addActor(icon)
            addListener(object: ClickListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    updatePlayerJson()
                    game.screen = MenuScreen(game)
                    return super.touchDown(event, x, y, pointer, button)
                }
            })
        }
    }
}