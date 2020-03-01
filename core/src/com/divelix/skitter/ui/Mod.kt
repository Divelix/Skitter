package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ObjectMap
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.utils.ScaledLabel
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import ktx.vis.table

data class Mod(val index: Int, val name: String, var level: Int, var quantity: Int = 0, val effects: ObjectMap<String, Float>? = null)// TODO add isShip (for icon choosing)

class ModIcon(val mod: Mod, val assets: Assets): Group() {
    private val iconHeight = Constants.MOD_WIDTH - 14f - 20f
    val bgColor = Color(1f, 1f, 0f, 1f)
    val lvlColor = Color(0f, 0f, 0f, 1f)
    val noLvlColor = Color(1f, 1f, 1f, 1f)
    val textureName: String

    val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    val bgDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(bgColor); fill()}))
    val lvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(lvlColor); fill()}))
    val noLvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(noLvlColor); fill()}))

    val quantityLabel: ScaledLabel
    val levelBars: VisTable

    init {
        name = "ModIcon"
        touchable = Touchable.enabled
        setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT)


        val bg = Image(bgDrawable).apply { setFillParent(true) }
        textureName = when (mod.index) {
            1001 -> Constants.MOD_SHIP_HEALTH
            1002 -> Constants.MOD_SHIP_SPEED
            1003 -> Constants.MOD_SHIP_CHUBBER

            2001 -> Constants.MOD_GUN_DAMAGE
            2002 -> Constants.MOD_GUN_CAPACITY
            2003 -> Constants.MOD_GUN_RELOAD
            2004 -> Constants.MOD_GUN_SPEED
            2005 -> Constants.MOD_GUN_CRIT
            2006 -> Constants.MOD_GUN_CHANCE

            else -> Constants.STAR
        }
        val texture: Texture = assets.manager.get(textureName)
        val aspectRatio = texture.width.toFloat() / texture.height.toFloat()
        val icon = Image(texture).apply {
            setSize(iconHeight * aspectRatio, iconHeight)
            setPosition((this@ModIcon.width - width) / 2f, (this@ModIcon.height - height) / 2f)
        }
        val quantityBg = Image(lvlDrawable).apply {
            setSize(14f, 14f)
            setPosition(this@ModIcon.width - width, this@ModIcon.height - height)
            isVisible = mod.quantity > 0
        }
        quantityLabel = ScaledLabel("${mod.quantity}").apply {
            setPosition(quantityBg.x + (quantityBg.width-width)/2f, quantityBg.y + (quantityBg.height-height)/2f)
            isVisible = mod.quantity > 0
        }
        levelBars = table {
            bottom().left()
            pad(2f)
            defaults().pad(1f)
            for (i in 1..10) {
                image(if (i <= mod.level) lvlDrawable else noLvlDrawable) {it.size(4f)}
            }
        }

        addActor(bg.apply { touchable = Touchable.disabled })
        addActor(icon.apply { touchable = Touchable.disabled })
        addActor(quantityBg.apply { touchable = Touchable.disabled })
        addActor(quantityLabel.apply { touchable = Touchable.disabled })
        addActor(levelBars.apply { touchable = Touchable.disabled })
    }

    fun updateLevelBars() {
        for (i in 1..10) {
            (levelBars.children[i - 1] as Image).drawable = if (i <= mod.level) lvlDrawable else noLvlDrawable
        }
    }
}

class EmptyMod(val assets: Assets): Group() {
    init {
        name = "EmptyMod"
        touchable = Touchable.enabled
        setSize(64f, 64f)
        val img = Image(assets.bgDrawable).apply { setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT) }
        addActor(img.apply { touchable = Touchable.disabled })
    }
}