package com.divelix.skitter.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.divelix.skitter.data.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.legacy.EditScreen
import com.divelix.skitter.ui.legacy.EmptyModIcon
import com.divelix.skitter.ui.legacy.EquipTableOld
import com.divelix.skitter.ui.legacy.ModIcon
import ktx.actors.plusAssign
import ktx.scene2d.scene2d
import ktx.scene2d.table

class EquipScreen(game: Main): EditScreen(game) {
    private val rootTable: Table

    init {
        tabbedBar.tabs[0].content = EquipTableOld(Constants.SHIPS_TAB, assets, reader, playerData)
        tabbedBar.tabs[1].content = EquipTableOld(Constants.GUNS_TAB, assets, reader, playerData)
        tabbedBar.makeActive(tabbedBar.tabs[0])

        rootTable = scene2d.table {
            setFillParent(true)
            top()
            add(tabbedBar)
        }

        stage += rootTable
        stage += carriage.apply { setPosition(-height, -width) }
        stage += applyBtn
        stage.addListener(makeStageListener())
    }

    override fun processModIcon(modIcon: ModIcon) {
        val container = (modIcon.parent as Container<*>)
        val offset = Vector2(-carriageBorderWidth, -carriageBorderWidth)
        val carriagePos = container.localToStageCoordinates(offset)
        when (activeMod) {
            null -> {// select this mod
                activeMod = modIcon
                activeModContainer = container
                carriage.setPosition(carriagePos.x, carriagePos.y)
            }
            modIcon -> {// deselect this mod
                deselect()
            }
            else -> {
                val isSameIndex = modIcon.mod.index == activeMod!!.mod.index
                val isSameTable = modIcon.parent.parent.name == activeMod!!.parent.parent.name
                val isActiveInSuit =  activeMod!!.parent.parent.name == "SuitTable"

                if(isSameTable || isSameIndex || isActiveInSuit && !isDup(modIcon) || !isActiveInSuit && !isDup(activeMod!!)) {
                    // switch mods
                    container.actor = activeMod
                    activeModContainer!!.actor = modIcon
                    activeModContainer = container
                    carriage.setPosition(carriagePos.x, carriagePos.y)
                    updateUI()
                } else {
                    println("Duplicates are not allowed")
                }
            }
        }
    }

    override fun processEmptyMod(emptyMod: EmptyModIcon) {
        if (activeMod == null) return
        val container = (emptyMod.parent as Container<*>)

        val isSameTable = emptyMod.parent.parent.name == activeMod!!.parent.parent.name
        val isActiveInSuit =  activeMod!!.parent.parent.name == "SuitTable"
        val isEmptyInStock =  emptyMod.parent.parent.name == "StockTable"

        if (isEmptyInStock || isSameTable || !isActiveInSuit && !isDup(activeMod!!)) {
            carriage.setPosition(-carriage.width, -carriage.height)
            container.actor = activeMod
            activeModContainer!!.actor = emptyMod
            activeMod = null
            activeModContainer = null
            updateUI()
        } else {
            println("duplicates are forbidden")
        }
    }

    override fun updateUI() {
        (tabbedBar.content.actor as EquipTableOld).updateSpecs()
    }

    private fun isDup(modIcon: ModIcon): Boolean {
        val suitTable = (tabbedBar.content.actor as EquipTableOld).suitTable
        suitTable.children.filter {(it as Container<*>).actor is ModIcon }.forEach {
            val suitModIcon = (it as Container<*>).actor as ModIcon
            if (suitModIcon.mod.index == modIcon.mod.index) return true
        }
        return false
    }

    override fun updatePlayerJson() {
        (tabbedBar.tabs[0].content as EquipTableOld).updatePlayerData() // update ships
        (tabbedBar.tabs[1].content as EquipTableOld).updatePlayerData() // update guns
        super.updatePlayerJson()
    }
}