package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.Data
import com.divelix.skitter.components.B2dBodyComponent
import ktx.ashley.has
import ktx.ashley.mapperFor

class LevelGenerator(val entityBuilder: EntityBuilder, val playerEntity: Entity) {
    companion object {
        var enemiesCount = 0
    }
    private val levelEntities = Array<Entity>()
    val cmBody = mapperFor<B2dBodyComponent>()
    val battlegroundSizes = Array<Vector2>()

    fun makeLevel() {
        makeBattleground(0f, 0f, 10f, 20f)
        makeEnemies()
    }

    fun goToNextLevel() {
        destroyLevel()
        makeBattleground(0f, 0f, 8f, 8f)
        resetPlayerTo(4f, 2f)
    }

    fun makeEnemies() {
        entityBuilder.createAgent(4f, 6f)
//        entityBuilder.createLover(-5f, -5f, playerEntity)
//        entityBuilder.createSniper(5f, 25f, playerEntity)
    }

    fun makeBattleground(x: Float, y: Float, width: Float, height: Float) {
        levelEntities.add(entityBuilder.createBg(x + width / 2f, y + height / 2f, width, height))
        levelEntities.add(entityBuilder.createWall(Vector2(x, y), Vector2(x, y + height)))
        levelEntities.add(entityBuilder.createWall(Vector2(x, y + height), Vector2(x + width, y + height)))
        levelEntities.add(entityBuilder.createWall(Vector2(x + width, y + height), Vector2(x + width, y)))
        levelEntities.add(entityBuilder.createWall(Vector2(x + width, y), Vector2(x, y)))
        levelEntities.add(entityBuilder.createDoor(x + width / 2f, y + height - 0.5f))
    }

    fun destroyLevel() {
        levelEntities.forEach {
            if (it.has(cmBody)) entityBuilder.world.destroyBody(cmBody.get(it).body)
            entityBuilder.engine.removeEntity(it)
        }
    }

    fun resetPlayerTo(x: Float, y: Float) {
        cmBody.get(playerEntity).body.setTransform(x, y, 0f)
        Data.dirVec.set(0f, 1f)
    }
}