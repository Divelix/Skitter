package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonWriter
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divelix.skitter.*
import ktx.actors.*
import ktx.app.KtxScreen
import ktx.assets.toInternalFile
import ktx.vis.table

// Drag'n'drop version is in Google Drive
class GunScreen(val game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val skin = assets.uiSkin
    private val stage = Stage(FitViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)
    private val dataFile = "json/playerData.json".toInternalFile()

    private val posLabel = Label("0; 0", skin)
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

    var damage: Float
    var speed: Float
    var critMult: Float
    var critChance: Float

    var finalDamage = 0f
    var finalSpeed = 0f
    var finalCritMult = 0f
    var finalCritChance = 0f

    init {
        val reader = JsonReader()
        val data = reader.parse(dataFile)
        val guns = data.get("guns")
        damage = guns[0].get("damage").asFloat()
        speed = guns[0].get("speed").asFloat()
        critMult = guns[0].get("critical_multiplier").asFloat()
        critChance = guns[0].get("critical_chance").asFloat()
        println(" Damage: $damage\n Speed: $speed\n Crit multiplier: $critMult\n Crit chance: $critChance")
        for (mod in guns[0].get("mods")) {
            val type = ModType.valueOf(mod.get("type").asString())
            val level = mod.get("level").asInt()
            val quantity = mod.get("quantity").asInt()
            suitMods.add(Mod(type, level, quantity))
        }
        for (mod in data.get("mods")) {
            if (mod.get("type").asString() == "gun") {
                val name = ModType.valueOf(mod.get("name").asString()) // not toString()!!!
                val level = mod.get("level").asInt()
                val quantity = mod.get("quantity").asInt()
                stockMods.add(Mod(name, level, quantity))
            }
        }

        stockTable = table {
            name = "StockTable"
            defaults().width(Constants.MOD_WIDTH).height(Constants.MOD_HEIGHT).pad(2f)

//                for (i in 1..stockMods.size) {
//                    container(ModImage(stockMods[i - 1])) {
//                        touchable = Touchable.enabled
//                    }
//                    if (i % 4 == 0) row()
//                }
            for (i in 1..30) {
                container(ModImage(stockMods[0])) {
                    touchable = Touchable.enabled
                }
                if (i % 4 == 0) row()
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
                specsTable = table {
                    padLeft(10f)
                    defaults().expandX().left()
                    label("DAMAGE: $damage");row()
                    label("SPEED: $speed");row()
                    label("CRIT: $critMult");row()
                    label("CHANCE: $critChance")
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
                game.screen = PlayScreen(game)
                return super.touchDown(event, x, y, pointer, button)
            }
        })

        stage.addListener(object: DragListener() {

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
                                actor.children[1].remove()
                                activeMod = null
                                sourceContainer = null
                            } else {
                                val isInSameTable = actor.parent.parent.name == activeMod!!.parent.parent.name
                                val isSameTypes = actor.mod.type == activeMod!!.mod.type
                                val modOnCheck = if (actor.parent.parent.name == "StockTable") actor.mod.type else activeMod!!.mod.type
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
                            val isInSameTable = actor.parent.parent.name == activeMod!!.parent.parent.name
                            if (isInSameTable || !checkDupSuit(activeMod!!.mod.type) || activeMod!!.parent.parent.name == "SuitTable") {
                                activeMod!!.children[1].remove()
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
//        stage + posLabel
        stage.isDebugAll = true
        updateSpecs()
    }

    fun checkDupSuit(type: ModType): Boolean {
        for (container in suitTable.children) {
            val c = (container as Container<*>)
            if (c.actor != null)
                if ((c.actor as ModImage).mod.type == type) return true
        }
        return false
    }

    fun updateSpecs() {
        finalDamage = damage
        finalSpeed = speed
        finalCritMult = critMult
        finalCritChance = critChance
        for (container in suitTable.children) {
            val c = (container as Container<*>)
            if (c.actor != null) {
                val mod = (c.actor as ModImage).mod
                when(mod.type) {
                    ModType.DAMAGE -> finalDamage *= 2f
                    ModType.ATTACK_SPEED -> finalSpeed *= 1.1f
                    else -> println("${mod.type} is not implemented yet")
                }
            }
        }
        (specsTable.children[0] as Label).setText("DAMAGE: $finalDamage")
        (specsTable.children[1] as Label).setText("SPEED: $finalSpeed")
        (specsTable.children[2] as Label).setText("CRIT: $finalCritMult")
        (specsTable.children[3] as Label).setText("CHANCE: $finalCritChance")
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
                    Input.Keys.W -> {
                        val json = Json(JsonWriter.OutputType.json)
                        val mods = Array<Mod>()
                        mods.add(Mod(ModType.DAMAGE, 1, 1))
                        val data = PlayerData("Qwerty", 5, 2000, 125, mods)
//                        println(json.prettyPrint(data))
                        dataFile.writeString(json.prettyPrint(data), false)
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
        println("stage size: (${stage.width}; ${stage.height})")
        println("stage.viewport screen size: (${stage.viewport.screenWidth}; ${stage.viewport.screenHeight})")
        println("stage.viewport world size: (${stage.viewport.worldWidth}; ${stage.viewport.worldHeight})")
        println("stage.camera size: (${stage.camera.viewportWidth}; ${stage.camera.viewportHeight})")
    }

    // hide?
    override fun dispose() {
        stage.dispose()
    }

    inner class ModImage(val mod: Mod): Group() {
        init {
            touchable = Touchable.enabled
            setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT)
            val texture = when(mod.type) {
                ModType.EMPTY -> assets.manager.get<Texture>(Constants.LOADING_IMAGE)

                // Gun stockMods
                ModType.DAMAGE -> assets.manager.get<Texture>(Constants.MOD_DAMAGE)
                ModType.FIRE_DAMAGE -> assets.manager.get<Texture>(Constants.MOD_FIRE_DAMAGE)
                ModType.COLD_DAMAGE -> assets.manager.get<Texture>(Constants.MOD_COLD_DAMAGE)
                ModType.ATTACK_SPEED -> assets.manager.get<Texture>(Constants.MOD_ATTACK_SPEED)

                // Ship stockMods
                ModType.HEALTH -> TODO()
                ModType.SPEED -> TODO()
                ModType.MANA -> TODO()
            }
            addActor(Image(texture).apply {
                touchable = Touchable.disabled
                setFillParent(true)
            })
        }
    }
}