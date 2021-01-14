package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.RegionName
import com.divelix.skitter.data.ModAlias
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import ktx.actors.onClick
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get

class ModView(val modAlias: ModAlias, selectMod: (ModView) -> Unit): Group() {
    private val iconHeight = Constants.MOD_WIDTH - 14f - 20f
    val textureName: String
    val bgDrawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.YELLOW_PIXEL))
    val lvlDrawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL))
    val noLvlDrawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.WHITE_PIXEL))
    private val quantityLabel: Label
    private val levelBars: Table
    private val carriage: Image

    init {
        touchable = Touchable.enabled
        setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT)


        val bg = Image(bgDrawable).apply { setFillParent(true) }
        textureName = when (modAlias.index) {
            1001 -> RegionName.MOD_SHIP_HEALTH()
            1002 -> RegionName.MOD_SHIP_SPEED()
            1003 -> RegionName.MOD_SHIP_CHUBBER()

            2001 -> RegionName.MOD_GUN_DAMAGE()
            2002 -> RegionName.MOD_GUN_CAPACITY()
            2003 -> RegionName.MOD_GUN_RELOAD()
            2004 -> RegionName.MOD_GUN_SPEED()
            2005 -> RegionName.MOD_GUN_CRIT()
            2006 -> RegionName.MOD_GUN_CHANCE()

            else -> RegionName.STAR()
        }
        carriage = Image(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.CARRIAGE_SHADOW())).apply {
            setFillParent(true)
            isVisible = false
        }
        val textureRegion = Scene2DSkin.defaultSkin.get<TextureRegion>(textureName)
        val aspectRatio = textureRegion.regionWidth.toFloat() / textureRegion.regionHeight.toFloat()
        val icon = Image(textureRegion).apply {
            setSize(iconHeight * aspectRatio, iconHeight)
            setPosition((this@ModView.width - width) / 2f, (this@ModView.height - height) / 2f)
        }
        val quantityBg = Image(lvlDrawable).apply {
            setSize(14f, 14f)
            setPosition(this@ModView.width - width, this@ModView.height - height)
            isVisible = modAlias.quantity > 0
        }
        quantityLabel = scene2d.scaledLabel("${modAlias.quantity}").apply {
            setPosition(quantityBg.x + (quantityBg.width-width)/2f, quantityBg.y + (quantityBg.height-height)/2f)
            isVisible = modAlias.quantity > 0
        }
        levelBars = scene2d.table {
            bottom().left()
            pad(2f)
            defaults().pad(1f)
            for (i in 1..10) {
                image(if (i <= modAlias.level) lvlDrawable else noLvlDrawable) {it.size(4f)}
            }
        }

        addActor(bg.apply { touchable = Touchable.disabled })
        addActor(carriage.apply { touchable = Touchable.disabled })
        addActor(icon.apply { touchable = Touchable.disabled })
        addActor(quantityBg.apply { touchable = Touchable.disabled })
        addActor(quantityLabel.apply { touchable = Touchable.disabled })
        addActor(levelBars.apply { touchable = Touchable.disabled })
        onClick {
            selectMod(this)
        }
    }

    fun activate() {
        carriage.isVisible = true
    }

    fun deactivate() {
        carriage.isVisible = false
    }

    fun updateLevelBars() {
        for (i in 1..10) {
            (levelBars.children[i - 1] as Image).drawable = if (i <= modAlias.level) lvlDrawable else noLvlDrawable
        }
    }
}