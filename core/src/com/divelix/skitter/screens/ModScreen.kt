package com.divelix.skitter.screens

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.*
import com.kotcrab.vis.ui.widget.VisLabel
import ktx.actors.plusAssign
import ktx.log.info
import ktx.vis.table
import ktx.collections.*

class ModScreen(game: Main): EditScreen(game) {
    private val bigMod = BigMod(ModIcon(Mod(1001, "HEALTH", 5), assets))
    lateinit var modName: VisLabel
    lateinit var modSpecs: VisLabel

    init {
        stage += table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                right().pad(12f)
                background = bgDrawable
                label("2500")
            }.cell(fillX = true)
            row()
            table {
                pad(12f)
                image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.SELL_BTN))).cell(width = 76f, height = 76f)
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
                image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.UP_BTN))).cell(width = 76f, height = 76f)
            }
            row()
            container(tabbedBar)
        }
        stage += carriage.apply { setPosition(-height, -width) }
        stage += applyBtn
        stage.addListener(makeStageListener())
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
            }
        }
        updateUI()
    }

    override fun processEmptyMod(emptyMod: EmptyMod) {
        info { "EmptyMod was clicked" }
    }

    override fun makeTabbedBar(): TabbedBar {
        tabs.put(Constants.SHIPS_TAB, StockTable(Constants.SHIPS_TAB, assets, reader, playerData))
        tabs.put(Constants.GUNS_TAB, StockTable(Constants.GUNS_TAB, assets, reader, playerData))
        return TabbedBar(tabs, assets)
    }

    override fun updateUI() {
        info { "updateUI()" }
        bigMod.setMod(activeMod)
    }

    override fun saveToJson() {
        info { "make saveToJson()" }
    }

    inner class BigMod(private val modIcon: ModIcon): Group() {
        private val iconHeight = 75f
        private val bg = Image(modIcon.bgDrawable).apply { setFillParent(true) }
        private val icon: Image
        private val levelBars: Table

        init {
            setSize(150f, 150f)

            val texture: Texture = assets.manager.get(modIcon.textureName)
            val aspectRatio = texture.width.toFloat() / texture.height.toFloat()
            icon = Image(texture).apply {
                setSize(iconHeight * aspectRatio, iconHeight)
                setPosition((this@BigMod.width - width) / 2f, (this@BigMod.height - height) / 2f)
            }
            levelBars = table {
                bottom().left()
                pad(5f)
                defaults().pad(2f)
                for (i in 1..10) {
                    image(if (i <= modIcon.mod.level) modIcon.lvlDrawable else modIcon.noLvlDrawable) {it.size(10f)}
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
            } else {
                bg.drawable = bgDrawable
                icon.drawable = null
                levelBars.isVisible = false
                modName.setText("")
                modSpecs.setText("")
            }
        }
    }
}