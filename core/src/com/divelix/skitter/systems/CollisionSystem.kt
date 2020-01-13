package com.divelix.skitter.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.audio.Sound
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.Main
import com.divelix.skitter.components.*
import ktx.ashley.has

class CollisionSystem(game: Main) : IteratingSystem(Family.all(CollisionComponent::class.java).get()) {
    private val cmCollision = ComponentMapper.getFor(CollisionComponent::class.java)
    private val cmType = ComponentMapper.getFor(TypeComponent::class.java)
    private val cmHealth = ComponentMapper.getFor(HealthComponent::class.java)
    private val cmEnemy = ComponentMapper.getFor(EnemyComponent::class.java)
    private val cmBullet = ComponentMapper.getFor(BulletComponent::class.java)
    private val cmDecay = ComponentMapper.getFor(DecayComponent::class.java)
    private val cmSlow = ComponentMapper.getFor(SlowComponent::class.java)

    private val assets = game.getContext().inject<Assets>()
    private val hitSound = assets.manager.get<Sound>(Constants.HIT_SOUND)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val collisionCmp = cmCollision.get(entity)
        val typeCmp = cmType.get(entity)

        val collidedEntity = collisionCmp.collisionEntity
        if (collidedEntity != null) {
            val collidedTypeCmp = cmType.get(collidedEntity)

            when(typeCmp.type) {
                TypeComponent.PLAYER -> {
                    when (collidedTypeCmp.type) {
                        TypeComponent.ENEMY -> {
//                            println("player hit enemy")
                            val playerHealthCmp = cmHealth.get(entity)
                            val enemyCmp = cmEnemy.get(collidedEntity)
                            playerHealthCmp.health -= enemyCmp.damage
//                            println(playerHealthCmp.health)
                        }
                        TypeComponent.SPAWN -> {
                            println("PLAYER collided SPAWN")
                        }
                    }
                }
                TypeComponent.PLAYER_BULLET -> {
                    val bulletCmp = cmBullet.get(entity)
                    if (bulletCmp.isDead) return // do not crush app when multiple collisions happens simultaneously
                    when (collidedTypeCmp.type) {
                        TypeComponent.ENEMY -> {
                            hitSound.play()
                            val enemyHealthCmp = cmHealth.get(collidedEntity)
                            if (enemyHealthCmp.health > Data.playerData.gun.damage)
                                enemyHealthCmp.health -= Data.playerData.gun.damage
                            else
                                enemyHealthCmp.health = 0f
                        }
                        TypeComponent.OBSTACLE -> {
                            println("OBSTACLE")
                        }
                    }
                    bulletCmp.isDead = true // always delete bullet after any collision
                }
                TypeComponent.ENEMY_BULLET -> {
                    val bulletCmp = cmBullet.get(entity)
                    if (bulletCmp.isDead) return // do not crush app when multiple collisions happens simultaneously
                    when (collidedTypeCmp.type) {
                        TypeComponent.PLAYER -> {
                            hitSound.play()
                            val playerHealthCmp = cmHealth.get(collidedEntity)
                            if (playerHealthCmp.health > 10f)
                                playerHealthCmp.health -= 10f
                            else
                                playerHealthCmp.health = 0f
                        }
                        TypeComponent.OBSTACLE -> {
                            println("Sniper hit OBSTACLE!!!")
                        }
                    }
                    bulletCmp.isDead = true // always delete bullet after any collision
                }
                TypeComponent.PUDDLE -> {
                    when (collidedTypeCmp.type) {
                        TypeComponent.PLAYER, TypeComponent.ENEMY -> {
                            if (!collidedEntity.has(cmDecay)) collidedEntity.add(DecayComponent())
                            if (!collidedEntity.has(cmSlow)) collidedEntity.add(SlowComponent())
                        }
                    }
                }
            }
            collisionCmp.collisionEntity = null // collision handled reset component
        }
    }
}