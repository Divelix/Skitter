package com.divelix.skitter.screens

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
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.EmptyMod
import com.divelix.skitter.ui.EquipTable
import com.divelix.skitter.ui.Mod
import com.divelix.skitter.ui.ModIcon
import com.divelix.skitter.utils.TopViewport
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.vis.table

class EquipScreen(val game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    val ships: Table
    val guns: Table
    lateinit var shipTab: Container<NavButton>
    lateinit var gunTab: Container<NavButton>
    lateinit var content: Container<Table>

    val carriage = Image(assets.manager.get<Texture>(Constants.CARRIAGE)).apply { touchable = Touchable.disabled }
    val carriageBorderWidth = 7f
    var activeMod: ModIcon? = null
    var activeModContainer: Container<*>? = null

    init {
        ships = EquipTable(Constants.SHIPS_TAB, assets)
        guns = EquipTable(Constants.GUNS_TAB, assets)
        stage += table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                shipTab = container(NavButton(Constants.SHIPS_TAB, true))
                gunTab = container(NavButton(Constants.GUNS_TAB))
            }
            row()
            content = container(ships)
        }
        stage += carriage.apply { setPosition(-height, -width) }
        stage.addListener(makeStageListener())
        val handler = object: InputAdapter() {
            override fun keyUp(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK -> game.screen = MenuScreen(game)
                    Input.Keys.ENTER -> println(activeMod)
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(handler, stage)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(Constants.BG_COLOR.r, Constants.BG_COLOR.g, Constants.BG_COLOR.b, Constants.BG_COLOR.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

    private fun makeStageListener(): InputListener {
        return object: ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                super.touchDown(event, x, y, pointer, button)
                val actor = stage.hit(x, y, true) ?: return false
                when (actor) {
                    is ModIcon -> processModIcon(actor)
                    is EmptyMod -> processEmptyMod(actor)
                }
//                updateSpecs()
                return true
            }
        }
    }

    private fun processModIcon(modIcon: ModIcon) {
        val container = (modIcon.parent as Container<*>)
        val offset = Vector2(-carriageBorderWidth, -carriageBorderWidth)
        val carriagePos = container.localToStageCoordinates(offset)
        when (activeMod) {
            null -> {// select this mod
                activeMod = modIcon
                activeModContainer = container
                carriage.setPosition(carriagePos.x, carriagePos.y)
            }
            modIcon -> {// deselect this mod
                activeMod = null
                activeModContainer = null
                carriage.setPosition(-carriage.width, -carriage.height)
            }
            else -> {
                val isSameIndex = modIcon.mod.index == activeMod!!.mod.index
                val isSameTable = modIcon.parent.parent.name == activeMod!!.parent.parent.name
                val isActiveInSuit =  activeMod!!.parent.parent.name == "SuitTable"

                if(isSameTable || isSameIndex || isActiveInSuit && !isDup(modIcon) || !isActiveInSuit && !isDup(activeMod!!)) {
                    // switch mods
                    container.actor = activeMod
                    activeModContainer!!.actor = modIcon
                    activeModContainer = container
                    carriage.setPosition(carriagePos.x, carriagePos.y)
                } else {
                    println("Duplicates are not allowed")
                }
            }
        }
    }

    private fun processEmptyMod(emptyMod: EmptyMod) {
        if (activeMod == null) return
        val container = (emptyMod.parent as Container<*>)

        val isSameTable = emptyMod.parent.parent.name == activeMod!!.parent.parent.name
        val isActiveInSuit =  activeMod!!.parent.parent.name == "SuitTable"
        val isEmptyInStock =  emptyMod.parent.parent.name == "StockTable"

        if (isEmptyInStock || isSameTable || !isActiveInSuit && !isDup(activeMod!!)) {
            carriage.setPosition(-carriage.width, -carriage.height)
            container.actor = activeMod
            activeModContainer!!.actor = emptyMod
            activeMod = null
            activeModContainer = null
        } else {
            println("duplicates are forbidden")
        }
    }

    fun isDup(modIcon: ModIcon): Boolean {
        val suitTable = (content.actor as EquipTable).suitTable
        suitTable.children.filter {(it as Container<*>).actor is ModIcon}.forEach {
            val suitModIcon = (it as Container<*>).actor as ModIcon
            if (suitModIcon.mod.index == modIcon.mod.index) return true
        }
        return false
    }

    inner class NavButton(val tabName: String, active: Boolean = false): Group() {
        val upColor = Color(0f, 0f, 0f, 0.2f)
        val downColor = Color(0f, 0f, 0f, 0f)
        val bg: Image
        val texture: Texture
        val icon: Image
        val btnSize = Vector2(Constants.D_WIDTH / 2f, 66f)
        val iconSize = 50f

        init {
            setSize(btnSize.x, btnSize.y)
            // Background
            val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
            val upDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(upColor); fill() }))
            val downDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(downColor); fill() }))
            val activeDrawable = if (active) downDrawable else upDrawable
            bg = Image(activeDrawable).apply { setFillParent(true) }
            // Icon
            texture = when(tabName) {
                Constants.SHIPS_TAB -> assets.manager.get<Texture>(Constants.SHIP_ICON)
                Constants.GUNS_TAB -> assets.manager.get<Texture>(Constants.GUN_ICON)
                else -> error {"No texture for tab"}
            }
            icon = Image(texture).apply {
                setSize(iconSize, iconSize)
                setPosition(btnSize.x / 2f - width / 2f, btnSize.y / 2f - height / 2f)
            }
            addActor(bg)
            addActor(icon)
            addListener(object: ClickListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    if (tabName == content.actor.name) return super.touchDown(event, x, y, pointer, button)
                    when (tabName) {
                        Constants.SHIPS_TAB -> {
                            shipTab.actor.bg.drawable = downDrawable
                            gunTab.actor.bg.drawable = upDrawable
                            content.actor = ships
                        }
                        Constants.GUNS_TAB -> {
                            gunTab.actor.bg.drawable = downDrawable
                            shipTab.actor.bg.drawable = upDrawable
                            content.actor = guns
                        }
                    }
                    return super.touchDown(event, x, y, pointer, button)
                }
            })
        }


    }
}