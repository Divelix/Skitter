package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.physics.box2d.*
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.Main
import com.divelix.skitter.components.*
import ktx.ashley.mapperFor
import java.lang.NullPointerException

class B2dContactListener(game: Main) : ContactListener {
    private val cmCollision = mapperFor<CollisionComponent>()
    private val cmAgent = mapperFor<AgentComponent>()
    private val cmHealth = mapperFor<HealthComponent>()
    private val cmBullet = mapperFor<BulletComponent>()

    private val assets = game.getContext().inject<Assets>()
    private val hitSound = assets.manager.get<Sound>(Constants.HIT_SOUND)

    override fun beginContact(contact: Contact) {
        val bodyA = contact.fixtureA.body // A is a body that was created earlier
        val bodyB = contact.fixtureB.body
        val entityA = bodyA.userData as Entity
        val entityB = bodyB.userData as Entity
        val typeA = contact.fixtureA.filterData.categoryBits
        val typeB = contact.fixtureB.filterData.categoryBits

        println("A is $typeA; B is $typeB")
        when(typeB) {
            TypeComponent.AGENT_SENSOR -> {
                when (typeA) {
                    TypeComponent.AGENT, TypeComponent.OBSTACLE, TypeComponent.PLAYER -> {
                        cmAgent.get(entityB).visibleEntities.add(entityA)
                    }
                }
            }
            TypeComponent.PLAYER -> {
                when (typeA) {
                    TypeComponent.AGENT -> {
                        println("A is AGENT; B is PLAYER")
                    }
                }
            }
            TypeComponent.PLAYER_BULLET -> {
                val bulletCmp = cmBullet.get(entityB)
                if (bulletCmp.isDead) return // do not crush app when multiple collisions happens simultaneously
                when(typeA) {
                    TypeComponent.AGENT -> {
                        hitSound.play()
                        val agentHealthCmp = cmHealth.get(entityA)
                        if (agentHealthCmp.health > Data.playerData.gun.damage)
                            agentHealthCmp.health -= Data.playerData.gun.damage
                        else
                            agentHealthCmp.health = 0f
                    }
                    TypeComponent.OBSTACLE -> println("wall or rectangle obstacle")
                }
                bulletCmp.isDead = true // always delete bullet after any collision
            }
        }

        when(typeA) {
            TypeComponent.PLAYER_BULLET -> {
                val bulletCmp = cmBullet.get(entityA)
                if (bulletCmp.isDead) return // do not crush app when multiple collisions happens simultaneously
                when(typeB) {
                    TypeComponent.OBSTACLE -> println("fckn circle obstacle")
                }
                bulletCmp.isDead = true // always delete bullet after any collision
            }
        }
    }

    override fun endContact(contact: Contact) {
        val bodyA = contact.fixtureA.body // A is a body that was created earlier
        val bodyB = contact.fixtureB.body
        val entityA = bodyA.userData as Entity
        val entityB = bodyB.userData as Entity
        val typeA = contact.fixtureA.filterData.categoryBits
        val typeB = contact.fixtureB.filterData.categoryBits

        when(typeB) {
            TypeComponent.AGENT_SENSOR -> {
                when(typeA) {
                    TypeComponent.AGENT, TypeComponent.OBSTACLE, TypeComponent.PLAYER -> {
                        val agentCmp = cmAgent.get(entityB)
                        val ve = try {agentCmp.visibleEntities} catch (e: NullPointerException) {return}
                        ve.remove(entityA)
                    }
                }
            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {}
    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}
}
// groupIndex:
// 0 -> categoryBits, maskBits
// !0 and !same -> categoryBits, maskBits
// - (same) -> won'stockTable collide no matter what
// + (same) -> will collide no matter what