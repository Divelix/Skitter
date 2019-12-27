package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.ObjectMap
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import ktx.assets.toInternalFile
import ktx.vis.table

class StockTable(tabName: String, val assets: Assets, val reader: JsonReader, val playerData: JsonValue): Table() {
    val stockTable: Table
    private val stockMods = Array<Mod>(20)

    private val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
    private val bgDrawable = TextureRegionDrawable(Texture(bgPixel.apply {setColor(Constants.UI_COLOR); fill()}))

    val modsData = reader.parse(Constants.MODS_FILE.toInternalFile())
    var modsType = ""
    var equipFile = ""
    var equipTypeName = ""

    init {
        when (tabName) {
            Constants.SHIPS_TAB -> {
                modsType = "ship"
                equipFile = Constants.SHIPS_FILE
                equipTypeName = "ships"
            }
            Constants.GUNS_TAB -> {
                modsType = "gun"
                equipFile = Constants.GUNS_FILE
                equipTypeName = "guns"
            }
        }
        readJsonData()
        stockTable = makeStockTable()

        padTop(12f)
        add(stockTable)

        updateSpecs()
    }

    private fun readJsonData() {
        val equipMods = modsData.get("mods").get(modsType)

        // fill stockMods
        playerData.get("mods").get(modsType).forEach {modDescription ->
            val index = modDescription.get("index").asInt()
            val level = modDescription.get("level").asInt()
            val quantity = modDescription.get("quantity").asInt()
            val equipMod = equipMods.single { it.get("index").asInt() == index }
            val effects = ObjectMap<String, Float>()
            equipMod.get("effects").forEach { effects.put(it.name, it[level-1].asFloat()) }
            stockMods.add(Mod(index, equipMod.get("name").asString(), level, quantity, effects))
        }
    }

    private fun updatePlayerData() {
        for (field in playerData) {
            when(field.name) {
                "mods" -> {// TODO update mods
                    val equipMods = field.get(modsType)
                }
                equipTypeName -> {// TODO update mods in equips
                    val activeEquipMods = field[0].get("mods")
                }
            }
        }
    }

    private fun updateSpecs() {
        println("Implement updateSpecs()")
    }

    private fun makeStockTable(): Table {
        return table {
            name = "StockTable"
            background = bgDrawable
            pad(7f)
            defaults().pad(7f)

            for (i in 0 until stockMods.size - stockMods.size % 4 + 8) {
                if (i < stockMods.size) {
                    container(ModIcon(stockMods[i], assets))
                } else {
                    container(EmptyMod())
                }
                if ((i+1) % 4 == 0) row()
            }
        }
    }
}