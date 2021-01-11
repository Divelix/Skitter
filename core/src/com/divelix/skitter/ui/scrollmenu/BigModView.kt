package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.Mod
import com.divelix.skitter.ui.tabbedmenu.ModView
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.image
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visTable
import ktx.style.get

class BigModView : Group() {
    private val iconHeight = 75f
    private val bg = Image(TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))).apply { setFillParent(true) }
    private val icon: Image
    private val levelBars: Table

    init {
        setSize(150f, 150f)

        icon = Image()
        levelBars = scene2d.table {
            bottom().left()
            pad(5f)
            defaults().pad(2f)
            for (i in 1..10) {
                image(TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))) { it.size(10f) }
            }
        }

        addActor(bg)
        addActor(icon)
        addActor(levelBars)
    }

    fun setMod(modView: ModView) {
        icon.isVisible = true
        levelBars.isVisible = true
        bg.drawable = modView.bgDrawable
        val textureRegion = Scene2DSkin.defaultSkin.get<TextureRegion>(modView.textureName)
        val aspectRatio = textureRegion.regionWidth.toFloat() / textureRegion.regionHeight.toFloat()
        icon.run {
            drawable = TextureRegionDrawable(textureRegion)
            setSize(iconHeight * aspectRatio, iconHeight)
            setPosition((this@BigModView.width - width) / 2f, (this@BigModView.height - height) / 2f)
        }
        for (i in 1..10) {
            if (i <= modView.modAlias.level)
                (levelBars.children[i - 1] as Image).drawable = modView.lvlDrawable
            else
                (levelBars.children[i - 1] as Image).drawable = modView.noLvlDrawable
        }
    }

    fun clearMod() {
        icon.isVisible = false
        levelBars.isVisible = false
        bg.drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
    }
}