package com.divelix.skitter.ui.hud

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Container
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
import ktx.scene2d.*
import ktx.style.get

class GameOverWindow(game: Main, title: String = "GameOver"): ModalWindow(title) {
    val content: Container<*>

    init {
        content = container {
            debug()
        }.cell(grow = true)
        row()
        table {
            defaults().align(Align.center).size(130f, 50f)
            imageButton(Constants.STYLE_EXIT_BTN)
                .onTouchDown {
                    LevelManager.isNextLvlRequired = true
                    GameEngine.slowRate = Constants.DEFAULT_SLOW_RATE
                    game.screen = ScrollMenuScreen(game)
                }
            imageButton(Constants.STYLE_RESTART_BTN)
                .onTouchDown {
                    LevelManager.isRestartNeeded = true
                    this@GameOverWindow.hide()
                    GameEngine.isPaused = false
                    Data.matchHistory.clear()
                }
        }
    }

    override fun update() {
        content.actor = table {
            var score = 0
            scrollPane {
                table {
                    debugAll()
                    for ((enemy, enemyCount) in Data.matchHistory) {
                        val imgName = RegionBinder.chooseEnemyRegionName(enemy)
                        val region = Scene2DSkin.defaultSkin.get<TextureRegion>(imgName)
                        image(region).cell(width = 50f, height = 50f, pad = 10f)
                        scaledLabel("x$enemyCount", scale = 0.2f)
                        val scorePoints = enemyCount!! * 100
                        score += scorePoints
                        scaledLabel(
                            "$scorePoints",
                            scale = 0.2f
                        ).apply { setAlignment(Align.center) }
                            .cell(width = 100f)
                        row()
                    }
                }
            }
            row()
            scaledLabel("Score: $score", scale = .25f).cell(colspan = 3)
        }
    }
}