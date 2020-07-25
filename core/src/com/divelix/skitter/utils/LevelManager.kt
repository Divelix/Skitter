package com.divelix.skitter.utils

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.Data
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.CameraComponent
import com.divelix.skitter.components.PlayerComponent
import ktx.ashley.hasNot
import ktx.collections.*

class LevelManager(private val gameEngine: GameEngine) {
    private val hud = gameEngine.hud
    private val engine = gameEngine.engine
    private val entityBuilder = gameEngine.entityBuilder

    var level = 0
    var isDoorAllowed = false
    val doorPos = Vector2()
    val chapter = Chapter("Chapter_1", gdxArrayOf(
            Level(Vector2(8f, 10f), gdxArrayOf()),
            Level(Vector2(15f, 30f), gdxArrayOf(
                EnemyBundle(Enemy.SNIPER, 1)
            )),
            Level(Vector2(15f, 30f), gdxArrayOf(
                    EnemyBundle(Enemy.JUMPER, 1)
            )),
            Level(Vector2(15f, 30f), gdxArrayOf(
                    EnemyBundle(Enemy.SNIPER, 1)
            )),
            Level(Vector2(15f, 30f), gdxArrayOf(
                    EnemyBundle(Enemy.WOMB, 1)
            )),
            Level(Vector2(15f, 30f), gdxArrayOf(
                    EnemyBundle(Enemy.RADIAL, 1)
            ))
        )
    )

    fun update() {
        if (isNextLvlRequired) {
            goToNextLevel()
            isNextLvlRequired = false
            isDoorAllowed = true
        }
        if (isDoorAllowed && enemiesCount == 0) {
            entityBuilder.createDoor(doorPos.x, doorPos.y)
            isDoorAllowed = false
        }
    }

    fun goToNextLevel() {
        level++
        if (level > chapter.levels.size) {
            hud.showVictoryWindow()
            return
        }
        if (level > 1) destroyLevel()
        buildLevel(chapter.levels[level - 1])
    }

    fun buildLevel(level: Level) {
        makeBattleground(0f, 0f, level.size.x, level.size.y)
        resetPlayerTo(level.size.x / 2f, 1f)
//        makeObstacles(level)
        makeEnemies(level)
    }

    fun destroyLevel() {
        engine.entities
                .filter { it.hasNot(PlayerComponent.mapper) && it.hasNot(CameraComponent.mapper) }
                .forEach { engine.removeEntity(it) }
    }

    fun makeEnemies(level: Level) {
        level.enemies.forEach {enemyBundle ->
            repeat(enemyBundle.quantity) {
                when(enemyBundle.enemy) {
                    Enemy.AGENT -> entityBuilder.createAgent(MathUtils.random(level.size.x), MathUtils.random(level.size.y))
                    Enemy.JUMPER -> entityBuilder.createJumper(MathUtils.random(level.size.x), MathUtils.random(level.size.y))
                    Enemy.RADIAL -> entityBuilder.createRadial(MathUtils.random(level.size.x), MathUtils.random(level.size.y))
                    Enemy.WOMB -> entityBuilder.createWomb(MathUtils.random(level.size.x), MathUtils.random(level.size.y))
                    Enemy.SNIPER -> entityBuilder.createSniper(MathUtils.random(level.size.x), MathUtils.random(level.size.y))
                }
            }
        }
//        entityBuilder.createPuddle(MathUtils.random(level.size.x), MathUtils.random(level.size.y), 1f)
    }

    fun makeBattleground(x: Float, y: Float, width: Float, height: Float) {
        entityBuilder.createBg(x + width / 2f, y + height / 2f, width, height)
        entityBuilder.createWall(Vector2(x, y), Vector2(x, y + height))
        entityBuilder.createWall(Vector2(x, y + height), Vector2(x + width, y + height))
        entityBuilder.createWall(Vector2(x + width, y + height), Vector2(x + width, y))
        entityBuilder.createWall(Vector2(x + width, y), Vector2(x, y))
        doorPos.set(x + width / 2f, y + height - 0.5f)
    }

    fun makeObstacles() {
        entityBuilder.createBreakableObstacle(1f, 7f)
        entityBuilder.createBreakableObstacle(3f, 7f)
        entityBuilder.createBreakableObstacle(5f, 7f)
        entityBuilder.createBreakableObstacle(7f, 7f)
        entityBuilder.createBreakableObstacle(9f, 7f)

        entityBuilder.createBreakableObstacle(14f, 15f)
        entityBuilder.createBreakableObstacle(12f, 15f)
        entityBuilder.createBreakableObstacle(10f, 15f)
        entityBuilder.createBreakableObstacle(8f, 15f)
        entityBuilder.createBreakableObstacle(6f, 15f)
    }

    fun resetPlayerTo(x: Float, y: Float) {
        B2dBodyComponent.mapper.get(gameEngine.playerEntity).body.setTransform(x, y, 0f)
        CameraComponent.mapper.get(gameEngine.cameraEntity).needCenter = true
        Data.dirVec.set(0f, 1f)
    }

    companion object {
        var enemiesCount = 0
        var isNextLvlRequired = true
    }
}