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


// Drag'n'drop version is in Google Drive
class GunScreen(val game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val skin = assets.uiSkin
    private val stage = Stage(FitViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    private val rootTable: Table
    lateinit var weaponImg: Image
    lateinit var infoTable: Table
    lateinit var specsTable: Table
    lateinit var suitTable: Table
    lateinit var stockTable: Table
    private val applyBtn: Image
    private val suitMods = Array<Mod>(8)
    private val stockMods = Array<Mod>(20)

    val glow_tex = assets.manager.get<Texture>(Constants.MOD_GLOW)

    var activeMod: ModImage? = null
    var sourceContainer: Container<*>? = null

    val gunSpecs = Array<Float>(5)
    val finalGunSpecs = Array(arrayOf(0f, 0f, 0f, 0f, 0f))

    val reader = JsonReader()
    val playerData = reader.parse("json/player_data.json".toInternalFile())
    val gunsData = reader.parse("json/guns.json".toInternalFile())
    val modsData = reader.parse("json/mods.json".toInternalFile())

    init {
        // Underscore in variable names (_) means non-player data, i.e. from mods.json and guns.json
        val pGuns = playerData.get("guns")
        val _guns = gunsData.get("guns")
        val _mods = modsData.get("mods")

        // GUN
        val pGun = pGuns[playerData.get("active_gun").asInt()]
        val pGunIndex = pGun.get("index").asInt()
        val pGunLevel = pGun.get("level").asInt()
        val pGunMods = pGun.get("mods")
        var _activeGun = _guns[0] // first gun by default
        for (_gun in _guns) {
            if (_gun.get("index").asInt() == pGunIndex) {
                _activeGun = _gun
                break
            }
        }

        val specs = _activeGun.get("specs")
        for (spec in specs) {
            gunSpecs.add(spec.asFloat())
        }

        // GUN MODS
        val _gunMods = _mods.get("gun")
        val pGunModIndices = Array<Int>(pGunMods.size)
        for (i in 0 until pGunMods.size) {
            pGunModIndices.add(pGunMods[i].get("index").asInt())
        }
        val _aGunMods = Array<JsonValue>(pGunMods.size)
        for (i in 0 until pGunMods.size) {
            for (_mod in _gunMods) {
                if (pGunModIndices[i] == _mod.get("index").asInt())
                    _aGunMods.add(_mod)
            }
        }

        // fill suit array with Mods
        for (i in 0 until pGunMods.size) {
            val index = pGunMods[i].get("index").asInt()
            val name = _aGunMods[i].get("name").asString()
            val level = pGunMods[i].get("level").asInt()
            suitMods.add(Mod(index, name, level))
        }

        // fill stock array with Mods
        for (pMod in playerData.get("mods").get("gun")) {
            val index = pMod.get("index").asInt()
            val level = pMod.get("level").asInt()
            val quantity = pMod.get("quantity").asInt()

            for (_mod in _gunMods) {
                if (_mod.get("index").asInt() == index) {
                    val name = _mod.get("name").asString()
                    val effects = _mod.get("effects")
                    var isRepeat = false
                    for (sm in suitMods) {
                        if (sm.index == index && sm.level == level) {
                            isRepeat = true
                            break
                        }
                    }
                    if (!isRepeat) stockMods.add(Mod(index, name, level, quantity))
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
                        label(gunSpecs[4].toString())
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
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                applyMods()
                game.screen = PlayScreen(game)
                return super.touchDown(event, x, y, pointer, button)
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
                            (activeMod as ModImage).addActor(Image(glow_tex).apply {
                                touchable = Touchable.disabled
                                setFillParent(true)
                            })
                            sourceContainer = actor.parent as Container<*>
                        } else {
                            if (actor == activeMod) {
                                actor.children[actor.children.size-1].remove() // removes glow texture
                                activeMod = null
                                sourceContainer = null
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
                                actor.actor = activeMod
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
        for (i in 0 until gunSpecs.size)
            finalGunSpecs[i] = gunSpecs[i]
        for (container in suitTable.children) {
            val c = (container as Container<*>)
            if (c.actor != null) {
                val mod = (c.actor as ModImage).mod
                when(mod.index) {
                    1 -> finalGunSpecs[0] = gunSpecs[0] * 2f
                    2 -> finalGunSpecs[1] = gunSpecs[1] * 2f
                    3 -> finalGunSpecs[2] = gunSpecs[2] * 2f
                    else -> println("${mod.name} is not implemented yet")
                }
            }
        }
        for (i in 0 until specsTable.children.size)
            (specsTable.children[i] as Label).setText("${finalGunSpecs[i]}")

        manageQuantityVisibility()
    }

    fun manageQuantityVisibility() {
        for (container in suitTable.children) {
            val modImage = if ((container as Container<*>).actor != null) container.actor as ModImage else continue
            for (child in modImage.children)
                if (child.name == "QuantityLabel")
                    child.isVisible = false
        }

        for (container in stockTable.children) {
            val modImage = if ((container as Container<*>).actor != null) container.actor as ModImage else continue
            for (child in modImage.children)
                if (child.name == "QuantityLabel")
                    child.isVisible = true
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.6f, 0.5f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun show() {
        val handler = object: InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK -> {
                        game.screen = PlayScreen(game)
                    }
                    Input.Keys.P -> println(suitTable.children)
                    Input.Keys.A -> {
                        for (child in suitTable.children) {
                            if (child is Container<*> && child.actor != null) {
                                val mod = (child.actor as ModImage).mod
                                println("index: ${mod.index}, level: ${mod.level}")
                            }
                        }
                    }
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(handler, stage)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun resize(width: Int, height: Int) {
//        stage.viewport.update(Constants.D_WIDTH, Constants.D_WIDTH * height/width, true)
        stage.viewport.update(width, height, true)
        applyBtn.setPosition(stage.camera.viewportWidth - applyBtn.width, 0f)
    }

    // hide?
    override fun dispose() {
        stage.dispose()
    }

    fun applyMods() {
        // update mod arrays
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
        init {
            touchable = Touchable.enabled
            setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT)
            val texture = when(mod.index) {
                // Gun stockMods
                1 -> assets.manager.get<Texture>(Constants.MOD_DAMAGE)
                2 -> assets.manager.get<Texture>(Constants.MOD_RELOAD_SPEED)
                3 -> assets.manager.get<Texture>(Constants.MOD_BULLET_SPEED)
                4 -> assets.manager.get<Texture>(Constants.LOADING_IMAGE) //TODO add texture for CRIT_MULT

                5 -> assets.manager.get<Texture>(Constants.MOD_FIRE_DAMAGE)
                6 -> assets.manager.get<Texture>(Constants.MOD_COLD_DAMAGE)
                else -> assets.manager.get<Texture>(Constants.LOADING_IMAGE)
            }
            addActor(Image(texture).apply {
                touchable = Touchable.disabled
                setFillParent(true)
            })
            addActor(VisLabel("lvl ${mod.level}", "mod-level").apply {
                touchable = Touchable.disabled
            })
            addActor(VisLabel("${mod.quantity}", "mod-quantity").apply {
                name = "QuantityLabel"
                setPosition(this@ModImage.width - width, this@ModImage.height - height)
                touchable = Touchable.disabled
            })
        }
    }
}