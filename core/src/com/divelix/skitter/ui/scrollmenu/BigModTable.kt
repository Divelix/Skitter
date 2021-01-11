package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.Mod
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.tabbedmenu.ModView
import ktx.actors.txt
import ktx.scene2d.*
import ktx.style.get
import ktx.collections.*

class BigModTable(val mods: Array<Mod>): Table(), KTable {
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
    }

    fun setMod(modView: ModView) {
        bigModView.setMod(modView)
        val modAlias = modView.modAlias
        val modData = mods.singleOrNull { it.type == modAlias.type && it.index == modAlias.index }
        if (modData != null) {
            modName.txt = "<${modData.name}>"
            var specString = ""
            modData.effects.forEach { (key, value) ->
                specString += "$key: $value\n"
            }
            modSpecs.txt = specString
        } else {
            println("Mod $modAlias not found in database")
        }
    }

    fun clearMod() {
        bigModView.clearMod()
        modName.txt = ""
        modSpecs.txt = ""
    }
}