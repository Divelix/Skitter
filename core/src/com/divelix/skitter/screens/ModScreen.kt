package com.divelix.skitter.screens

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.*
import com.kotcrab.vis.ui.widget.VisLabel
import ktx.actors.plusAssign
import ktx.log.info
import ktx.vis.table

class ModScreen(game: Main): EditScreen(game) {
    private val bigMod = BigMod(ModIcon(Mod(1001, "HEALTH", 5), assets))
    lateinit var descriptionLabel: VisLabel

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
                                    descriptionLabel = label("dsfsdf dsfs d sdf sd fsdf sfdf sdfsd sdf hsdhsi hu dhsui hduh sduish udh sudh iush ids").apply {
                                        setWrap(true)
                                        setAlignment(Align.left)
                                    }.cell(width = 126f) // width may be any value
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
    }

    override fun processEmptyMod(emptyMod: EmptyMod) {
        info { "EmptyMod was clicked" }
    }

    override fun makeTabbedBar(): TabbedBar {
        tabs.put(Constants.SHIPS_TAB, StockTable(Constants.SHIPS_TAB, assets, reader, playerData))
        tabs.put(Constants.GUNS_TAB, StockTable(Constants.GUNS_TAB, assets, reader, playerData))
        return TabbedBar(tabs, assets)
    }

    override fun updateSpecs() {
        info { "make updateSpecs()" }
    }

    override fun saveToJson() {
        info { "make saveToJson()" }
    }

    inner class BigMod(private val modIcon: ModIcon): Group() {
        private val iconHeight = 75f

        init {
            setSize(150f, 150f)
            val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
            val bgDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(modIcon.bgColor); fill()}))
            val lvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(modIcon.lvlColor); fill()}))
            val noLvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(modIcon.noLvlColor); fill()}))

            val bg = Image(bgDrawable).apply { setFillParent(true) }
            val texture: Texture = assets.manager.get(modIcon.textureName)
            val aspectRatio = texture.width.toFloat() / texture.height.toFloat()
            val icon = Image(texture).apply {
                setSize(iconHeight * aspectRatio, iconHeight)
                setPosition((this@BigMod.width - width) / 2f, (this@BigMod.height - height) / 2f)
            }
            val levelBars = table {
                bottom().left()
                pad(5f)
                defaults().pad(2f)
                for (i in 1..10) {
                    image(if (i <= modIcon.mod.level) lvlDrawable else noLvlDrawable) {it.size(10f)}
                }
            }

            addActor(bg)
            addActor(icon)
            addActor(levelBars)
        }
    }
}