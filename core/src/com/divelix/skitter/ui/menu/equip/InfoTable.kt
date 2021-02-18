package com.divelix.skitter.ui.menu.equip

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.data.*
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.utils.AliasBinder
import com.divelix.skitter.utils.RegionBinder
import ktx.actors.onClickEvent
import ktx.actors.txt
import ktx.collections.*
import ktx.scene2d.*
import ktx.style.get

class InfoTable(showEquipWindow: () -> Unit) : Table(), KTable {

    private val equipName: Label
    private val description: Label
    private val equipIcon: Image
    private val specsNames: Label
    private val specsValues: Label

    init {
        pad(Constants.UI_PADDING)

        // Description
        table {
            this@InfoTable.equipName = scaledLabel("Equip name")
            row()
            scrollPane {
                this@InfoTable.description = scaledLabel(Constants.LOREM_IPSUM).apply {
                    wrap = true
                    setAlignment(Align.top)
                }
            }.cell(grow = true)
        }.cell(width = 92f, height = 100f, padRight = Constants.UI_PADDING)

        // Icon
        table {
            touchable = Touchable.enabled
            background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
            this@InfoTable.equipIcon = image(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                    .apply { setScaling(Scaling.fit) }.cell(pad = Constants.UI_PADDING)
            onClickEvent { _ ->
                showEquipWindow()
            }
        }.cell(width = 100f, height = 100f)

        // Stats
        table {
            left()
            this@InfoTable.specsNames = scaledLabel("DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: ")
            this@InfoTable.specsValues = scaledLabel("100\n13\n0.5\n10\nx2.0\n20%")
        }.cell(width = 92f, height = 100f, padLeft = Constants.UI_PADDING)
    }

    fun setInfo(equipAlias: EquipAlias) {
        val equip = AliasBinder.getEquip(equipAlias)

        equipName.txt = equip.name
        description.txt = equip.description
        val regionName = RegionBinder.chooseEquipRegionName(equip.type, equip.index)
        equipIcon.drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<TextureRegion>(regionName()))
        val equipLvlIndex = equipAlias.level - 1
        when (val specs = equip.specs) {
            is ShipSpecs -> {
                var health = specs.health[equipLvlIndex]
                var speed = specs.speed[equipLvlIndex]
                equipAlias.mods.forEach { modAlias ->
                    val modLvlIndex = modAlias.level - 1
                    val mod = AliasBinder.getMod(modAlias)
                    mod.effects.forEach { (effect, values) ->
                        when(effect as ModEffect.ShipModEffect) {
                            is ModEffect.ShipModEffect.HealthBooster -> health += values?.get(modLvlIndex) ?: 1f
                            is ModEffect.ShipModEffect.SpeedBooster -> speed += values?.get(modLvlIndex) ?: 1f
                        }
                    }
                }
                specsNames.txt = "HEALTH: \nSPEED: "
                specsValues.txt = "${health}\n${speed}"
            }
            is GunSpecs -> {
                specsNames.txt = "DAMAGE: \nCAPACITY: \nRELOAD: \nSPEED: \nCRITICAL: \nCHANCE: "
                specsValues.txt = "${specs.damage[equipLvlIndex]}\n${specs.capacity[equipLvlIndex]}\n" +
                        "${specs.reload[equipLvlIndex]}\n${specs.speed[equipLvlIndex]}\n" +
                        "${specs.crit[equipLvlIndex]}\n${specs.chance[equipLvlIndex]}"
            }
        }
    }
}