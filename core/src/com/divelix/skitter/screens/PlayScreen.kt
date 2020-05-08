package com.divelix.skitter.screens

import com.badlogic.gdx.*
import com.badlogic.gdx.utils.JsonReader
import com.divelix.skitter.*
import com.divelix.skitter.utils.LevelManager
import ktx.app.KtxScreen
import ktx.assets.toLocalFile
import ktx.log.info

class PlayScreen(val game: Main): KtxScreen {
    private val gameEngine by lazy { GameEngine(game) }
    private val levelManager by lazy { LevelManager(gameEngine) }

    init {
        Data.renderTime = 0f
        Data.physicsTime = 0f
        Data.score = 0
        LevelManager.enemiesCount = 0

        loadPlayerData()

        val handler = object: InputAdapter() {
            override fun keyUp(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK, Input.Keys.ESCAPE  -> game.screen = MenuScreen(game)
                    Input.Keys.SPACE -> GameEngine.isPaused = !GameEngine.isPaused
                    Input.Keys.N -> levelManager.goToNextLevel()
                }
                return false
            }
        }
        val multiplexer = InputMultiplexer(handler, gameEngine.hud.hudStage, gameEngine.hud.playerCtrl)
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

    private fun loadPlayerData() {
        val playerReader = JsonReader().parse(Constants.PLAYER_FILE.toLocalFile())
        val shipSpecs = playerReader.get("active_ship_specs")
        Data.playerData.ship.health = shipSpecs[0].asFloat()
        health = Data.playerData.ship.health
        Data.playerData.ship.speed = shipSpecs[1].asFloat()
        val gunSpecs = playerReader.get("active_gun_specs")
        Data.playerData.gun.damage = gunSpecs[0].asFloat()
        Data.playerData.gun.capacity = gunSpecs[1].asInt()
        Data.playerData.gun.reloadTime = gunSpecs[2].asFloat()
        Data.playerData.gun.bulletSpeed = gunSpecs[3].asFloat()
        Data.playerData.gun.critMultiplier = gunSpecs[4].asFloat()
        Data.playerData.gun.critChance = gunSpecs[5].asFloat()
        ammo = Data.playerData.gun.capacity
    }

    companion object {
        const val TAG = "PlayScreen"
        var ammo = 0
        var health = 0f
    }
}
