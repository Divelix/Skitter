package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.physics.box2d.*
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.GameEngine
import com.divelix.skitter.Main
import com.divelix.skitter.components.*
import com.divelix.skitter.ui.Hud
import ktx.ashley.*
import java.lang.NullPointerException

class B2dContactListener(
        game: Main,
        private val engine: PooledEngine,
        private val hud: Hud
) : ContactListener {
    private val assets = game.getContext().inject<Assets>()
    private val hitSound = assets.manager.get<Sound>(Constants.HIT_SOUND)

    override fun beginContact(contact: Contact) {
        val isLess = contact.fixtureA.filterData.categoryBits < contact.fixtureB.filterData.categoryBits
        val fixA = if (isLess) contact.fixtureA else contact.fixtureB
        val fixB = if (isLess) contact.fixtureB else contact.fixtureA
        val entityA = fixA.body.userData as Entity
        val entityB = fixB.body.userData as Entity
        val typeA = fixA.filterData.categoryBits
        val typeB = fixB.filterData.categoryBits

        when(typeA) {
            TypeComponent.VISION_SENSOR -> {
                when (typeB) {
                    TypeComponent.ENEMY, TypeComponent.OBSTACLE, TypeComponent.PLAYER -> {
                        GameEngine.cmVision.get(entityA).visibleEntities.add(entityB)
                    }
                }
            }
            TypeComponent.PLAYER_BULLET, TypeComponent.ENEMY_BULLET -> {
                val bulletCmp = GameEngine.cmBullet.get(entityA)
                when(typeB) {
                    TypeComponent.ENEMY, TypeComponent.PLAYER -> bulletHitsTarget(bulletCmp.damage, entityB)
                    TypeComponent.OBSTACLE -> {
                        if (entityB.has(GameEngine.cmHealth)) bulletHitsTarget(bulletCmp.damage, entityB)
                    }
                }
                engine.removeEntity(entityA)// always delete bullet after any collision
            }
            TypeComponent.PLAYER -> {
                when(typeB) {
                    TypeComponent.DOOR -> {
                        LevelManager.isNextLvlRequired = true
                        engine.removeEntity(entityB)
                    }
                }
            }
        }

        when(typeB) {
            TypeComponent.PUDDLE -> {
                when(typeA) {
                    TypeComponent.PLAYER, TypeComponent.ENEMY -> {
                        entityA.add(SlowComponent())
                    }
                }
            }
        }
    }

    override fun endContact(contact: Contact) {
        val isLess = contact.fixtureA.filterData.categoryBits < contact.fixtureB.filterData.categoryBits
        val fixA = if (isLess) contact.fixtureA else contact.fixtureB
        val fixB = if (isLess) contact.fixtureB else contact.fixtureA
        val entityA = fixA.body.userData as Entity
        val entityB = fixB.body.userData as Entity
        val typeA = fixA.filterData.categoryBits
        val typeB = fixB.filterData.categoryBits

        when(typeA) {
            TypeComponent.VISION_SENSOR -> {
                when(typeB) {
                    TypeComponent.ENEMY, TypeComponent.OBSTACLE, TypeComponent.PLAYER -> {
                        val agentCmp = GameEngine.cmVision.get(entityA)
                        val ve = try {agentCmp.visibleEntities} catch (e: NullPointerException) {return}
                        ve.remove(entityB)
                    }
                }
            }
        }

        when(typeB) {
            TypeComponent.PUDDLE -> {
                when(typeA) {
                    TypeComponent.PLAYER, TypeComponent.ENEMY -> {
                        entityA.remove<SlowComponent>()
                    }
                }
            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {}
    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}

    private fun bulletHitsTarget(damage: Float, targetEntity: Entity) {
        val targetHealthCmp = GameEngine.cmHealth.get(targetEntity)
        if (targetHealthCmp.isIntHp) {
            targetHealthCmp.health--
        } else {
            if (targetHealthCmp.health > damage)
                targetHealthCmp.health -= damage
            else
                targetHealthCmp.health = 0f
            hud.damageLabelsProvider.makeDamageLabel(damage, targetEntity)
        }
        hitSound.play()
    }
}
// groupIndex:
// 0 -> categoryBits, maskBits
// !0 and !same -> categoryBits, maskBits
// - (same) -> won't collide no matter what
// + (same) -> will collide no matter what