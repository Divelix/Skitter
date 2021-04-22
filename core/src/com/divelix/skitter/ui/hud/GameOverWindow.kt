package com.divelix.skitter.ui.hud

import com.badlogic.gdx.utils.Align
import com.divelix.skitter.Main
import com.divelix.skitter.data.Constants
import com.divelix.skitter.gameplay.GameEngine
import com.divelix.skitter.gameplay.LevelManager
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.screens.ScrollMenuScreen
import ktx.actors.onTouchDown
import ktx.scene2d.imageButton
import ktx.scene2d.table

class GameOverWindow(game: Main, title: String = "GameOver"): ModalWindow(title) {
    init {
        table {
            scaledLabel("Game over window body")
        }.cell(grow = true)
        row()
        table {
            debug()
            defaults().size(130f, 50f).pad(10f)
            imageButton(Constants.STYLE_EXIT_BTN)
                    .cell(align = Align.center)
                    .onTouchDown {
                        LevelManager.isNextLvlRequired = true
                        GameEngine.slowRate = Constants.DEFAULT_SLOW_RATE
                        game.screen = ScrollMenuScreen(game)
                    }
            imageButton(Constants.STYLE_RESTART_BTN)
                    .cell(align = Align.center)
                    .onTouchDown {
                        LevelManager.isRestartNeeded = true
                        this@GameOverWindow.hide()
                        GameEngine.isPaused = false
                    }
        }
    }
}