package com.divelix.skitter.screens

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.ui.EditScreen
import com.divelix.skitter.ui.EmptyMod
import com.divelix.skitter.ui.EquipTable
import com.divelix.skitter.ui.ModIcon
import com.kotcrab.vis.ui.widget.VisTable
import ktx.actors.plusAssign
import ktx.scene2d.container
import ktx.scene2d.scene2d
import ktx.scene2d.vis.visTable

class EquipScreen(game: Main): EditScreen(game) {
    private val rootTable: VisTable

    init {
        tabbedBar.tabs[0].content = EquipTable(Constants.SHIPS_TAB, assets, reader, playerData)
        tabbedBar.tabs[1].content = EquipTable(Constants.GUNS_TAB, assets, reader, playerData)
        tabbedBar.makeActive(tabbedBar.tabs[0])
        rootTable = scene2d.visTable {
            setFillParent(true)
            top()
            defaults().expandX()
            container {
                addActor(tabbedBar)
            }
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

    override fun processEmptyMod(emptyMod: EmptyMod) {
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
        (tabbedBar.content.actor as EquipTable).updateSpecs()
    }

    private fun isDup(modIcon: ModIcon): Boolean {
        val suitTable = (tabbedBar.content.actor as EquipTable).suitTable
        suitTable.children.filter {(it as Container<*>).actor is ModIcon}.forEach {
            val suitModIcon = (it as Container<*>).actor as ModIcon
            if (suitModIcon.mod.index == modIcon.mod.index) return true
        }
        return false
    }

    override fun updatePlayerJson() {
        (tabbedBar.tabs[0].content as EquipTable).updatePlayerData() // update ships
        (tabbedBar.tabs[1].content as EquipTable).updatePlayerData() // update guns
        super.updatePlayerJson()
    }
}