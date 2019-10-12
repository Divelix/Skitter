package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.Mod
import com.divelix.skitter.ui.ModImage
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.assets.toInternalFile
import ktx.assets.toLocalFile
import ktx.vis.table
import java.nio.file.Files.size

class ModScreen(game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(FitViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    private val stockTable: Table

    private val stockMods = Array<Mod>(20)
    var activeMod: ModImage? = null
    var bigContainer: Container<*>? = null
//    var sourceContainer: Container<*>? = null

    private val reader = JsonReader()
    private val playerData = reader.parse("json/player_data.json".toLocalFile())
    private val gunsData = reader.parse("json/guns.json".toInternalFile())
    private val modsData = reader.parse("json/mods.json".toInternalFile())
    // GUN MODS
    val _gunMods = modsData.get("mods").get("gun")

    init {
        // fill stockMods
        for (mod in playerData.get("mods").get("gun")) {
            val index = mod.get("index").asInt()
            val level = mod.get("level").asInt()
            val quantity = mod.get("quantity").asInt()

            for (_mod in _gunMods) {
                if (_mod.get("index").asInt() == index) {
                    val name = _mod.get("name").asString()
                    val effects = _mod.get("effects")
                    stockMods.add(Mod(index, name, level, quantity))
                    break
                }
            }
        }

        stockTable = table {
            name = "StockTable"
            defaults().width(Constants.MOD_WIDTH).height(Constants.MOD_HEIGHT).pad(2f)

            for (i in 0 until stockMods.size + 8) {
                if (i < stockMods.size) {
                    container(ModImage(stockMods[i], assets)) { touchable = Touchable.enabled }
                } else {
                    container<ModImage> { touchable = Touchable.enabled }
                }
                if ((i + 1) % 4 == 0) row()
            }
        }

        stage += table {
            setFillParent(true)
            image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.SELL_BTN)))
            bigContainer = container<Image> {
                size(150f)
                touchable = Touchable.disabled
            }
            image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.UP_BTN)))
            row()
            scrollPane(stockTable).cell(colspan = 3)
        }
        stage.isDebugAll = true

        stage.addListener(object: ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                super.touchDown(event, x, y, pointer, button)
                val actor = stage.hit(x, y, true) ?: return false
                when (actor) {
                    is ModImage -> {
                        if (activeMod != null) {
                            val a = activeMod as ModImage
                            a.children[a.children.size-1].remove()
                        }
                        activeMod = actor
                        (activeMod as ModImage).addActor(Image(assets.manager.get<Texture>(Constants.MOD_GLOW)).apply {
                            touchable = Touchable.disabled
                            setFillParent(true)
                        })
                        bigContainer?.actor = Image(actor.texture).apply { setFillParent(true) }
                        println("CLICKED MOD")
                    }
                    else -> println(actor.name) // TODO aaaaaaa, wtf?
                }
                return true
            }
        })
    }

    override fun show() {
        println("ModScreen - show()")
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.6f, 0.5f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun pause() {
        println("ModScreen - pause()")
        super.pause()
    }

    override fun resume() {
        println("ModScreen - resume()")
        super.resume()
    }

    override fun resize(width: Int, height: Int) {
        println("ModScreen - resize()")
        stage.viewport.update(width, height, true)
    }

    override fun hide() {
        println("ModScreen - hide()")
        super.hide()
    }

    override fun dispose() {
        println("GunScreen - dispose()")
        stage.dispose()
    }
}