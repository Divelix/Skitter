package com.divelix.skitter.screens.experimental

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.Main
import com.divelix.skitter.container
import com.divelix.skitter.data.*
import com.divelix.skitter.image
import com.divelix.skitter.utils.TopViewport
import ktx.actors.onClickEvent
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.assets.toLocalFile
import ktx.collections.*
import ktx.inject.Context
import ktx.json.fromJson
import ktx.scene2d.*
import ktx.style.get

class SwipeMenuScreen(game: Main) : KtxScreen {
    val context = game.getContext()
    val batch = context.inject<SpriteBatch>()
    val assets = context.inject<Assets>()
    private val aspectRatio = Gdx.graphics.height.toFloat() / Gdx.graphics.width
    private val stage = Stage(TopViewport(Constants.D_WIDTH.toFloat(), Constants.D_WIDTH * aspectRatio), batch)

    val swipeMenu: SwipeMenu

    init {
        swipeMenu = SwipeMenu(context)
        stage += swipeMenu
        stage.isDebugAll = true
        val handler = object : InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.NUM_1 -> swipeMenu.scrollPane.scrollX = 0f
                    Input.Keys.NUM_2 -> swipeMenu.scrollPane.scrollX = 350f
                    Input.Keys.NUM_3 -> swipeMenu.scrollPane.scrollX = 700f
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(stage, handler)
        Gdx.input.inputProcessor = multiplexer
    }

    // It make new instance of PlayerData and write it to json file
    fun fillJsonFile() {
        val file = "json/playerData.json".toLocalFile()
        val printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }

        val playerData = PlayerData(
                id = 1234,
                name = "Lex",
                coins = 525,
                activeShip = 1,
                activeShipSpecs = gdxArrayOf(100f, 20f),
                activeGun = 2,
                activeGunSpecs = gdxArrayOf(30f, 10f, 1f, 25f, 2f, 0.1f),
                ships = gdxArrayOf(EquipData(
                        index = 1,
                        level = 1,
                        mods = gdxArrayOf(
                                ModAvatarData(index = 1001, level = 1)
                        )
                )),
                guns = gdxArrayOf(EquipData(
                        index = 1,
                        level = 1,
                        mods = gdxArrayOf()
                )),
                mods = PlayerModsData(
                        ship = gdxArrayOf(
                                ModAvatarData(index = 1001, level = 1, quantity = 1),
                                ModAvatarData(index = 1002, level = 2, quantity = 2),
                                ModAvatarData(index = 1003, level = 1, quantity = 3)
                        ),
                        gun = gdxArrayOf(
                                ModAvatarData(index = 2001, level = 1, quantity = 1),
                                ModAvatarData(index = 2002, level = 1, quantity = 2),
                                ModAvatarData(index = 2002, level = 1, quantity = 3),
                                ModAvatarData(index = 2002, level = 1, quantity = 4),
                                ModAvatarData(index = 2002, level = 1, quantity = 5),
                                ModAvatarData(index = 2002, level = 1, quantity = 6)
                        )
                )
        )
        val json = context.inject<Json>()
        file.writeString(json.prettyPrint(playerData, printSettings), false)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(assets.BG_COLOR.r, assets.BG_COLOR.g, assets.BG_COLOR.b, assets.BG_COLOR.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }

    override fun dispose() {
        stage.dispose()
    }
}

class SwipeMenu(context: Context) : Group() {
    val assets = context.inject<Assets>()
    val json = context.inject<Json>()
    val playerDataFile = "json/playerData.json".toLocalFile()
    val playerData = json.fromJson<PlayerData>(playerDataFile)
    val pages = gdxArrayOf(
            Constants.MOD_ICON to ModPage(playerData),
            Constants.EQUIP_ICON to EquipPage(playerData))

    val scrollPane: ScrollPane
    val bottomNav: BottomNav

    init {
        setSize(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat())
        val (pageNames, pageContent) = pages.unzip()
        scrollPane = scene2d.scrollPane {
            setFillParent(true)
            setScrollingDisabled(false, true)
            setOverscroll(false, false)
            setScrollbarsVisible(false)
            setFlickScroll(false)
            table {
                pageContent.forEach { container(it) }
            }
        }
        bottomNav = BottomNav(pageNames.toGdxArray())

        addActor(scrollPane)
        addActor(bottomNav)
    }

    fun updateLabels() {
        val l = (((((scrollPane.children[0] as Table).children[1] as Container<*>).actor as EquipPage).children[0] as Table).children[0] as Label)
        l.setText(playerData.name)
    }

    inner class BottomNav(pageNames: Array<String>) : Group() {
        init {
            width = Constants.D_WIDTH.toFloat()
            height = 50f
            val content = scene2d.table {
                setFillParent(true)
                defaults().expand()
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                pageNames.forEachIndexed { index, name ->
                    image(assets.manager.get<Texture>(name))
                            .apply { setScaling(Scaling.fit) }
                            .cell(fill = true, padTop = 5f, padBottom = 5f)
                            .onClickEvent { event, actor ->
//                                println("[EVENT = $event; ACTOR = $actor]")
                                updateLabels()
                                scrollPane.scrollX = index * Constants.D_WIDTH.toFloat()
                            }
                }
            }
            addActor(content)
        }
    }
}

abstract class Page : Group() {
    init {
        width = Constants.D_WIDTH.toFloat()
        height = Constants.D_HEIGHT.toFloat()
    }

    abstract fun update()
}

class ModPage(playerData: PlayerData) : Page() {
    init {
        val rootTable = scene2d.table {
            setFillParent(true)
            label("Player name = ${playerData.name}")
            row()
            textButton("update name").onClickEvent { event, actor ->
                playerData.name = "Serega"
            }
            row()
        }
        addActor(rootTable)
    }

    override fun update() {}
}

class EquipPage(playerData: PlayerData) : Page() {
    init {
        val rootTable = scene2d.table {
            setFillParent(true)
            label("Player coins = ${playerData.coins}")
        }
        addActor(rootTable)
    }

    override fun update() {}
}