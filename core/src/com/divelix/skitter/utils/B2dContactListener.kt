package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.Main
import com.divelix.skitter.components.*
import com.divelix.skitter.screens.MenuScreen
import com.divelix.skitter.ui.Hud
import ktx.ashley.*
import java.lang.NullPointerException

class B2dContactListener(val game: Main, val hud: Hud, val levelManager: LevelManager, val entityBuilder: EntityBuilder) : ContactListener {
    private val cmAgent = mapperFor<AgentComponent>()
    private val cmHealth = mapperFor<HealthComponent>()
    private val cmBullet = mapperFor<BulletComponent>()
    private val cmBody = mapperFor<B2dBodyComponent>()

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
            TypeComponent.AGENT_SENSOR -> {
                when (typeB) {
                    TypeComponent.AGENT, TypeComponent.OBSTACLE, TypeComponent.PLAYER -> {
                        cmAgent.get(entityA).visibleEntities.add(entityB)
                    }
                }
            }
            TypeComponent.PLAYER_BULLET -> {
                val bulletCmp = cmBullet.get(entityA)
                if (bulletCmp.isDead) return // do not crush app when multiple collisions happens simultaneously
                when(typeB) {
                    TypeComponent.AGENT -> bulletHitsTarget(entityB)
                    TypeComponent.OBSTACLE -> {
                        if (entityB.has(cmHealth)) bulletHitsTarget(entityB)
                    }
                }
                bulletCmp.isDead = true // always delete bullet after any collision
            }
            TypeComponent.PLAYER -> {
                when(typeB) {
                    TypeComponent.DOOR -> {
                        cmBody.get(entityB).isDead = true
//                        levelManager.goToNextLevel()
//                        game.screen = MenuScreen(game)
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
            TypeComponent.AGENT_SENSOR -> {
                when(typeB) {
                    TypeComponent.AGENT, TypeComponent.OBSTACLE, TypeComponent.PLAYER -> {
                        val agentCmp = cmAgent.get(entityA)
                        val ve = try {agentCmp.visibleEntities} catch (e: NullPointerException) {return}
                        ve.remove(entityB)
                    }
                }
            }
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {}
    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}

    private fun bulletHitsTarget(targetEntity: Entity) {
        val damage = Data.playerData.gun.damage
        hitSound.play()
        val targetHealthCmp = cmHealth.get(targetEntity)
        if (targetHealthCmp.health > damage)
            targetHealthCmp.health -= damage
        else
            targetHealthCmp.health = 0f

        hud.makeDamageLabel(damage, targetEntity)
    }
}
// groupIndex:
// 0 -> categoryBits, maskBits
// !0 and !same -> categoryBits, maskBits
// - (same) -> won'stockTable collide no matter what
// + (same) -> will collide no matter what