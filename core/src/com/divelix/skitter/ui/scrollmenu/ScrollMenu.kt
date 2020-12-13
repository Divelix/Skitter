package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.*
import com.divelix.skitter.image
import ktx.actors.onClickEvent
import ktx.assets.toLocalFile
import ktx.collections.gdxArrayOf
import ktx.collections.toGdxArray
import ktx.inject.Context
import ktx.json.fromJson
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.container
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.style.get

class ScrollMenu(context: Context) : Group() {
    val assets = context.inject<Assets>()
    val json = context.inject<Json>()
    val playerDataFile = "json/playerData.json".toLocalFile()
    val shipsDataFile = "json/ships.json".toLocalFile()
    val gunsDataFile = "json/guns.json".toLocalFile()
    val playerData = json.fromJson<Player>(playerDataFile)
    val shipsData = json.fromJson<ShipsData>(shipsDataFile)
    val gunsData = json.fromJson<GunsData>(gunsDataFile)
    val pages = gdxArrayOf(
            Constants.EQUIP_ICON to EquipPage(context, playerData),
            Constants.BATTLE_ICON to PlayPage(context, playerData),
            Constants.MOD_ICON to ModPage(context, playerData))

    val scrollPane: ScrollPane

    init {
        setSize(Constants.STAGE_WIDTH.toFloat(), Constants.stageHeight)
        val (pageNames, pageContent) = pages.unzip()
        scrollPane = scene2d.scrollPane {
            setFillParent(true)
            setScrollingDisabled(false, true)
            setOverscroll(false, false)
            setScrollbarsVisible(false)
            setFlickScroll(false)
            table {
                top()
                pageContent.forEach { container(it) }
            }
        }
        addActor(scrollPane)
        addActor(BottomNav(pageNames.toGdxArray()))
    }

    fun updateUI() {
        pages.forEach {
            it.second.update()
        }
    }

    fun saveToJson() {
        val printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }
        playerDataFile.writeString(json.prettyPrint(playerData, printSettings), false)
        log.debug { "Player data was saved to json" }
    }

    inner class BottomNav(pageNames: Array<String>) : Group() {
        init {
            width = Constants.STAGE_WIDTH.toFloat()
            height = 50f
            val content = scene2d.table {
                setFillParent(true)
                defaults().expand()
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_70))
                pageNames.forEachIndexed { index, name ->
                    image(assets.manager.get<Texture>(name))
                            .apply { setScaling(Scaling.fit) }
                            .cell(fill = true, padTop = 5f, padBottom = 5f)
                            .onClickEvent { event ->
//                                println("[EVENT = $event; ACTOR = $actor]")
                                updateUI()
//                                saveToJson()
                                scrollPane.scrollX = index * Constants.STAGE_WIDTH.toFloat()
                            }
                }
            }
            addActor(content)
        }
    }

    companion object {
        val log = logger<ScrollMenu>()
    }
}