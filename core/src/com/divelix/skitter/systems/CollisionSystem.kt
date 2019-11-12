package com.divelix.skitter.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.audio.Sound
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.Main
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.GunScreen
import com.divelix.skitter.screens.MenuScreen
import com.divelix.skitter.screens.PlayScreen

class CollisionSystem(val game: Main) : IteratingSystem(Family.all(CollisionComponent::class.java).get()) {
    private val cmCollision = ComponentMapper.getFor(CollisionComponent::class.java)
    private val cmType = ComponentMapper.getFor(TypeComponent::class.java)
    private val playerDamage = Data.playerData.gun.damage
    private val assets = game.getContext().inject<Assets>()
    private val hitSound = assets.manager.get<Sound>(Constants.HIT_SOUND)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        // get player collision component
        val typeCmp = cmType.get(entity)
        val collisionCmp = cmCollision.get(entity)

        val collidedEntity = collisionCmp.collisionEntity
        if (collidedEntity != null) {
            val collidedTypeCmp = collidedEntity.getComponent(TypeComponent::class.java)

            when(typeCmp.type) {
                TypeComponent.PLAYER -> {
                    when (collidedTypeCmp.type) {
                        TypeComponent.ENEMY -> {
                            println("player hit enemy")
                            val enemyCmp = collidedEntity.getComponent(EnemyComponent::class.java)
//                            playerCmp.health -= enemyCmp.damage
                            Data.playerData.ship.health -= enemyCmp.damage
                            println(Data.playerData.ship.health)
                            if (Data.playerData.ship.health <= 0f) {
                                println("Player died")
                                gameOver()
                            }
                            collisionCmp.collisionEntity = null // collision handled reset component
                        }
                    }
                }
                TypeComponent.BULLET -> {
                    val bulletCmp = entity.getComponent(BulletComponent::class.java)
                    if (bulletCmp.isDead) return // do not crush app when multiple collisions happens simultaneously
                    when (collidedTypeCmp.type) {
                        TypeComponent.ENEMY -> {
                            hitSound.play()
                            val enemyCmp = collidedEntity.getComponent(EnemyComponent::class.java)
                            enemyCmp.health -= playerDamage
                            if(enemyCmp.health <= 0) {
                                val collidedBodyCmp = collidedEntity.getComponent(B2dBodyComponent::class.java)
                                collidedBodyCmp.isDead = true
                                Data.enemiesCount--
                                Data.score += 100
                            }
                            collisionCmp.collisionEntity = null
                            bulletCmp.isDead = true
                        }
                        TypeComponent.OBSTACLE -> {
                            println("OBSTACLE")
                            collisionCmp.collisionEntity = null
                            bulletCmp.isDead = true
                        }
                    }
                }
            }
        }
    }

    private fun gameOver() {
        println("------------------------------------")
        println("-------------Game Over--------------")
        println("------------------------------------")
        Data.dynamicData.dirVec.setZero()
        game.screen = MenuScreen(game)
    }

}