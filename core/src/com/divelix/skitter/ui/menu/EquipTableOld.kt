package com.divelix.skitter.ui.menu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModOld
import com.divelix.skitter.image
import com.divelix.skitter.ui.ScaledLabel
import ktx.actors.txt
import ktx.assets.toInternalFile
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get

class EquipTableOld(private val tabName: String, val assets: Assets, val reader: JsonReader, val playerData: JsonValue): Table() {
    private val equipSpecs = Array<Float>(6)
    private val modEffects = Array<Float>(6)
    private val finalEquipSpecs = Array<Float>(6)

    private val suitMods = Array<ModOld>(8)
    private val stockMods = Array<ModOld>(20)
    private val specNames = Array<String>()
    private val equipMods: JsonValue
    private val equipData: JsonValue

    private val infoTable: Table
    lateinit var specsTable: Table
    val suitTable: Table
    val stockTable: Table

    val infoLabel = ScaledLabel()

    val modsData = reader.parse(Constants.MODS_FILE.toInternalFile())
    var modsType = ""
    var equipFile = ""
    var equipTypeName = ""
    var activeEquipName = ""
    var activeEquipSpecs = ""

    init {
        when (tabName) {
            Constants.SHIPS_TAB -> {
                modsType = "ship"
                equipFile = Constants.SHIPS_FILE
                equipTypeName = "ships"
                activeEquipName = "active_ship"
                activeEquipSpecs = "active_ship_specs"
            }
            Constants.GUNS_TAB -> {
                modsType = "gun"
                equipFile = Constants.GUNS_FILE
                equipTypeName = "guns"
                activeEquipName = "active_gun"
                activeEquipSpecs = "active_gun_specs"
            }
        }
        equipMods = modsData.get("mods").get(modsType)
        equipData = reader.parse(equipFile.toInternalFile())
        readJsonData()
        infoTable = makeInfoTable()
        suitTable = makeSuitTable()
        stockTable = makeStockTable()

        val topPart = scene2d.table {
            name = "TopPart"
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            add(infoTable)
            row()
            add(suitTable)
        }
        val botPart = scene2d.table {
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

        updateSpecs()
    }

    private fun readJsonData() {
        equipData.get("specs").forEach { specNames.add(it.asString()) }
        val allEquips = equipData.get(equipTypeName)
        val activeEquipDescription = playerData.get(equipTypeName)[playerData.get(activeEquipName).asInt()]
        val activeEquip = allEquips.filter { it.get("index").asInt() == activeEquipDescription.get("index").asInt() }[0]
        activeEquip.get("specs").forEach { equipSpecs.add(it.asFloat()) }
        equipSpecs.forEach {
            finalEquipSpecs.add(it)
            modEffects.add(1f)
        }

//        // fill suitMods
        activeEquipDescription.get("mods").forEach { modDescription ->
            equipMods.filter { it.get("index").asInt() == modDescription.get("index").asInt() }
                    .forEach { suitMods.add(ModOld(it.get("index").asInt(), it.get("name").asString(), modDescription.get("level").asInt())) }
        }

        // fill stockMods
        playerData.get("mods").get(modsType).forEach {modDescription ->
            val index = modDescription.get("index").asInt()
            val level = modDescription.get("level").asInt()
//            val quantity = modDescription.get("quantity").asInt()
            equipMods.filter { mod ->
//                val effects = mod.get("effects")
                mod.get("index").asInt() == index
            }.forEach { stockMods.add(ModOld(index, it.get("name").asString(), level)) }
        }
        suitMods.forEach { suitMod -> // consider repetition
            stockMods.filter { it.index == suitMod.index && it.level == suitMod.level }.forEach {it.quantity--}
        }
        stockMods.filter { it.quantity == -1 }.forEach { stockMods.removeValue(it, true) }
    }

    fun updatePlayerData() {
        suitMods.clear()
        suitTable.children.filterIsInstance<Container<ModIcon>>().filter { it.actor is ModIcon }.forEach { suitMods.add(it.actor.mod) }
        for (field in playerData) {
            when(field.name) {
                activeEquipName -> field.set(0, null) // TODO change when gun switch implemented
                activeEquipSpecs -> {
                    for (i in 0 until field.size)
                        field[i].set(finalEquipSpecs[i].toDouble(), null)
                }
                equipTypeName -> {
                    val activeEquipMods = field[0].get("mods")

                    // clear mods JsonValue before writing
                    for (i in 0 until activeEquipMods.size)
                        activeEquipMods.remove(0)

                    suitMods.forEach {
                        val jsonMod = JsonValue(JsonValue.ValueType.`object`).apply {
                            addChild("index", JsonValue(it.index.toLong()))
                            addChild("level", JsonValue(it.level.toLong()))
                        }
                        activeEquipMods.addChild(jsonMod)
                        activeEquipMods.size++
                    }
                }
            }
        }
    }

    fun updateSpecs() {
        for (i in 0 until equipSpecs.size) {
            finalEquipSpecs[i] = equipSpecs[i]
            modEffects[i] = 1f
        }
        val equipSpecsValues = equipData.get("specs").asStringArray()
        suitTable.children.filterIsInstance<Container<ModIcon>>().filter { it.actor is ModIcon }.forEach {
            val suitMod = it.actor.mod
            val equipMod = equipMods.single { it.get("index").asInt() == suitMod.index }
            equipMod.get("effects").forEach {effect ->
                for (i in equipSpecsValues.indices) {
                    if (effect.name == equipSpecsValues[i])
                        modEffects[i] += effect[suitMod.level-1].asFloat()
                }
            }
        }
        for (i in 0 until finalEquipSpecs.size) {
            finalEquipSpecs[i] = equipSpecs[i] * modEffects[i]
            (specsTable.children[i] as ScaledLabel).txt = "${MathUtils.round(finalEquipSpecs[i] * 10) / 10f}"
        }
    }

    private fun makeInfoTable(): Table {
        return scene2d.table {
            // make table width equal 298 = 99 + 100 + 99
            val textWidth = 99f
            val tableHeight = 100f
            pad(14f, 14f, 0f, 14f)
            add(infoLabel.apply {
                txt = "Description for this specific equipment"
                wrap = true
                setAlignment(Align.center)
            }).size(textWidth, tableHeight)
            container {
                image(getEquipDrawable(1, tabName == Constants.SHIPS_TAB))
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                pad(10f)
                it.size(tableHeight, tableHeight)
            }
            table {
//                it.size(textWidth, tableHeight)
                defaults().left()
                table {
                    defaults().left()
                    specNames.forEach {
                        add(ScaledLabel("${it.toUpperCase()}:", "equip-specs", 0.25f))
//                        label("${it.toUpperCase()}:", "equip-specs").apply { setFontScale(0.25f) }
                        row()
                    }
                }
                specsTable = table {
//                    padLeft(5f)
                    defaults().left()
                    equipSpecs.forEach {
                        add(ScaledLabel(it.toString(), "equip-specs", 0.25f))
//                        label(it.toString(), "equip-specs").apply { setFontScale(0.25f) }
                        row()
                    }
                }
            }
        }
    }

    private fun makeSuitTable(): Table {
        return scene2d.table {
            name = "SuitTable"
            pad(7f)
            defaults().pad(7f)

            for (i in 1..8) {
                if (i <= suitMods.size) {
                    container(ModIcon(suitMods[i - 1], assets))
                } else {
                    container(EmptyModIcon(assets))
                }
                if (i % 4 == 0) row()
            }
        }
    }

    private fun makeStockTable(): Table {
        return scene2d.table {
            name = "StockTable"
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            pad(7f)
            defaults().pad(7f)

            for (i in 0 until stockMods.size + 8) {
                if (i < stockMods.size) {
                    container(ModIcon(stockMods[i], assets))
//                    container { modIcon(stockMods[i], assets) }
//                    modWidget(stockMods[i], assets)
                } else {
                    container(EmptyModIcon(assets))
                }
                if ((i+1) % 4 == 0) row()
            }
        }
    }

    private fun getEquipDrawable(index: Int, isShip: Boolean): TextureRegionDrawable {
        val textureName = if (isShip) {
            when (index) {
                1 -> Constants.SHIP_DEFAULT
                else -> Constants.AIM
            }
        } else {
            when (index) {
                1 -> Constants.GUN_DEFAULT
                else -> Constants.AIM
            }
        }
        return TextureRegionDrawable(assets.manager.get<Texture>(textureName))
    }
}