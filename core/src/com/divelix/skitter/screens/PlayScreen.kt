package com.divelix.skitter.screens

import com.badlogic.gdx.*
import com.divelix.skitter.*
import com.divelix.skitter.data.ActivePlayerData
import com.divelix.skitter.data.Data
import com.divelix.skitter.gameplay.GameEngine
import com.divelix.skitter.gameplay.components.EnemyComponent
import com.divelix.skitter.gameplay.components.HealthComponent
import com.divelix.skitter.gameplay.LevelManager
import ktx.app.KtxScreen
import ktx.ashley.get
import ktx.ashley.has
import ktx.log.debug
import ktx.log.info

class PlayScreen(val game: Main, val activePlayerData: ActivePlayerData): KtxScreen {
    private val gameEngine by lazy { GameEngine(activePlayerData, game) }
    private val levelManager by lazy { LevelManager(gameEngine) }

    init {
        Data.renderTime = 0f
        Data.physicsTime = 0f
        Data.score = 0
        Data.matchHistory.clear()
        LevelManager.enemiesCount = 0

        val keysHandler = object: InputAdapter() {
            override fun keyUp(keycode: Int): Boolean {
                when(keycode) {
//                    Input.Keys.BACK, Input.Keys.ESCAPE  -> game.screen = MenuScreen(game)
                    Input.Keys.SPACE -> GameEngine.isPaused = !GameEngine.isPaused
                    Input.Keys.N -> levelManager.goToNextLevel()
                    Input.Keys.D -> gameEngine.engine.entities
                            .filter { it.has(EnemyComponent.mapper) }
                            .forEach {
                                val targetHealthCmp = it[HealthComponent.mapper]
                                require(targetHealthCmp != null) {"Null HealthComponent"}
                                targetHealthCmp.currentHealth = 0f
                            }
                    Input.Keys.I -> gameEngine.engine.entities
                            .filter { it.has(EnemyComponent.mapper) }
                            .forEach {
                                val targetHealthCmp = it[HealthComponent.mapper]
                                require(targetHealthCmp != null) {"Null HealthComponent"}
                                debug(TAG) { targetHealthCmp.currentHealth.toString() }
                                println(targetHealthCmp.currentHealth.toString())
                            }
                    Input.Keys.M -> println(Data.matchHistory)
                }
                return false
            }
        }
        val multiplexer = InputMultiplexer(keysHandler, gameEngine.hud.hudStage, gameEngine.hud.playerCtrl)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun render(delta: Float) {
        levelManager.update()
        gameEngine.update(delta)
    }

    override fun pause() {
        info(TAG) { "pause()" }
        GameEngine.isPaused = true
    }

    override fun resume() {
        info(TAG) { "resume()" }
        GameEngine.isPaused = false
    }

    override fun resize(width: Int, height: Int) {
        info(TAG) { "resize()" }
        gameEngine.hud.resize(width, height)
    }

    override fun hide() {
        info(TAG) { "hide()" }
    }

    override fun dispose() {
        info(TAG) { "dispose()" }
        gameEngine.hud.dispose()
        gameEngine.engine.clearPools()
    }

    companion object {
        val TAG = PlayScreen::class.simpleName!!
    }
}
