package com.divelix.skitter.ui.hud

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
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
import ktx.collections.*
import ktx.scene2d.*
import ktx.style.get

class PauseWindow(game: Main, title: String = "Pause") : ModalWindow(title) {
    val content: Container<*>

    init {
        debugAll()
        content = container {}.cell(expand = true)
        row()
        table {
            defaults().align(Align.center).size(100f, 50f)
            imageButton(Constants.STYLE_EXIT_BTN)
                .onTouchDown {
                    LevelManager.isNextLvlRequired = true
                    game.screen = ScrollMenuScreen(game)
                }
            imageButton(Constants.STYLE_RESUME_BTN)
                .onTouchDown {
//                        GameEngine.isPaused = false
                    this@PauseWindow.hide()
                }
            imageButton(Constants.STYLE_RESTART_BTN)
                .onTouchDown {
                    LevelManager.isRestartNeeded = true
                    this@PauseWindow.hide()
                    GameEngine.isPaused = false
                }
        }
    }

    override fun update() {
        content.actor = table {
            setFillParent(true)
            for ((enemy, enemyCount) in Data.matchHistory) {
                val imgName = RegionBinder.chooseEnemyRegionName(enemy)
                val region = Scene2DSkin.defaultSkin.get<TextureRegion>(imgName)
                image(region).cell(width = 50f, height = 50f, pad = 10f)
                scaledLabel("count: $enemyCount", scale = 0.1f)
                row()
            }
        }
    }
}