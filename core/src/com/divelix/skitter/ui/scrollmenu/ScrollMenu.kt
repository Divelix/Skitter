package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.container
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.PlayerData
import com.divelix.skitter.image
import ktx.actors.onClickEvent
import ktx.assets.toLocalFile
import ktx.collections.gdxArrayOf
import ktx.collections.toGdxArray
import ktx.inject.Context
import ktx.json.fromJson
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.style.get

class ScrollMenu(context: Context) : Group() {
    val assets = context.inject<Assets>()
    val json = context.inject<Json>()
    val playerDataFile = "json/playerData.json".toLocalFile()
    val playerData = json.fromJson<PlayerData>(playerDataFile)
    val pages = gdxArrayOf(
            Constants.EQUIP_ICON to EquipPage(playerData, assets),
            Constants.MOD_ICON to ModPage(playerData))

    val scrollPane: ScrollPane

    init {
        val aspectRatio = Gdx.graphics.height.toFloat() / Gdx.graphics.width.toFloat()
        setSize(Constants.STAGE_WIDTH.toFloat(), Constants.STAGE_WIDTH.toFloat() * aspectRatio)
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
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>("bg"))
                pageNames.forEachIndexed { index, name ->
                    image(assets.manager.get<Texture>(name))
                            .apply { setScaling(Scaling.fit) }
                            .cell(fill = true, padTop = 5f, padBottom = 5f)
                            .onClickEvent { event, actor ->
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