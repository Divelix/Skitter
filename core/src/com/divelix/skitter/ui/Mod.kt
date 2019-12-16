package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.kotcrab.vis.ui.widget.VisLabel
import ktx.vis.table

data class Mod(val index: Int, val name: String, var level: Int, var quantity: Int = 0)// TODO add isShip (for icon choosing)

class ModIcon(val mod: Mod, val assets: Assets): Group() {
    private val iconSize = Constants.MOD_WIDTH / 2f
    private val bgColor = Color(1f, 1f, 0f, 1f)
    private val lvlColor = Color(0f, 0f, 0f, 1f)
    private val noLvlColor = Color(1f, 1f, 1f, 1f)

    val quantityLabel: VisLabel

    init {
        name = "ModIcon"
        touchable = Touchable.enabled
        setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT)

        val pixel = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        val bgDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(bgColor); fill()}))
        val lvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(lvlColor); fill()}))
        val noLvlDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(noLvlColor); fill()}))

        val bg = Image(bgDrawable).apply { setFillParent(true) }
        val textureName = when (mod.index) {
            1 -> Constants.MOD_ICON_DAMAGE
            2 -> Constants.MOD_ICON_RELOAD
            3 -> Constants.MOD_ICON_CAPACITY
            else -> Constants.STAR
        }
        val texture: Texture = assets.manager.get(textureName)
        val icon = Image(texture).apply {
            setSize(iconSize, iconSize)
            setPosition((this@ModIcon.width - width) / 2f, (this@ModIcon.height - height) / 2f)
        }
        val quantityBg = Image(lvlDrawable).apply {
            setSize(14f, 14f)
            setPosition(this@ModIcon.width - width, this@ModIcon.height - height)
            isVisible = mod.quantity > 0
        }
        quantityLabel = VisLabel("${mod.quantity}").apply {
            setPosition(quantityBg.x + (quantityBg.width-width)/2f, quantityBg.y + (quantityBg.height-height)/2f)
            isVisible = mod.quantity > 0
        }
        val levelBars = table {
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
}

class EmptyMod: Group() {
    private val bgColor = Color(0f, 0f, 0f, 0.3f)

    init {
        name = "EmptyMod"
        touchable = Touchable.enabled
        setSize(64f, 64f)
        val pixel = Pixmap(1, 1, Pixmap.Format.Alpha)
        val bgDrawable = TextureRegionDrawable(Texture(pixel.apply {setColor(bgColor); fill()}))
        val img = Image(bgDrawable).apply { setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT) }
        addActor(img.apply { touchable = Touchable.disabled })
    }
}

// Deprecated Mod appearance
class ModImage(val mod: Mod, val assets: Assets): Group() {
    val texture: Texture
    val levelLabel: VisLabel
    val quantityLabel: VisLabel

    init {
        touchable = Touchable.enabled
        setSize(Constants.MOD_WIDTH, Constants.MOD_HEIGHT)
        texture = when(mod.index) {
            // Gun stockMods
            1 -> assets.manager.get(Constants.MOD_DAMAGE)
            2 -> assets.manager.get(Constants.MOD_RELOAD_SPEED)
            3 -> assets.manager.get(Constants.MOD_BULLET_SPEED)
            4 -> assets.manager.get(Constants.BACKGROUND_IMAGE) //TODO add texture for CRIT_MULT

            5 -> assets.manager.get(Constants.MOD_FIRE_DAMAGE)
            6 -> assets.manager.get(Constants.MOD_COLD_DAMAGE)
            else -> assets.manager.get(Constants.BACKGROUND_IMAGE)
        }
        levelLabel = VisLabel("lvl ${mod.level}", "mod-level").apply {
            touchable = Touchable.disabled
        }
        quantityLabel = VisLabel("${mod.quantity}", "mod-quantity").apply {
            setPosition(this@ModImage.width - width, this@ModImage.height - height)
            touchable = Touchable.disabled
        }

        addActor(Image(texture).apply {
            touchable = Touchable.disabled
            setFillParent(true)
        })
        addActor(levelLabel)
        addActor(quantityLabel)
        addActor(quantityLabel)
    }
}