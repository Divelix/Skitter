package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.Mod
import com.divelix.skitter.ui.ModImage
import com.kotcrab.vis.ui.widget.VisTable
import ktx.actors.onChange
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.assets.toInternalFile
import ktx.assets.toLocalFile
import ktx.vis.table
import ktx.vis.window

class ModScreen(game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(FitViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    private val stockTable: VisTable

    private var coins: Int
    private val stockMods = Array<Mod>(20)
    var activeMod: ModImage? = null
    var bigContainer: Container<*>? = null

    val rootTable: VisTable

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

        coins = playerData.get("coins").asInt()

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

        rootTable = table {
            top()
            setFillParent(true)
            label("$coins", "mod-quantity").cell(align = Align.right, colspan = 3, padBottom = 50f)
            row()
            image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.SELL_BTN))).name = "sellBtn"
            bigContainer = container<Image> {
                size(150f)
                touchable = Touchable.disabled
            }
            image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.UP_BTN))).name = "upBtn"
            row()
            scrollPane(stockTable).cell(colspan = 3)
        }
        stage += rootTable
//        stage.isDebugAll = true

        stage.addListener(object: ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                super.touchDown(event, x, y, pointer, button)
                val actor = stage.hit(x, y, true) ?: return false
                when (actor) {
                    is Image -> when(actor.name) {
//                        "sellBtn" -> stage += sellWindow.fadeIn(1f)
                        "sellBtn" -> makeSellWindow()
                        "upBtn" -> upgradeActiveMod()
                    }
                    is ModImage -> makeModActive(actor)
                }
                return true
            }
        })

        makeModActive((stockTable.children[0] as Container<*>).actor as ModImage)
    }

    fun makeSellWindow() {
        rootTable.touchable = Touchable.disabled
        stage += window("Sell mode?") {
            debugAll()
            centerWindow()
            defaults().expand()
            padTop(25f) // title height
            width = 200f
            height = 100f
            val quantitySlider = slider(1f, activeMod!!.mod.quantity.toFloat()).cell(width = 200f, colspan = 2)
            row()
            val quantityLabel = label("1").cell(colspan = 2)
            quantitySlider.onChange {
                quantityLabel.setText(quantitySlider.value.toInt().toString())
            }
            row()
            textButton("Sell").cell(align = Align.left)
            textButton("Cancel").cell(align = Align.right).addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    super.touchUp(event, x, y, pointer, button)
                    rootTable.touchable = Touchable.enabled
                    this@window.fadeOut(1f)
                }
            })
        }.fadeIn(1f)
    }

    fun sellActiveMod() {
        val level = activeMod!!.mod.level
        val price = modsData.get("prices").asIntArray()



        val cost = price[level - 1]
        println("SELL for $cost")
    }

    fun upgradeActiveMod() {
        val level = activeMod!!.mod.level
        val price = modsData.get("prices").asIntArray()

        val cost = price[level] - price[0]
        println("UPGRADE for $cost")
    }

    fun makeModActive(modImage: ModImage) {
        if (activeMod != null) {
            val a = activeMod as ModImage
            a.children[a.children.size-1].remove()
        }
        activeMod = modImage
        (activeMod as ModImage).addActor(Image(assets.manager.get<Texture>(Constants.MOD_GLOW)).apply {
            touchable = Touchable.disabled
            setFillParent(true)
        })
        bigContainer?.actor = Image(modImage.texture).apply { setFillParent(true) }
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