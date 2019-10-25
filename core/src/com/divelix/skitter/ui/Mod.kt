package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.kotcrab.vis.ui.widget.VisLabel

data class Mod(val index: Int, val name: String, val level: Int, var quantity: Int = 1)

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
            4 -> assets.manager.get(Constants.LOADING_IMAGE) //TODO add texture for CRIT_MULT

            5 -> assets.manager.get(Constants.MOD_FIRE_DAMAGE)
            6 -> assets.manager.get(Constants.MOD_COLD_DAMAGE)
            else -> assets.manager.get(Constants.LOADING_IMAGE)
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