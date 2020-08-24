package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.divelix.skitter.data.PlayerData
import ktx.actors.onClickEvent
import ktx.scene2d.*

class ModPage(val playerData: PlayerData) : Page() {
    val nameLabel: Label
    lateinit var textFiled: TextField

    init {
        val rootTable = scene2d.table {
            setFillParent(true)
            label("Player name = ")
            nameLabel = label(playerData.name)
            row()
            textButton("update name")
                    .cell(colspan = 2)
                    .onClickEvent { event, actor ->
                        playerData.name = textFiled.text
                        update()
                    }
            row()
            textFiled = textField("New name").cell(colspan = 2)
        }
        addActor(rootTable)
    }

    override fun update() {
        nameLabel.setText(playerData.name)
    }
}