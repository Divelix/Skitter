package com.divelix.skitter.ui.menu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.*
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.data.binders.RegionBinder
import ktx.actors.onClick
import ktx.actors.txt
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.image
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.style.get

class ModView(val modAlias: ModAlias, selectMod: (ModView) -> Unit): Group() {
    private val iconHeight = Constants.MOD_SIZE - 14f - 20f
    val textureName: String
    val bgDrawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.YELLOW_PIXEL))
    val lvlDrawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL))
    val noLvlDrawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.WHITE_PIXEL))
    val quantityLabel: Label
    private val levelBars: Table
    private val carriage: Image

    init {
        touchable = Touchable.enabled
        setSize(Constants.MOD_SIZE, Constants.MOD_SIZE)

        val bg = Image(bgDrawable).apply { setFillParent(true) }
        textureName = RegionBinder.chooseModRegionName(modAlias.type, modAlias.index)
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

    fun update() {
        quantityLabel.txt = modAlias.quantity.toString()
        updateLevelBars()
    }

    private fun updateLevelBars() {
        for (i in 1..10) {
            (levelBars.children[i - 1] as Image).drawable = if (i <= modAlias.level) lvlDrawable else noLvlDrawable
        }
    }
}