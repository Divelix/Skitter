package com.divelix.skitter.ui.hud

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.Main
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.Data
import com.divelix.skitter.gameplay.GameEngine
import com.divelix.skitter.gameplay.LevelManager
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.screens.ScrollMenuScreen
import com.divelix.skitter.ui.ScaledLabel
import com.divelix.skitter.utils.RegionBinder.chooseEnemyRegionName
import ktx.actors.onTouchDown
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.imageButton
import ktx.scene2d.table
import ktx.style.get
import ktx.collections.*
import ktx.scene2d.image

class VictoryWindow(game: Main, title: String = "Victory"): ModalWindow(title) {

    init {
//        controlsTable.apply {
//            debug()
//            defaults().size(80f, 50f).pad(10f)
//            imageButton(Constants.STYLE_EXIT_BTN)
//                .cell(align = Align.center)
//                .onTouchDown {
//                    LevelManager.isNextLvlRequired = true
//                    game.screen = ScrollMenuScreen(game)
//                }
//            imageButton(Constants.STYLE_RESTART_BTN)
//                .cell(align = Align.center)
//                .onTouchDown {
//                    LevelManager.isRestartNeeded = true
//                    this@VictoryWindow.hide()
//                    GameEngine.isPaused = false
//                }
//            imageButton(Constants.STYLE_NEXT_BTN)
//                .cell(align = Align.center)
//                .onTouchDown {
//                    // TODO make transition to next chapter
//                    print("Next button clicked")
////                        GameEngine.isPaused = false
////                        this@VictoryWindow.hide()
//                }
//        }
    }

    override fun update() {
//        contentTable.clear()
//        for ((enemy, enemyCount) in Data.matchHistory) {
//            contentTable.add(Image(Scene2DSkin.defaultSkin.get<TextureRegion>(chooseEnemyRegionName(enemy))))
//            contentTable.add(ScaledLabel("count: $enemyCount"))
//            contentTable.row()
//            row()
//        }
    }
}