package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.*
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divelix.skitter.*
import com.divelix.skitter.utils.Mod
import ktx.actors.*
import ktx.app.KtxScreen
import ktx.assets.toInternalFile
import ktx.assets.toLocalFile
import ktx.vis.table
import com.badlogic.gdx.utils.JsonValue
import com.kotcrab.vis.ui.widget.VisLabel

class GunScreen(val game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(FitViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    private val rootTable: Table
    lateinit var weaponImg: Image
    lateinit var infoTable: Table
    lateinit var specsTable: Table
    lateinit var suitTable: Table
    private val stockTable: Table
    private val applyBtn: Image
    private val suitMods = Array<Mod>(8)
    private val stockMods = Array<Mod>(20)

    val glowTexture: Texture = assets.manager.get(Constants.MOD_GLOW)

    var activeMod: ModImage? = null
    var sourceContainer: Container<*>? = null

    private val gunSpecs = Array<Float>(6)
    private val finalGunSpecs = Array(arrayOf(0f, 0f, 0f, 0f, 0f, 0f))
    private val modEffects = Array(arrayOf(1f, 1f, 1f, 1f, 1f, 1f))

    private val reader = JsonReader()
    private val playerData = reader.parse("json/player_data.json".toLocalFile())
    private val gunsData = reader.parse("json/guns.json".toInternalFile())
    private val modsData = reader.parse("json/mods.json".toInternalFile())
    // GUN MODS
    val _gunMods = modsData.get("mods").get("gun")

    init {
        // Underscore in variable names (_) means non-player data, i.e. from mods.json and guns.json

        // GUN
        val _guns = gunsData.get("guns")
        val pGun = playerData.get("guns")[playerData.get("active_gun").asInt()]
        fun findActiveGun(): JsonValue {
            for (gun in gunsData.get("guns")) {
                if (gun.get("index").asInt() == pGun.get("index").asInt())
                    return gun
            }
            return _guns[0] // first gun by default
        }
        val _pGun = findActiveGun()

        for (spec in _pGun.get("specs")) {
            gunSpecs.add(spec.asFloat())
        }

        // fill suitMods
        for (mod in pGun.get("mods")) {
            for (_mod in _gunMods) {
                if (mod.get("index").asInt() == _mod.get("index").asInt()) {
                    val index = mod.get("index").asInt()
                    val name = _mod.get("name").asString()
                    val level = mod.get("level").asInt()
                    suitMods.add(Mod(index, name, level))
                }
            }
        }

        // fill stockMods
        for (mod in playerData.get("mods").get("gun")) {
            val index = mod.get("index").asInt()
            val level = mod.get("level").asInt()
            val quantity = mod.get("quantity").asInt()
            //exclude repeats
            var isRepeat = false
            for (suitMod in suitMods) {
                if (suitMod.index == index && suitMod.level == level) {
                    isRepeat = true
                    break
                }
            }

            for (_mod in _gunMods) {
                if (_mod.get("index").asInt() == index) {
                    val name = _mod.get("name").asString()
                    val effects = _mod.get("effects")
                    if (isRepeat) {
                        if (quantity > 1)
                            stockMods.add(Mod(index, name, level, quantity-1))
                    } else {
                        stockMods.add(Mod(index, name, level, quantity))
                    }
                    break
                }
            }
        }

        stockTable = table {
            name = "StockTable"
            defaults().width(Constants.MOD_WIDTH).height(Constants.MOD_HEIGHT).pad(2f)

            for (i in 0 until stockMods.size + 8) {
                if (i < stockMods.size) {
                    container(ModImage(stockMods[i])) { touchable = Touchable.enabled }
                } else {
                    container<ModImage> { touchable = Touchable.enabled }
                }
                if ((i+1) % 4 == 0) row()
            }
        }
        rootTable = table {
            setFillParent(true)
            top()
            padTop(50f)
            defaults().left()//.expandX()

            infoTable = table {
                weaponImg = image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.RIFLE))) { cell ->
                    cell.size(128f, 64f)
                }
                table {
                    padLeft(10f)
                    defaults().expandX().left()
                    table {
                        defaults().left()
                        label("DAMAGE:"); row()
                        label("CAPACITY:"); row()
                        label("RELOAD:"); row()
                        label("SPEED:"); row()
                        label("CRIT:"); row()
                        label("CHANCE:")
                    }
                    specsTable = table {
                        padLeft(5f)
                        defaults().left()
                        label(gunSpecs[0].toString());row()
                        label(gunSpecs[1].toString());row()
                        label(gunSpecs[2].toString());row()
                        label(gunSpecs[3].toString());row()
                        label(gunSpecs[4].toString());row()
                        label(gunSpecs[5].toString())
                    }
                }
            }
            row()
            suitTable = table {
                name = "SuitTable"
                pad(25f, 0f, 25f, 0f)
                defaults().width(Constants.MOD_WIDTH).height(Constants.MOD_HEIGHT).pad(2f)

                for (i in 1..8) {
                    if (i <= suitMods.size) {
                        container(ModImage(suitMods[i - 1])) {
                            touchable = Touchable.enabled
                        }
                    } else {
                        container<ModImage> {
                            touchable = Touchable.enabled
                        }
                    }
                    if (i % 4 == 0) row()
                }
            }
            row()
            scrollPane(stockTable)
        }
        applyBtn = Image(assets.manager.get<Texture>(Constants.APPLY_BTN))
        applyBtn.setSize(64f, 64f)
        applyBtn.addListener(object: ClickListener() {
//            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
//                applyMods()
//                game.screen = PlayScreen(game)
//                return super.touchDown(event, x, y, pointer, button)
//            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                applyMods()
                game.screen = PlayScreen(game)
                super.touchUp(event, x, y, pointer, button)
            }
        })

        stage.addListener(object: ClickListener() {

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                super.touchDown(event, x, y, pointer, button)
                val actor = stage.hit(x, y, true) ?: return false
                when (actor) {
                    is ModImage -> {
                        if (activeMod == null) {
                            activeMod = actor
                            (activeMod as ModImage).addActor(Image(glowTexture).apply {
                                touchable = Touchable.disabled
                                setFillParent(true)
                            })
                            sourceContainer = actor.parent as Container<*>
                        } else {
                            if (actor == activeMod) {
                                actor.children[actor.children.size-1].remove() // removes glow texture
                                activeMod = null
                                sourceContainer = null
                            } else if (actor.mod.index == activeMod!!.mod.index && actor.mod.level == activeMod!!.mod.level) {
                                // SAME MODS -> need merge
                                if (actor.parent.parent.name == "StockTable") {
                                    actor.mod.quantity += activeMod!!.mod.quantity
                                    actor.quantityLabel.setText(actor.mod.quantity)
                                    activeMod!!.remove()
                                    activeMod = null
                                    sourceContainer = null
                                }
                            } else if (actor.mod.quantity != 1) {
                                println("DON'T TAP ON MULTI MODS") // TODO make visual (e.g. change color of quantity label)
                            } else {
                                val isInSameTable = actor.parent.parent.name == activeMod!!.parent.parent.name
                                val isSameTypes = actor.mod.name == activeMod!!.mod.name
                                val modOnCheck = if (actor.parent.parent.name == "StockTable") actor.mod.name else activeMod!!.mod.name
                                if (isInSameTable || isSameTypes || !checkDupSuit(modOnCheck)) {
                                    val cont = actor.parent as Container<*>
                                    activeMod!!.remove()
                                    sourceContainer!!.actor = actor
                                    cont.actor = activeMod
                                    sourceContainer = activeMod!!.parent as Container<*>
                                }
                            }
                        }
                    }
                    is Container<*> -> {
                        if (activeMod != null) {
                            val isInSameTable = actor.parent.name == activeMod!!.parent.parent.name
                            if (isInSameTable || !checkDupSuit(activeMod!!.mod.name) || activeMod!!.parent.parent.name == "SuitTable") {
                                activeMod!!.children[activeMod!!.children.size-1].remove()
                                if (activeMod!!.mod.quantity == 1) {
                                    actor.actor = activeMod
                                } else {
                                    val source = activeMod!!.mod
                                    actor.actor = ModImage(Mod(source.index, source.name, source.level))
                                    activeMod!!.mod.quantity--
                                    activeMod!!.quantityLabel.setText(activeMod!!.mod.quantity)
                                }
                                activeMod = null
                                sourceContainer = null
                            }
                        }
                    }
                }
                updateSpecs()
                return true
            }
        })

        stage += rootTable
        stage += applyBtn
        stage.isDebugAll = true
        updateSpecs()
    }

    fun checkDupSuit(name: String): Boolean {
        for (container in suitTable.children) {
            val c = (container as Container<*>)
            if (c.actor != null)
                if ((c.actor as ModImage).mod.name == name) return true
        }
        return false
    }

    fun updateSpecs() {
        for (i in 0 until gunSpecs.size) {
            finalGunSpecs[i] = gunSpecs[i]
            modEffects[i] = 1f
        }

        for (container in suitTable.children) {
            val c = (container as Container<*>)
            if (c.actor != null) {
                val mod = (c.actor as ModImage).mod
                for (_mod in _gunMods) {
                    if (_mod.get("index").asInt() == mod.index) {
                        when (_mod.get("effects").child.name) {
                            "damage" -> modEffects[0] = _mod.get("effects").child[mod.level-1].asFloat()
                            "capacity" -> modEffects[1] = _mod.get("effects").child[mod.level-1].asFloat()
                            "reload_time" -> modEffects[2] = _mod.get("effects").child[mod.level-1].asFloat()
                            "bullet_speed" -> modEffects[3] = _mod.get("effects").child[mod.level-1].asFloat()
                            "crit_chance" -> modEffects[4] = _mod.get("effects").child[mod.level-1].asFloat()
                            "crit_multiplier" -> modEffects[5] = _mod.get("effects").child[mod.level-1].asFloat()
                        }
                    }
                }
            }
        }
        for (i in 0 until finalGunSpecs.size) {
            finalGunSpecs[i] = gunSpecs[i] * modEffects[i]
        }
        for (i in 0 until specsTable.children.size)
            (specsTable.children[i] as Label).setText("${finalGunSpecs[i]}")

        manageQuantityVisibility()
    }

    private fun manageQuantityVisibility() {
        for (container in suitTable.children) {
            val modImage = if ((container as Container<*>).actor != null) container.actor as ModImage else continue
            modImage.quantityLabel.isVisible = false
        }

        for (container in stockTable.children) {
            val modImage = if ((container as Container<*>).actor != null) container.actor as ModImage else continue
            modImage.quantityLabel.isVisible = true
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.6f, 0.5f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun show() {
        println("GunScreen - show()")
        val handler = object: InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK -> game.screen = PlayScreen(game)
                    Input.Keys.P -> println(suitTable.children)
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(handler, stage)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun pause() {
        println("GunScreen - pause()")
        super.pause()
    }

    override fun resume() {
        println("GunScreen - resume()")
        super.resume()
    }

    override fun resize(width: Int, height: Int) {
        println("GunScreen - resize()")
//        stage.viewport.update(Constants.D_WIDTH, Constants.D_WIDTH * height/width, true)
        stage.viewport.update(width, height, true)
        applyBtn.setPosition(stage.camera.viewportWidth - applyBtn.width, 0f)
    }

    override fun hide() {
        println("GunScreen - hide()")
        super.hide()
    }

    override fun dispose() {
        println("GunScreen - dispose()")
        stage.dispose()
    }

    fun applyMods() {
        // update suitMods array
        suitMods.clear()
        for (child in suitTable.children) {
            if (child is Container<*> && child.actor != null) {
                val mod = (child.actor as ModImage).mod
                suitMods.add(mod)
                println("index: ${mod.index}, level: ${mod.level}")
            }
        }

        // update player_data.json
        for (field in playerData) {
            when(field.name) {
                "active_gun" -> field.set(0, null) // TODO change when gun switch implemented
                "active_gun_specs" -> {
                    for (i in 0 until field.size)
                        field[i].set(finalGunSpecs[i].toDouble(), null)
                }
                "guns" -> {
                    val activeGunMods = field[0].get("mods")

                    // clear mods JsonValue before writing
                    for (i in 0 until activeGunMods.size)
                        activeGunMods.remove(0)

                    for (mod in suitMods) {
                        val jsonMod = JsonValue(JsonValue.ValueType.`object`)
                        jsonMod.addChild("index", JsonValue(mod.index.toLong()))
                        jsonMod.addChild("level", JsonValue(mod.level.toLong()))
                        activeGunMods.addChild(jsonMod)
                    }
                }
            }
        }
        "json/player_data.json".toLocalFile().writeString(playerData.prettyPrint(JsonWriter.OutputType.json, 100), false)
        println("----------------gun mods applied-----------------")
    }

    inner class ModImage(val mod: Mod): Group() {
        val texture: Texture
        val levelLabel: VisLabel
        val quantityLabel: VisLabel

        init {
            touchable = Touchable.enabled
            setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT)
            texture = when(mod.index) {
                // Gun stockMods
                1 -> assets.manager.get(Constants.MOD_DAMAGE)
                2 -> assets.manager.get(Constants.MOD_RELOAD_SPEED)
                3 -> assets.manager.get(Constants.MOD_BULLET_SPEED)
                4 -> assets.manager.get(Constants.LOADING_IMAGE) //TODO add texture for CRIT_MULT

                5 -> assets.manager.get(Constants.MOD_FIRE_DAMAGE)
                6 -> assets.manager.get(Constants.MOD_COLD_DAMAGE)
                else -> assets.manager.get(Constants.LOADING_IMAGE)
            }
            levelLabel = VisLabel("lvl ${mod.level}", "mod-level").apply {
                touchable = Touchable.disabled
            }
            quantityLabel = VisLabel("${mod.quantity}", "mod-quantity").apply {
                setPosition(this@ModImage.width - width, this@ModImage.height - height)
                touchable = Touchable.disabled
            }
            addActor(Image(texture).apply {
                touchable = Touchable.disabled
                setFillParent(true)
            })
            addActor(levelLabel)
            addActor(quantityLabel)
            addActor(quantityLabel)
        }
    }
}