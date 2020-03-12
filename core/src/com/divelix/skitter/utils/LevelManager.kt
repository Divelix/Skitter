package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.Data
import com.divelix.skitter.Main
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.CameraComponent
import com.divelix.skitter.screens.MenuScreen
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.collections.*

class LevelManager(val game: Main, val entityBuilder: EntityBuilder, val playerEntity: Entity, val cameraEntity: Entity) {
    companion object {
        var enemiesCount = 0
        var isNextLvlRequired = true
    }
    var level = 0
    var isDoorAllowed = false
    val levelEntities = Array<Entity>()
    val cmBody = mapperFor<B2dBodyComponent>()
    val cmCamera = mapperFor<CameraComponent>()
    val battlegroundSizes = gdxArrayOf(
            Vector2(8f, 10f),
//            Vector2(10f, 20f),
            Vector2(15f, 30f)
    )
    val doorPos = Vector2()

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
        if (level > 1) destroyLevel()
        if (level > battlegroundSizes.size) {
            game.screen = MenuScreen(game)
            return
        }
        buildLevel(level)
    }

    fun buildLevel(level: Int) {
        val levelSize = battlegroundSizes[level - 1]
        makeBattleground(0f, 0f, levelSize.x, levelSize.y)
        resetPlayerTo(levelSize.x / 2f, 1f)
        if (level > 1) makeObstacles()
        makeEnemies(levelSize)
    }

    fun destroyLevel() {
        levelEntities.forEach {
            if (it.has(cmBody)) {
                cmBody.get(it).isDead = true
            } else {
                entityBuilder.engine.removeEntity(it)
            }
        }
        levelEntities.clear()
    }

    fun makeEnemies(levelSize: Vector2) {
        if (level <= 1) return
//        for (i in 0..10) entityBuilder.createJumper(MathUtils.random(levelSize.x), MathUtils.random(levelSize.y))
        entityBuilder.createAgent(4f, 6f)
        entityBuilder.createSniper(5f, 25f, playerEntity)
        entityBuilder.createJumper(MathUtils.random(levelSize.x), MathUtils.random(levelSize.y))
    }

    fun makeBattleground(x: Float, y: Float, width: Float, height: Float) {
        levelEntities + entityBuilder.createBg(x + width / 2f, y + height / 2f, width, height)
        levelEntities + entityBuilder.createWall(Vector2(x, y), Vector2(x, y + height))
        levelEntities + entityBuilder.createWall(Vector2(x, y + height), Vector2(x + width, y + height))
        levelEntities + entityBuilder.createWall(Vector2(x + width, y + height), Vector2(x + width, y))
        levelEntities + entityBuilder.createWall(Vector2(x + width, y), Vector2(x, y))
        doorPos.set(x + width / 2f, y + height - 0.5f)
    }

    fun makeObstacles() {
        levelEntities + entityBuilder.createBreakableObstacle(1f, 7f)
        levelEntities + entityBuilder.createBreakableObstacle(3f, 7f)
        levelEntities + entityBuilder.createBreakableObstacle(5f, 7f)
        levelEntities + entityBuilder.createBreakableObstacle(7f, 7f)
        levelEntities + entityBuilder.createBreakableObstacle(9f, 7f)

        levelEntities + entityBuilder.createBreakableObstacle(14f, 15f)
        levelEntities + entityBuilder.createBreakableObstacle(12f, 15f)
        levelEntities + entityBuilder.createBreakableObstacle(10f, 15f)
        levelEntities + entityBuilder.createBreakableObstacle(8f, 15f)
        levelEntities + entityBuilder.createBreakableObstacle(6f, 15f)
    }

    fun resetPlayerTo(x: Float, y: Float) {
        cmBody.get(playerEntity).body.setTransform(x, y, 0f)
        cmCamera.get(cameraEntity).needCenter = true
        Data.dirVec.set(0f, 1f)
    }
}