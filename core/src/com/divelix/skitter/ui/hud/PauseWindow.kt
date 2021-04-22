package com.divelix.skitter.ui.hud

import com.badlogic.gdx.utils.Align
import com.divelix.skitter.Main
import com.divelix.skitter.data.Constants
import com.divelix.skitter.gameplay.GameEngine
import com.divelix.skitter.gameplay.LevelManager
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.screens.ScrollMenuScreen
import ktx.actors.onTouchDown
import ktx.log.info
import ktx.scene2d.imageButton
import ktx.scene2d.table

class PauseWindow(game: Main, title: String = "Pause"): ModalWindow(title) {
    init {
        table {
            scaledLabel("Pause window body")
        }.cell(grow = true)
        row()
        table {
            debug()
            defaults().size(80f, 50f).pad(10f)
            imageButton(Constants.STYLE_EXIT_BTN)
                    .cell(align = Align.center)
                    .onTouchDown {
                        LevelManager.isNextLvlRequired = true
                        game.screen = ScrollMenuScreen(game)
                    }
            imageButton(Constants.STYLE_RESUME_BTN)
                    .cell(align = Align.center)
                    .onTouchDown {
//                        GameEngine.isPaused = false
                        this@PauseWindow.hide()
                    }
            imageButton(Constants.STYLE_RESTART_BTN)
                    .cell(align = Align.center)
                    .onTouchDown {
                        LevelManager.isRestartNeeded = true
                        this@PauseWindow.hide()
                        GameEngine.isPaused = false
                    }
        }
    }
}