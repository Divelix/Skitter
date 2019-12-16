package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import ktx.assets.toInternalFile
import ktx.assets.toLocalFile
import ktx.vis.table

class EquipTable(private val tabName: String, val assets: Assets): Table() {
    private val equipSpecs = Array<Float>(6)
    private val suitMods = Array<Mod>(8)
    private val stockMods = Array<Mod>(20)
    private val specNames = Array<String>()

    private val infoTable: Table
    lateinit var specsTable: Table
    val suitTable: Table
    val stockTable: Table

    private val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
    private val bgDrawable = TextureRegionDrawable(Texture(bgPixel.apply {setColor(Color(0f, 0f, 0f, 0.3f)); fill()}))

    init {
        readJsonData()
        infoTable = makeInfoTable()
        suitTable = makeSuitTable()
        stockTable = makeStockTable()

        val topPart = table {
            name = "TopPart"
            background = bgDrawable
            add(infoTable)
            row()
            add(suitTable)
        }
        val botPart = table {
            name = "BotPart"
            padTop(12f)
            add(ScrollPane(stockTable))
        }
        // make UI
        setFillParent(true)
        top()
        padTop(12f)
        add(topPart)
        row()
        add(botPart)
    }

    private fun readJsonData() {
        val reader = JsonReader()
        val playerDataFile = Constants.PLAYER_FILE.toLocalFile()
        val playerData = reader.parse(playerDataFile)
        val modsData = reader.parse(Constants.MODS_FILE.toInternalFile())
        var modsType = ""
        var equipFile = ""
        var equipTypeName = ""
        var activeEquipName = ""
        when (tabName) {
            Constants.SHIPS_TAB -> {
                modsType = "ship"
                equipFile = Constants.SHIPS_FILE
                equipTypeName = "ships"
                activeEquipName = "active_ship"
            }
            Constants.GUNS_TAB -> {
                modsType = "gun"
                equipFile = Constants.GUNS_FILE
                equipTypeName = "guns"
                activeEquipName = "active_gun"
            }
        }
        val equipData = reader.parse(equipFile.toInternalFile())
        val equipMods = modsData.get("mods").get(modsType)
        equipData.get("specs").forEach { specNames.add(it.asString()) }
        val allEquips = equipData.get(equipTypeName)
        val activeEquipDescription = playerData.get(equipTypeName)[playerData.get(activeEquipName).asInt()]
        val activeEquip = allEquips.filter { it.get("index").asInt() == activeEquipDescription.get("index").asInt() }[0]
        activeEquip.get("specs").forEach { equipSpecs.add(it.asFloat()) }

//        // fill suitMods
        activeEquipDescription.get("mods").forEach { modDescription ->
            equipMods.filter { it.get("index").asInt() == modDescription.get("index").asInt() }
                    .forEach { suitMods.add(Mod(it.get("index").asInt(), it.get("name").asString(), modDescription.get("level").asInt())) }
        }

        // fill stockMods
        playerData.get("mods").get(modsType).forEach {modDescription ->
            val index = modDescription.get("index").asInt()
            val level = modDescription.get("level").asInt()
            val quantity = modDescription.get("quantity").asInt()
            equipMods.filter { mod ->
                val effects = mod.get("effects")
                mod.get("index").asInt() == index
            }.forEach { stockMods.add(Mod(index, it.get("name").asString(), level)) }
        }
        suitMods.forEach { suitMod -> // consider repetition
            stockMods.filter { it.index == suitMod.index }.forEach {it.quantity--}
        }
        stockMods.filter { it.quantity == -1 }.forEach { stockMods.removeValue(it, true) }
    }

    private fun makeInfoTable(): Table {
        return table {
            // make table width equal 298 = 99 + 100 + 99
            val textWidth = 99f
            val tableHeight = 100f
            pad(14f, 14f, 0f, 14f)
            label("Description for this specific gun") {
                it.size(textWidth, tableHeight)
                setWrap(true)
                setAlignment(Align.center)
            }
            container(image(getEquipDrawable(1, tabName == Constants.SHIPS_TAB))) {
                background = bgDrawable
                pad(10f)
                it.size(tableHeight, tableHeight)
            }
            table {
                it.size(textWidth, tableHeight)
                defaults().left()
                table {
                    defaults().left()
                    specNames.forEach { label("${it.toUpperCase()}:", "mod-quantity"); row() }
                }
                specsTable = table {
//                    padLeft(5f)
                    defaults().left()
                    equipSpecs.forEach { label(it.toString(), "mod-quantity");row() }
                }
            }
        }
    }

    private fun makeSuitTable(): Table {
        return table {
            name = "SuitTable"
            pad(7f)
            defaults().pad(7f)

            for (i in 1..8) {
                if (i <= suitMods.size) {
                    container(ModIcon(suitMods[i - 1], assets))
                } else {
                    container(EmptyMod())
                }
                if (i % 4 == 0) row()
            }
        }
    }

    private fun makeStockTable(): Table {
        return table {
            name = "StockTable"
            background = bgDrawable
            pad(7f)
            defaults().pad(7f)

            for (i in 0 until stockMods.size + 8) {
                if (i < stockMods.size) {
                    container(ModIcon(stockMods[i], assets))
                } else {
                    container(EmptyMod())
                }
                if ((i+1) % 4 == 0) row()
            }
        }
    }

    private fun getEquipDrawable(index: Int, isShip: Boolean): TextureRegionDrawable {
        val textureName = if (isShip) {
            when (index) {
                1 -> Constants.BUCKET_ICON
                else -> Constants.AIM
            }
        } else {
            when (index) {
                1 -> Constants.PISTOL_ICON
                else -> Constants.AIM
            }
        }
        return TextureRegionDrawable(assets.manager.get<Texture>(textureName))
    }
}