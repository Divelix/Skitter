package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.Data
import com.divelix.skitter.components.B2dBodyComponent
import com.divelix.skitter.components.CameraComponent
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.collections.*

class LevelManager(val entityBuilder: EntityBuilder, val playerEntity: Entity, val cameraEntity: Entity) {
    companion object {
        var enemiesCount = 0
    }
    var level = 0
    private val levelEntities = Array<Entity>()
    val cmBody = mapperFor<B2dBodyComponent>()
    val cmCamera = mapperFor<CameraComponent>()
    val battlegroundSizes = gdxArrayOf(
            Vector2(8f, 10f),
            Vector2(10f, 20f),
            Vector2(15f, 30f)
    )
    val doorPos = Vector2()

    fun update() {
        if (enemiesCount == 0)
            levelEntities + entityBuilder.createDoor(doorPos.x, doorPos.y)
    }

    fun goToNextLevel() {
        level++
        if (level > 1) destroyLevel() // TODO overloads program
        buildLevel(level)
        println("Level $level is ready")
    }

    fun makeEnemies(levelSize: Vector2) {
        entityBuilder.createAgent(4f, 6f)
//        entityBuilder.createLover(-5f, -5f, playerEntity)
//        entityBuilder.createSniper(5f, 25f, playerEntity)
    }

    fun makeBattleground(x: Float, y: Float, width: Float, height: Float) {
        levelEntities + entityBuilder.createBg(x + width / 2f, y + height / 2f, width, height)
        levelEntities + entityBuilder.createWall(Vector2(x, y), Vector2(x, y + height))
        levelEntities + entityBuilder.createWall(Vector2(x, y + height), Vector2(x + width, y + height))
        levelEntities + entityBuilder.createWall(Vector2(x + width, y + height), Vector2(x + width, y))
        levelEntities + entityBuilder.createWall(Vector2(x + width, y), Vector2(x, y))
        doorPos.set(x + width / 2f, y + height - 0.5f)
    }

    fun destroyLevel() {
        levelEntities.forEach {
            if (it.has(cmBody)) entityBuilder.world.destroyBody(cmBody.get(it).body)
            entityBuilder.engine.removeEntity(it)
        }
    }

    fun resetPlayerTo(x: Float, y: Float) {
        cmBody.get(playerEntity).body.setTransform(x, y, 0f)
        cmCamera.get(cameraEntity).needCenter = true
        Data.dirVec.set(0f, 1f)
    }

    fun buildLevel(level: Int) {
        val levelSize = battlegroundSizes[level - 1]
        makeBattleground(0f, 0f, levelSize.x, levelSize.y)
        resetPlayerTo(levelSize.x / 2f, 1f)
        makeEnemies(levelSize)
    }
}