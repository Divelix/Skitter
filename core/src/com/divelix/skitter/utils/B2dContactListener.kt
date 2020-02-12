package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.*
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.Main
import com.divelix.skitter.components.*
import ktx.actors.txt
import ktx.ashley.*
import java.lang.NullPointerException

class B2dContactListener(game: Main, val camera: OrthographicCamera) : ContactListener {
    private val cmAgent = mapperFor<AgentComponent>()
    private val cmHealth = mapperFor<HealthComponent>()
    private val cmBullet = mapperFor<BulletComponent>()
    private val cmDamage = mapperFor<DamageLabelComponent>()
    private val cmTrans = mapperFor<TransformComponent>()

    private val assets = game.getContext().inject<Assets>()
    private val hitSound = assets.manager.get<Sound>(Constants.HIT_SOUND)

    val temp = Vector3()

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
                    TypeComponent.AGENT -> bulletHitsAgent(entityB)
                    TypeComponent.OBSTACLE -> println("wall or rectangle obstacle")
                }
                bulletCmp.isDead = true // always delete bullet after any collision
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

    private fun bulletHitsAgent(agentEntity: Entity) {
        val damage = Data.playerData.gun.damage
        hitSound.play()
        val agentHealthCmp = cmHealth.get(agentEntity)
        if (agentHealthCmp.health > damage)
            agentHealthCmp.health -= damage
        else
            agentHealthCmp.health = 0f

        Data.damageLabelsPool.obtain().run {
            txt = "${damage.toInt()}"
            temp.set(cmTrans.get(agentEntity).position)
            camera.project(temp)
            prevPos.set(temp.x, temp.y)
            setPosition(temp.x, temp.y)
            cmDamage.get(agentEntity).damageLabels.add(this)
            Data.damageLabels.add(this)
            animate()
        }
    }
}
// groupIndex:
// 0 -> categoryBits, maskBits
// !0 and !same -> categoryBits, maskBits
// - (same) -> won'stockTable collide no matter what
// + (same) -> will collide no matter what