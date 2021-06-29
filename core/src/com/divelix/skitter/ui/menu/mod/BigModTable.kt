package com.divelix.skitter.ui.menu.mod

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.ModEffect
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.menu.ModView
import com.divelix.skitter.data.binders.AliasBinder
import ktx.actors.txt
import ktx.scene2d.*
import ktx.style.get
import ktx.collections.*

class BigModTable: Table(), KTable {
    private val bigModView by lazy { BigModView() }

    private val modName: Label
    private val modSpecs: Label

    init {
        pad(Constants.UI_MARGIN)

        // Big mod
        add(bigModView)
        row()

        // Scroll pane with description
        table {
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            scrollPane {
                table {
                    pad(12f)
                    this@BigModTable.modName = scaledLabel("Mod name")
                    row()
                    this@BigModTable.modSpecs = scaledLabel("spec1: 100\nspec2: 50\nspec3: 20\nspec4: 120") {
                        wrap = true
                        setAlignment(Align.center)
                    }
                }
            }.cell(width = 150f, height = 78f)
        }
        clearMod()
    }

    fun setMod(modView: ModView) {
        bigModView.setMod(modView)
        val modAlias = modView.modAlias
        val mod = AliasBinder.getMod(modAlias)
        modName.txt = "<${mod.name}>"
        var specString = ""
        mod.effects.forEach { (key, value) ->
            val effectName = when(key) {
                is ModEffect.ShipModEffect.HealthBooster -> "health"
                is ModEffect.ShipModEffect.SpeedBooster -> "speed"

                is ModEffect.GunModEffect.DamageBooster -> "damage"
                is ModEffect.GunModEffect.ReloadBooster -> "reload"
            }
            specString += "$effectName: ${value?.get(modAlias.level-1) ?: -1}\n"
        }
        modSpecs.txt = specString
    }

    fun clearMod() {
        bigModView.clearMod()
        modName.txt = ""
        modSpecs.txt = ""
    }
}