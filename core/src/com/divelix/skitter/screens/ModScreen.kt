package com.divelix.skitter.screens

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.data.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.data.ModOld
import com.divelix.skitter.image
import com.divelix.skitter.ui.menu.EditScreen
import com.divelix.skitter.ui.menu.EmptyModIcon
import com.divelix.skitter.ui.menu.ModIcon
import com.divelix.skitter.ui.menu.StockTable
import com.divelix.skitter.ui.ScaledLabel
import ktx.actors.plusAssign
import ktx.actors.txt
import ktx.log.info
import ktx.collections.*
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visTable
import ktx.style.get

class ModScreen(game: Main): EditScreen(game) {
    private val bigMod = BigMod()
    private val sellPrices = modsData.get("sell_prices").asIntArray()
    private val upgradePrices = modsData.get("upgrade_prices").asIntArray()
    private var coins = playerData.get("coins").asInt()

    private val rootTable: Table
    private val coinsLabel = ScaledLabel("$coins", "mod-name")
    private val modName = ScaledLabel("", "mod-name")
    private val modSpecs = ScaledLabel()
    private val sellPriceLabel = ScaledLabel("0", "mod-name")
    private val upgradePriceLabel = ScaledLabel("0", "mod-name")

    init {
        tabbedBar.tabs[0].content = StockTable(tabbedBar.tabs[0].tabName, assets, playerData, modsData)
        tabbedBar.tabs[1].content = StockTable(tabbedBar.tabs[1].tabName, assets, playerData, modsData)
        tabbedBar.makeActive(tabbedBar.tabs[0])

        rootTable = scene2d.table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                right().pad(12f)
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                add(coinsLabel)
            }.cell(fillX = true)
            row()
            table {
                pad(12f)
                table {
                    image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.SELL_BTN))).cell(width = 76f, height = 76f).addListener(object: ClickListener() {
                        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                            if (activeMod != null) sellMod(activeMod!!.mod)
                            super.touchUp(event, x, y, pointer, button)
                        }
                    })
                    row()
                    add(sellPriceLabel).padTop(12f)
                }
                table {
                    pad(0f, 12f, 0f, 12f)
                    add(bigMod)
                    row()
                    table {
                        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                        scrollPane (init = {
                            table {
                                pad(12f)
                                add(modName)
                                row()
                                add(modSpecs.apply {
                                    wrap = true
                                    setAlignment(Align.center)
                                })//.cell(width = 126f) // width may be any value
                            }
                        }).cell(width = 150f, height = 78f)
                    }
                }
                table {
                    image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.UP_BTN))).cell(width = 76f, height = 76f).addListener(object: ClickListener() {
                        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                            if (activeMod != null) {
                                upMod(activeMod!!.mod)
                                bigMod.setMod(activeMod)
                            }
                            super.touchUp(event, x, y, pointer, button)
                        }
                    })
                    row()
                    add(upgradePriceLabel).padTop(12f)
                }
            }
            row()
//            tabbedBar(assets)
            add(tabbedBar)
        }
        stage += rootTable
        stage += carriage.apply { setPosition(-height, -width) }
        stage += applyBtn
        stage.addListener(makeStageListener())

        bigMod.setMod(null)
    }

    fun sellMod(mod: ModOld) {
        val stockTable = tabbedBar.content.actor as StockTable
        stockTable.subtractMod(mod)
        if (mod.quantity == 0) deselect()

        val income = sellPrices[mod.level-1]
        coins += income
        playerData.get("coins").set(coins.toLong(), null) // TODO move to save function
        updateUI()
    }

    fun upMod(mod: ModOld) {
        if (coins < upgradePrices[mod.level - 1]) {
            info { "not enough coins (need ${upgradePrices[mod.level - 1] - coins} more)" }
            return
        } else if (mod.level == 10) {
            info { "max level" }
            return
        }

        coins -= upgradePrices[mod.level - 1]
        val stockTable = tabbedBar.activeTab.content as StockTable
        if (mod.quantity == 1) {
            mod.level++
        } else {
            stockTable.addMod(ModOld(mod.index, mod.name, mod.level + 1, 1, mod.effects))
            mod.quantity--
            deselect()
        }
        stockTable.updateLabels()
        updateUI()
    }

    override fun updateUI() {
        coinsLabel.txt = coins.toString()
    }

    override fun processModIcon(modIcon: ModIcon) {
        val container = (modIcon.parent as Container<*>)
        val offset = Vector2(-carriageBorderWidth, -carriageBorderWidth)
        val carriagePos = container.localToStageCoordinates(offset)
        when (activeMod) {
            modIcon -> deselect()
            else -> {
                activeMod = modIcon
                activeModContainer = container
                carriage.setPosition(carriagePos.x, carriagePos.y)
                bigMod.setMod(activeMod)
            }
        }
    }

    override fun processEmptyMod(emptyMod: EmptyModIcon) {
        info { "EmptyMod was clicked" }
    }

    override fun updatePlayerJson() {
        (tabbedBar.tabs[0].content as StockTable).updatePlayerData() // update ships
        (tabbedBar.tabs[1].content as StockTable).updatePlayerData() // update guns
        super.updatePlayerJson()
    }

    override fun deselect() {
        super.deselect()
        bigMod.setMod(null)
    }

    inner class BigMod: Group() {
        private val iconHeight = 75f
        private val bg = Image(TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))).apply { setFillParent(true) }
        private val icon: Image
        private val levelBars: Table

        init {
            setSize(150f, 150f)

            icon = Image()
            levelBars = scene2d.visTable {
                bottom().left()
                pad(5f)
                defaults().pad(2f)
                for (i in 1..10) {
                    visImage(TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))) {it.size(10f)}
                }
            }

            addActor(bg)
            addActor(icon)
            addActor(levelBars)
        }

        fun setMod(modIcon: ModIcon?) {
            if (modIcon != null) {
                bg.drawable = modIcon.bgDrawable
                val texture: Texture = assets.manager.get(modIcon.textureName)
                val aspectRatio = texture.width.toFloat() / texture.height.toFloat()
                icon.run {
                    drawable = TextureRegionDrawable(texture)
                    setSize(iconHeight * aspectRatio, iconHeight)
                    setPosition((this@BigMod.width - width) / 2f, (this@BigMod.height - height) / 2f)
                }
                levelBars.isVisible = true
                for (i in 1..10) {
                    if (i <= modIcon.mod.level)
                        (levelBars.children[i - 1] as Image).drawable = modIcon.lvlDrawable
                    else
                        (levelBars.children[i - 1] as Image).drawable = modIcon.noLvlDrawable
                }
                modName.txt = "<${modIcon.mod.name}>"
                var specString = ""
                modIcon.mod.effects?.forEach { (key, value) ->
                    specString += "$key: $value\n"
                }
                modSpecs.txt = specString
                sellPriceLabel.txt = "${sellPrices[modIcon.mod.level-1]}"
                upgradePriceLabel.txt = "${upgradePrices[modIcon.mod.level-1]}"
            } else {
                bg.drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                icon.drawable = null
                levelBars.isVisible = false
                modName.txt = ""
                modSpecs.txt = ""
                sellPriceLabel.txt = ""
                upgradePriceLabel.txt = ""
            }
        }
    }
}