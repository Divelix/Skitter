package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.divelix.skitter.data.PlayerData
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table

class EquipPage(val playerData: PlayerData) : Page() {
    val nameLabel: Label

    init {
        val rootTable = scene2d.table {
            setFillParent(true)
            label("Player name = ")
            nameLabel = label(playerData.name)
        }
        addActor(rootTable)
    }

    override fun update() {
        nameLabel.setText(playerData.name)
    }
}