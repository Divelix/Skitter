package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import ktx.assets.toInternalFile
import ktx.assets.toLocalFile
import ktx.vis.table

class EquipTable(tabName: String, val assets: Assets): Table() {
    private val reader = JsonReader()
    private val playerDataFile = Constants.PLAYER_FILE.toLocalFile()
    private val playerData = reader.parse(playerDataFile)
    private val modsData = reader.parse(Constants.MODS_FILE.toInternalFile())
    private val equipData: JsonValue
    private val equipMods: JsonValue

    private val equipSpecs = Array<Float>(6)
    private val suitMods = Array<Mod>(8)
    private val stockMods = Array<Mod>(20)
    private val specNames = Array<String>()

    lateinit var modsType: String
    lateinit var equipFile: String
    lateinit var equipTypeName: String
    lateinit var activeEquipName: String

    private val infoTable: Table
    lateinit var icon: Image
    lateinit var specsTable: Table
    private val suitTable: Table
    private val stockTable: Table

    init {
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
        equipData = reader.parse(equipFile.toInternalFile())
        equipMods = modsData.get("mods").get(modsType)
        useJsonFile()

        infoTable = makeInfoTable()
        suitTable = makeSuitTable()
        stockTable = makeStockTable()

        // make UI
        setFillParent(true)
        top()
        padTop(25f)
        defaults().left().growX()
        debugAll()

        add(infoTable)
        row()
        add(suitTable)
        row()
        add(ScrollPane(stockTable))
    }

    private fun useJsonFile() {
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
            }.forEach { stockMods.add(Mod(index, it.get("name").asString(), level, quantity)) }
        }
        suitMods.forEach { suitMod -> // consider repetition
            stockMods.filter { it.index == suitMod.index }.forEach {it.quantity--}
        }
        stockMods.filter { it.quantity == 0 }.forEach { stockMods.removeValue(it, true) }
    }

    private fun makeInfoTable(): Table {
        return table {
            icon = image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.RIFLE))) { cell ->
                cell.size(64f, 64f)
            }
            table {
                padLeft(10f)
                defaults().expandX().left()
                table {
                    defaults().left()
                    specNames.forEach { label("${it.toUpperCase()}:"); row() }
                }
                specsTable = table {
                    padLeft(5f)
                    defaults().left()
                    equipSpecs.forEach { label(it.toString());row() }
                }
            }
        }
    }

    private fun makeSuitTable(): Table {
        return table {
            name = "SuitTable"
            pad(25f, 0f, 25f, 0f)
            defaults().width(Constants.MOD_WIDTH).height(Constants.MOD_HEIGHT).pad(2f)

            for (i in 1..8) {
                if (i <= suitMods.size) {
                    container(ModImage(suitMods[i - 1], assets)) {
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
    }

    private fun makeStockTable(): Table {
        return table {
            name = "StockTable"
            defaults().width(Constants.MOD_WIDTH).height(Constants.MOD_HEIGHT).pad(2f)

            for (i in 0 until stockMods.size + 8) {
                if (i < stockMods.size) {
                    container(ModImage(stockMods[i], assets)) { touchable = Touchable.enabled }
                } else {
                    container<ModImage> { touchable = Touchable.enabled }
                }
                if ((i+1) % 4 == 0) row()
            }
        }
    }
}