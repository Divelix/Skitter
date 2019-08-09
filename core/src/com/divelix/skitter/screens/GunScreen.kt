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
    private val playerJson = "json/playerData.json".toInternalFile()
    private val gunsJson = "json/guns.json".toInternalFile()
    private val modsJson = "json/mods.json".toInternalFile()

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
    var reloadSpeed: Float
    var bulletSpeed: Float
    var critChance: Float
    var critMult: Float

    init {
        val reader = JsonReader()
        val playerData = reader.parse(playerJson)
        val gunsData = reader.parse(gunsJson)
        val modsData = reader.parse(modsJson)
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

        val gunSpecs = activeGun.get("specs")
        damage = gunSpecs[0].asFloat()
        reloadSpeed = gunSpecs[1].asFloat()
        bulletSpeed = gunSpecs[2].asFloat()
        critChance = gunSpecs[3].asFloat()
        critMult = gunSpecs[4].asFloat()
        println(" Damage: $damage\n Reload: $reloadSpeed\n BulletSpeed: $bulletSpeed\n CritChance: $critChance\n CritMult: $critMult")

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
                    label("DAMAGE: $damage");row()
                    label("RELOAD: $reloadSpeed");row()
                    label("SPEED: $bulletSpeed");row()
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
        var finalDamage = damage
        var finalReloadSpeed = reloadSpeed
        var finalBulletSpeed = bulletSpeed
        var finalCritMult = critMult
        var finalCritChance = critChance
        for (container in suitTable.children) {
            val c = (container as Container<*>)
            if (c.actor != null) {
                val mod = (c.actor as ModImage).mod
                when(mod.name) {
                    ModName.DAMAGE -> finalDamage *= 2f
                    ModName.RELOAD_SPEED -> finalReloadSpeed *= 1.1f
                    ModName.BULLET_SPEED -> finalBulletSpeed *= 1.1f
                    else -> println("${mod.name} is not implemented yet")
                }
            }
        }
        (specsTable.children[0] as Label).setText("DAMAGE: $finalDamage")
        (specsTable.children[1] as Label).setText("RELOAD: $finalReloadSpeed")
        (specsTable.children[2] as Label).setText("SPEED: $finalBulletSpeed")
        (specsTable.children[3] as Label).setText("CRIT: $finalCritMult")
        (specsTable.children[4] as Label).setText("CHANCE: $finalCritChance")
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
                        mods.add(Mod(ModName.DAMAGE, 1, 1))
                        val data = PlayerData("Qwerty", 5, 2000, 125, mods)
//                        println(json.prettyPrint(data))
                        playerJson.writeString(json.prettyPrint(data), false)
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

    fun applyMods() {
        println("gun mods applied")
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