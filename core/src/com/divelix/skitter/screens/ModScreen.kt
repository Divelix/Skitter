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
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.*
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import ktx.actors.plusAssign
import ktx.log.info
import ktx.vis.table
import ktx.collections.*

class ModScreen(game: Main): EditScreen(game) {
    private val bigMod = BigMod()
    private val sellPrices = modsData.get("sell_prices").asIntArray()
    private val upgradePrices = modsData.get("upgrade_prices").asIntArray()
    private var coins = playerData.get("coins").asInt()

    private val rootTable: VisTable
    lateinit var coinsLabel: VisLabel
    lateinit var modName: VisLabel
    lateinit var modSpecs: VisLabel
    lateinit var sellPriceLabel: VisLabel
    lateinit var upgradePriceLabel: VisLabel

    init {
        tabbedBar.tabs[0].content = StockTable(tabbedBar.tabs[0].tabName, assets, playerData, modsData)
        tabbedBar.tabs[1].content = StockTable(tabbedBar.tabs[1].tabName, assets, playerData, modsData)
        tabbedBar.makeActive(tabbedBar.tabs[0])

        rootTable = table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                right().pad(12f)
                background = bgDrawable
                coinsLabel = label("$coins", "mod-name")
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
                    sellPriceLabel = label("0", "mod-name").cell(padTop = 12f)
                }
                table {
                    pad(0f, 12f, 0f, 12f)
                    add(bigMod)
                    row()
                    table {
                        background = bgDrawable
                        scrollPane(
                                table {
                                    pad(12f)
                                    modName = label("", "mod-name")
                                    row()
                                    modSpecs = label("").apply {
                                        setWrap(true)
                                        setAlignment(Align.center)
                                    }//.cell(width = 126f) // width may be any value
                                }
                        ).cell(width = 150f, height = 78f)
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
                    upgradePriceLabel = label("0", "mod-name").cell(padTop = 12f)
                }
            }
            row()
            container(tabbedBar)
        }
        stage += rootTable
        stage += carriage.apply { setPosition(-height, -width) }
        stage += applyBtn
        stage.addListener(makeStageListener())

        bigMod.setMod(null)
    }

    fun sellMod(mod: Mod) {
        val stockTable = tabbedBar.content.actor as StockTable
        stockTable.subtractMod(mod)
        if (mod.quantity == 0) deselect()

        val income = sellPrices[mod.level-1]
        coins += income
        playerData.get("coins").set(coins.toLong(), null) // TODO move to save function
        updateUI()
    }

    fun upMod(mod: Mod) {
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
            stockTable.addMod(Mod(mod.index, mod.name, mod.level + 1, 1, mod.effects))
            mod.quantity--
            deselect()
        }
        stockTable.updateLabels()
        updateUI()
    }

    override fun updateUI() {
        coinsLabel.setText(coins)
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

    override fun processEmptyMod(emptyMod: EmptyMod) {
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
        private val bg = Image(bgDrawable).apply { setFillParent(true) }
        private val icon: Image
        private val levelBars: Table

        init {
            setSize(150f, 150f)

            icon = Image()
            levelBars = table {
                bottom().left()
                pad(5f)
                defaults().pad(2f)
                for (i in 1..10) {
                    image(bgDrawable) {it.size(10f)}
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
                modName.setText("<${modIcon.mod.name}>")
                modIcon.mod.effects?.forEach { (key, value) ->
                    // TODO add multiple effects support
                    modSpecs.setText("$key: x$value")
                }
                sellPriceLabel.setText("${sellPrices[modIcon.mod.level-1]}")
                upgradePriceLabel.setText("${upgradePrices[modIcon.mod.level-1]}")
            } else {
                bg.drawable = bgDrawable
                icon.drawable = null
                levelBars.isVisible = false
                modName.setText("")
                modSpecs.setText("")
                sellPriceLabel.setText("")
                upgradePriceLabel.setText("")
            }
        }
    }
}