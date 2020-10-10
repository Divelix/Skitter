package com.divelix.skitter.ui.menu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonValue
import com.badlogic.gdx.utils.ObjectMap
import com.divelix.skitter.container
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.Mod
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get

class StockTable(tabName: String, val assets: Assets, val playerData: JsonValue, modsData: JsonValue): Table() {
    val stockTable: Table
    private val stockMods = Array<Mod>(20)
    private val equipMods: JsonValue

    var modsType = ""
    var equipTypeName = ""

    init {
        when (tabName) {
            Constants.SHIPS_TAB -> {
                modsType = "ship"
                equipTypeName = "ships"
            }
            Constants.GUNS_TAB -> {
                modsType = "gun"
                equipTypeName = "guns"
            }
        }
        equipMods = modsData.get("mods").get(modsType)
        readJsonData()
        stockTable = makeStockTable()

        padTop(12f)
        add(ScrollPane(stockTable))

        updateLabels()
    }

    private fun readJsonData() {
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

    fun updatePlayerData() {
        for (field in playerData) {
            when(field.name) {
                "mods" -> {// TODO update mods
                    val equipMods = field.get(modsType)
                    for (i in 0 until equipMods.size)
                        equipMods.remove(0)
                    stockMods.forEach {
                        val jsonMod = JsonValue(JsonValue.ValueType.`object`).apply {
                            addChild("index", JsonValue(it.index.toLong()))
                            addChild("level", JsonValue(it.level.toLong()))
                            addChild("quantity", JsonValue(it.quantity.toLong()))
                        }
                        equipMods.addChild(jsonMod)
                        equipMods.size++
                    }
                }
//                equipTypeName -> {// TODO update mods in equips
//                    val activeEquipMods = field[0].get("mods")
//                }
            }
        }
    }

    private fun makeStockTable(): Table {
        return scene2d.table {
            name = "StockTable"
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            pad(7f)
            defaults().pad(7f)

            for (i in 0 until stockMods.size - stockMods.size % 4 + 8) {
                if (i < stockMods.size) {
                    container(ModIcon(stockMods[i], assets))
                } else {
                    container(EmptyModIcon(assets))
                }
                if ((i+1) % 4 == 0) row()
            }
        }
    }

    fun subtractMod(mod: Mod) {
        mod.quantity--
        updateLabels()
    }

    fun addMod(mod: Mod) {
        stockMods.add(mod)
        val emptyContainer = stockTable.children.first { (it as Container<*>).actor is EmptyModIcon } as Container<*>
        emptyContainer.actor = ModIcon(mod, assets)
    }

    fun updateLabels() {
        stockTable.children.filterIsInstance<Container<*>>().forEach {
            if (it.actor is ModIcon) {
                val modIcon = it.actor as ModIcon
                modIcon.updateLevelBars()
                if (modIcon.mod.quantity > 0) {
                    modIcon.quantityLabel.setText(modIcon.mod.quantity)
                } else {
                    modIcon.remove()
                    stockMods.removeValue(modIcon.mod, true)
                    it.actor = EmptyModIcon(assets)
                }
            }
        }
    }
}