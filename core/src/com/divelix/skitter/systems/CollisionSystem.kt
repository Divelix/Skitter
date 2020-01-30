package com.divelix.skitter.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.audio.Sound
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.Main
import com.divelix.skitter.components.*
import ktx.ashley.allOf
import ktx.ashley.has
import ktx.ashley.mapperFor

class CollisionSystem(game: Main) : IteratingSystem(allOf(CollisionComponent::class).get()) {
    private val cmCollision = mapperFor<CollisionComponent>()
    private val cmHealth = mapperFor<HealthComponent>()
    private val cmEnemy = mapperFor<EnemyComponent>()
    private val cmBullet = mapperFor<BulletComponent>()
    private val cmDecay = mapperFor<DecayComponent>()
    private val cmSlow = mapperFor<SlowComponent>()
    private val cmAgent = mapperFor<AgentComponent>()
    private val cmBody = mapperFor<B2dBodyComponent>()

    private val assets = game.getContext().inject<Assets>()
    private val hitSound = assets.manager.get<Sound>(Constants.HIT_SOUND)

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val collisionCmp = cmCollision.get(entity)
        val collidedEntity = collisionCmp.collisionEntity ?: return
        if (collisionCmp.isBeginContact) {
            processBeginContact(entity, collidedEntity)
        } else {
            processEndContact(entity, collidedEntity)
        }
        collisionCmp.collisionEntity = null
    }

    private fun processBeginContact(entity1: Entity, entity2: Entity) {
        val type1 = cmCollision.get(entity2).collidedCategoryBits
        val type2 = cmCollision.get(entity1).collidedCategoryBits
        when(type1) {
            TypeComponent.PLAYER -> {
                when (type2) {
                    TypeComponent.AGENT -> {
//                            println("player hit enemy")
                        val playerHealthCmp = cmHealth.get(entity1)
                        val enemyCmp = cmEnemy.get(entity2)
                        playerHealthCmp.health -= enemyCmp.damage
//                            println(playerHealthCmp.health)
                    }
                    TypeComponent.SPAWN -> {
                        println("PLAYER collided SPAWN")
                    }
                }
            }
            TypeComponent.PLAYER_BULLET -> {
                val bulletCmp = cmBullet.get(entity1)
                if (bulletCmp.isDead) return // do not crush app when multiple collisions happens simultaneously
                when (type2) {
                    TypeComponent.AGENT -> {
                        hitSound.play()
                        val enemyHealthCmp = cmHealth.get(entity2)
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
                val bulletCmp = cmBullet.get(entity1)
                if (bulletCmp.isDead) return // do not crush app when multiple collisions happens simultaneously
                when (type2) {
                    TypeComponent.PLAYER -> {
                        hitSound.play()
                        val playerHealthCmp = cmHealth.get(entity2)
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
                when (type2) {
                    TypeComponent.PLAYER, TypeComponent.AGENT -> {
                        if (!entity2.has(cmDecay)) entity2.add(DecayComponent())
                        if (!entity2.has(cmSlow)) entity2.add(SlowComponent())
                    }
                }
            }
            TypeComponent.AGENT_SENSOR -> {
                when (type2) {
                    TypeComponent.PLAYER, TypeComponent.AGENT, TypeComponent.OBSTACLE -> {
                        val visibleEntities = cmAgent.get(entity1).visibleEntities
                        visibleEntities.add(entity2)
                        print("add - ")
                        for (e in visibleEntities) {
                            val shape = cmBody.get(e).body.fixtureList[0].shape.type
                            print("$shape ")
                        }
                        print("\n")
//                        cmBody.get(entity1).body.applyForceToCenter(0f, 100f, true)
                    }
                }
            }
            TypeComponent.AGENT -> println("enemy!!!")
        }
    }

    private fun processEndContact (entity1: Entity, entity2: Entity) {
        val type1 = cmCollision.get(entity2).collidedCategoryBits
        val type2 = cmCollision.get(entity1).collidedCategoryBits
        when (type1) {
            TypeComponent.AGENT_SENSOR -> {
                when (type2) {
                    TypeComponent.PLAYER, TypeComponent.AGENT, TypeComponent.OBSTACLE -> {
                        val visibleEntities = cmAgent.get(entity1).visibleEntities
                        visibleEntities.remove(entity2)
                        print("sub - ")
                        for (e in visibleEntities) {
                            val shape = cmBody.get(e).body.fixtureList[0].shape.type
                            print("$shape ")
                        }
                        print("\n")
                    }
                }
            }
        }
    }
}