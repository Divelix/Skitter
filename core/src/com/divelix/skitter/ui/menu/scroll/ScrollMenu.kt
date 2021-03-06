package com.divelix.skitter.ui.menu.scroll

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.*
import com.divelix.skitter.data.JsonProcessor
import com.divelix.skitter.data.editors.PlayerDataEditor
import ktx.actors.onClickEvent
import ktx.assets.toInternalFile
import ktx.assets.toLocalFile
import ktx.collections.gdxArrayOf
import ktx.collections.toGdxArray
import ktx.inject.Context
import ktx.json.fromJson
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.*
import ktx.style.get

class ScrollMenu(context: Context) : Group() {
    private val playerDataPath = "json/playerData.json"
    private val playerDataFile = playerDataPath.toLocalFile()
    private val playerData: PlayerData = try {
        JsonProcessor.fromJson(playerDataPath.toLocalFile())
    } catch (e: SerializationException) {
        JsonProcessor.fromJson(playerDataPath.toInternalFile())
    }
    private val activePlayerData = ActivePlayerData()
    private val equipPage = EquipPage(context, playerData, activePlayerData)
    private val playPage = PlayPage(context, activePlayerData)
    private val modPage = ModPage(context, playerData, equipPage::reloadForModType)
    private val pages = gdxArrayOf(
            RegionName.EQUIP_ICON() to equipPage,
            RegionName.BATTLE_ICON() to playPage,
            RegionName.MOD_ICON() to modPage)

    private val scrollPane: ScrollPane

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

        // set initial scroll to middle page
        scrollPane.layout()
        scrollPane.scrollX = Constants.STAGE_WIDTH.toFloat()
        scrollPane.updateVisualScroll() // disables animation
    }

    fun saveToJson() {
        val printSettings = JsonValue.PrettyPrintSettings().apply {
            outputType = JsonWriter.OutputType.json
            singleLineColumns = 100
        }
        playerDataFile.writeString(JsonProcessor.prettyPrint(playerData, printSettings), false)
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
                    image(Scene2DSkin.defaultSkin.get<TextureRegion>(name))
                            .apply { setScaling(Scaling.fit) }
                            .cell(fill = true, padTop = 5f, padBottom = 5f)
                            .onClickEvent { _ ->
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