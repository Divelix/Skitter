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
import com.divelix.skitter.utils.RegionBinder
import ktx.actors.onTouchDown
import ktx.collections.component1
import ktx.collections.component2
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.imageButton
import ktx.scene2d.table
import ktx.style.get

class GameOverWindow(game: Main, title: String = "GameOver"): ModalWindow(title) {
    init {
//        controlsTable.apply {
//            debug()
//            defaults().size(130f, 50f).pad(10f)
//            imageButton(Constants.STYLE_EXIT_BTN)
//                .cell(align = Align.center)
//                .onTouchDown {
//                    LevelManager.isNextLvlRequired = true
//                    GameEngine.slowRate = Constants.DEFAULT_SLOW_RATE
//                    game.screen = ScrollMenuScreen(game)
//                }
//            imageButton(Constants.STYLE_RESTART_BTN)
//                .cell(align = Align.center)
//                .onTouchDown {
//                    LevelManager.isRestartNeeded = true
//                    this@GameOverWindow.hide()
//                    GameEngine.isPaused = false
//                }
//        }
    }

    override fun update() {
//        contentTable.clear()
//        for ((enemy, enemyCount) in Data.matchHistory) {
//            contentTable.add(Image(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionBinder.chooseEnemyRegionName(enemy))))
//            contentTable.add(ScaledLabel("count: $enemyCount"))
//            contentTable.row()
//            row()
//        }
    }
}