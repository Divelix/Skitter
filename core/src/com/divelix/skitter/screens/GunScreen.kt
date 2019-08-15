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
import com.divelix.skitter.utils.ModName
import ktx.actors.*
import ktx.app.KtxScreen
import ktx.assets.toInternalFile
import ktx.assets.toLocalFile
import ktx.vis.table

// Drag'n'drop version is in Google Drive
class GunScreen(val game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val skin = assets.uiSkin
    private val stage = Stage(FitViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

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

    val gunSpecs = Array<Float>(5)
    val finalGunSpecs = Array<Float>(5)

    val reader = JsonReader()
    val playerData = reader.parse("json/player_data.json".toInternalFile())
    val gunsData = reader.parse("json/guns.json".toInternalFile())
    val modsData = reader.parse("json/mods.json".toInternalFile())

    init {
        val playerGuns = playerData.get("guns")
        val guns = gunsData.get("guns")
        val mods = modsData.get("mods")

        // GUN
        val playerGun = playerGuns[playerData.get("active_gun").asInt()]
        val playerGunIndex = playerGun.get("index").asInt()
        val playerGunLevel = playerGun.get("level").asInt()
        val playerGunMods = playerGun.get("mods")
        var activeGun = guns[0] // first gun by default
        for (gun in guns) {
            if (gun.get("index").asInt() == playerGunIndex) {
                activeGun = gun
                break
            }
        }

        val specs = activeGun.get("specs")
        for (spec in specs) {
            gunSpecs.add(spec.asFloat())
        }
        println(" Damage: ${gunSpecs[0]}\n Reload: ${gunSpecs[1]}\n BulletSpeed: ${gunSpecs[2]}\n CritChance: ${gunSpecs[3]}\n CritMult: ${gunSpecs[4]}")

        // MODS
        val gunMods = mods.get("gun")
        val playerGunModIndices = Array<Int>(playerGunMods.size)
        for (i in 0 until playerGunMods.size) {
            playerGunModIndices.add(playerGunMods[i].get("index").asInt())
        }
        val activeGunMods = Array<JsonValue>(playerGunMods.size)
        for (i in 0 until playerGunMods.size) {
            for (mod in gunMods) {
                if (playerGunModIndices[i] == mod.get("index").asInt())
                    activeGunMods.add(mod)
            }
        }
        for (i in 0 until playerGunMods.size) {
            val name = ModName.valueOf(activeGunMods[i].get("name").asString())
            val level = playerGunMods[i].get("level").asInt()
            suitMods.add(Mod(name, level))
        }
        for (playerMod in playerData.get("mods").get("gun")) {
            val index = playerMod.get("index").asInt()
            val level = playerMod.get("level").asInt()
            val quantity = playerMod.get("quantity").asInt()

            for (mod in gunMods) {
                if (mod.get("index").asInt() == index) {
                    val name = ModName.valueOf(mod.get("name").asString())
                    val effects = mod.get("effects")
                    stockMods.add(Mod(name, level, quantity))
                    break
                }
            }
        }

        stockTable = table {
            name = "StockTable"
            defaults().width(Constants.MOD_WIDTH).height(Constants.MOD_HEIGHT).pad(2f)

            for (i in 1..stockMods.size) {
                container(ModImage(stockMods[i - 1])) {
                    touchable = Touchable.enabled
                }
                if (i % 4 == 0) row()
            }
//            for (i in 1..30) {
//                container(ModImage(stockMods[0])) {
//                    touchable = Touchable.enabled
//                }
//                if (i % 4 == 0) row()
//            }
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
                    label("DAMAGE: ${gunSpecs[0]}");row()
                    label("RELOAD: ${gunSpecs[1]}");row()
                    label("SPEED: ${gunSpecs[2]}");row()
                    label("CRIT: ${gunSpecs[3]}");row()
                    label("CHANCE: ${gunSpecs[4]}")
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
                                actor.children[1].remove()
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

    fun checkDupSuit(name: ModName): Boolean {
        for (container in suitTable.children) {
            val c = (container as Container<*>)
            if (c.actor != null)
                if ((c.actor as ModImage).mod.name == name) return true
        }
        return false
    }

    fun updateSpecs() {
        for (spec in gunSpecs)
            finalGunSpecs.add(spec)
        for (container in suitTable.children) {
            val c = (container as Container<*>)
            if (c.actor != null) {
                val mod = (c.actor as ModImage).mod
                when(mod.name) {
                    ModName.DAMAGE -> finalGunSpecs[0] *= 2f
                    ModName.RELOAD_SPEED -> finalGunSpecs[1] *= 1.1f
                    ModName.BULLET_SPEED -> finalGunSpecs[2] *= 1.1f
                    else -> println("${mod.name} is not implemented yet")
                }
            }
        }
        (specsTable.children[0] as Label).setText("DAMAGE: ${finalGunSpecs[0]}")
        (specsTable.children[1] as Label).setText("RELOAD: ${finalGunSpecs[1]}")
        (specsTable.children[2] as Label).setText("SPEED: ${finalGunSpecs[2]}")
        (specsTable.children[3] as Label).setText("CRIT: ${finalGunSpecs[3]}")
        (specsTable.children[4] as Label).setText("CHANCE: ${finalGunSpecs[4]}")
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
//        println(playerData.toJson(JsonWriter.OutputType.json))
        for (field in playerData) {
            when(field.name) {
                "active_gun" -> field.set(0, null) // TODO change when gun switch implemented
                "active_gun_specs" -> {
                    for (i in 0 until field.size)
                        field[i].set(finalGunSpecs[i].toDouble(), null)
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
            val texture = when(mod.name) {
                // Gun stockMods
                ModName.DAMAGE -> assets.manager.get<Texture>(Constants.MOD_DAMAGE)
                ModName.RELOAD_SPEED -> assets.manager.get<Texture>(Constants.MOD_RELOAD_SPEED)
                ModName.BULLET_SPEED -> assets.manager.get<Texture>(Constants.MOD_BULLET_SPEED)
                ModName.CRIT_CHANCE -> assets.manager.get<Texture>(Constants.LOADING_IMAGE) //TODO add texture
                ModName.CRIT_MULT -> assets.manager.get<Texture>(Constants.LOADING_IMAGE) //TODO add texture

                ModName.FIRE_DAMAGE -> assets.manager.get<Texture>(Constants.MOD_FIRE_DAMAGE)
                ModName.COLD_DAMAGE -> assets.manager.get<Texture>(Constants.MOD_COLD_DAMAGE)

                // Ship stockMods
                ModName.HEALTH -> assets.manager.get<Texture>(Constants.LOADING_IMAGE) //TODO add texture
                ModName.SPEED -> assets.manager.get<Texture>(Constants.LOADING_IMAGE) //TODO add texture
                ModName.MANA -> assets.manager.get<Texture>(Constants.LOADING_IMAGE) //TODO add texture
            }
            addActor(Image(texture).apply {
                touchable = Touchable.disabled
                setFillParent(true)
            })
        }
    }
}