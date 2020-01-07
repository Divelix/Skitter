package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.*
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.kotcrab.vis.ui.widget.VisLabel
import ktx.assets.toInternalFile
import ktx.vis.table

class EquipTable(private val tabName: String, val assets: Assets, val reader: JsonReader, val playerData: JsonValue): Table() {
    private val equipSpecs = Array<Float>(6)
    private val modEffects = Array<Float>(6)
    private val finalEquipSpecs = Array<Float>(6)

    private val suitMods = Array<Mod>(8)
    private val stockMods = Array<Mod>(20)
    private val specNames = Array<String>()
    private val equipMods: JsonValue
    private val equipData: JsonValue

    private val infoTable: Table
    lateinit var specsTable: Table
    val suitTable: Table
    val stockTable: Table

    private val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
    private val bgDrawable = TextureRegionDrawable(Texture(bgPixel.apply {setColor(Constants.UI_COLOR); fill()}))

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
            (specsTable.children[i] as VisLabel).setText("${MathUtils.round(finalEquipSpecs[i] * 10) / 10f}")
        }
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
                    specNames.forEach { label("${it.toUpperCase()}:", "equip-specs").apply { setFontScale(0.25f) }; row() }
                }
                specsTable = table {
//                    padLeft(5f)
                    defaults().left()
                    equipSpecs.forEach { label(it.toString(), "equip-specs").apply { setFontScale(0.25f) };row() }
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