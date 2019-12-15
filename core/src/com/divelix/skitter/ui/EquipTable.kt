package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.kotcrab.vis.ui.widget.VisLabel
import ktx.assets.toInternalFile
import ktx.assets.toLocalFile
import ktx.vis.table

class EquipTable(val tabName: String, val assets: Assets): Table() {
    private val reader = JsonReader()
    private val playerDataFile = Constants.PLAYER_FILE.toLocalFile()
    private val playerData = reader.parse(playerDataFile)
    private val modsData = reader.parse(Constants.MODS_FILE.toInternalFile())
//    private val equipData: JsonValue
//    private val equipMods: JsonValue

    private val equipSpecs = Array<Float>(6)
    private val suitMods = Array<Mod>(8)
    private val stockMods = Array<Mod>(20)
    private val specNames = Array<String>()

    private val infoTable: Table
    lateinit var specsTable: Table
    private val suitTable: Table
    private val stockTable: Table

    val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
    val bgDrawable = TextureRegionDrawable(Texture(bgPixel.apply {setColor(Color(0f, 0f, 0f, 0.3f)); fill()}))

    init {
        useJsonFile()

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
            padTop(14f)
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

    private fun useJsonFile() {
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
            }.forEach { stockMods.add(Mod(index, it.get("name").asString(), level, quantity)) }
        }
        suitMods.forEach { suitMod -> // consider repetition
            stockMods.filter { it.index == suitMod.index }.forEach {it.quantity--}
        }
        stockMods.filter { it.quantity == 0 }.forEach { stockMods.removeValue(it, true) }
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

    class ModIcon(val mod: Mod, val assets: Assets): Group() {
        private val iconSize = Constants.MOD_WIDTH / 2f
        private val bgColor = Color(1f, 1f, 0f, 1f)
        private val lvlColor = Color(0f, 0f, 0f, 1f)
        private val noLvlColor = Color(1f, 1f, 1f, 1f)

        init {
            touchable = Touchable.enabled
            setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT)

            val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            val bgDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(bgColor); fill()}))
            val lvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(lvlColor); fill()}))
            val noLvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(noLvlColor); fill()}))

            val bg = Image(bgDrawable).apply { setFillParent(true) }
            val texture: Texture = when(mod.index) {
                1 -> assets.manager.get(Constants.STAR)
                else -> assets.manager.get(Constants.BACKGROUND_IMAGE)
            }
            val icon = Image(texture).apply {
                setSize(iconSize, iconSize)
                setPosition((this@ModIcon.width - width) / 2f, (this@ModIcon.height - height) / 2f)
            }
            val quantityBg = Image(lvlDrawable).apply {
                setSize(14f, 14f)
                setPosition(this@ModIcon.width - width, this@ModIcon.height - height)
            }
            val quantityLabel = VisLabel("${mod.quantity}").apply {
                //                setPosition(this@ModIcon.width - width, this@ModIcon.height - height)
                setPosition(quantityBg.x + (quantityBg.width-width)/2f, quantityBg.y + (quantityBg.height-height)/2f)
                touchable = Touchable.disabled
            }
            val levelBars = table {
                bottom().left()
                pad(2f)
                defaults().pad(1f)
                for (i in 1..10) {
                    image(if (i <= mod.level) lvlDrawable else noLvlDrawable) {it.size(4f)}
                }
            }

            addActor(bg)
            addActor(icon)
            addActor(quantityBg)
            addActor(quantityLabel)
            addActor(levelBars)
        }
    }

    class EmptyMod: Group() {
        private val bgColor = Color(0f, 0f, 0f, 0.3f)

        init {
            setSize(64f, 64f)
            val pixel = Pixmap(1, 1, Pixmap.Format.Alpha)
            val bgDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(bgColor); fill()}))
            val img = Image(bgDrawable).apply { setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT) }
            addActor(img)
        }
    }
}