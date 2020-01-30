package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.*
import com.divelix.skitter.components.*
import ktx.ashley.mapperFor
import java.lang.NullPointerException

class B2dContactListener : ContactListener {
    private val cmCollision = mapperFor<CollisionComponent>()
    private val cmAgent = mapperFor<AgentComponent>()

    override fun beginContact(contact: Contact) {
        val bodyA = contact.fixtureA.body // A is a body that was created earlier
        val bodyB = contact.fixtureB.body
        val entityA = bodyA.userData as Entity
        val entityB = bodyB.userData as Entity
        val typeA = contact.fixtureA.filterData.categoryBits
        val typeB = contact.fixtureB.filterData.categoryBits

        when(typeA) {
            TypeComponent.AGENT -> {
                when(typeB) {
                    TypeComponent.AGENT_SENSOR -> {
                        val agentCmp = cmAgent.get(entityB)
                        val ve = agentCmp.visibleEntities
                        ve.add(entityA)
//                        println(ve)
                    }
                }
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

        when(typeA) {
            TypeComponent.AGENT -> {
                when(typeB) {
                    TypeComponent.AGENT_SENSOR -> {
                        val agentCmp = cmAgent.get(entityB)
                        //a crutch to delete bodies safely
                        val ve = try {agentCmp.visibleEntities} catch (e: NullPointerException) {return}
                        ve.remove(entityA)
                    }
                }
            }
        }
    }

    private fun processContact(contact: Contact, isBegin: Boolean) {
        val bodyA = contact.fixtureA.body // A is a body that was created earlier
        val bodyB = contact.fixtureB.body
        val entityA = bodyA.userData as Entity
        val entityB = bodyB.userData as Entity
        val cCmpA = cmCollision.get(entityA) ?: return
        val cCmpB = cmCollision.get(entityB) ?: return // return because NPE on bullet death
        cCmpA.collisionEntity = entityB
        cCmpB.collisionEntity = entityA
        cCmpA.collidedCategoryBits = contact.fixtureB.filterData.categoryBits
        cCmpB.collidedCategoryBits = contact.fixtureA.filterData.categoryBits
        cCmpA.isBeginContact = isBegin
        cCmpB.isBeginContact = isBegin
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {}
    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}

}
// groupIndex:
// 0 -> categoryBits, maskBits
// !0 and !same -> categoryBits, maskBits
// - (same) -> won'stockTable collide no matter what
// + (same) -> will collide no matter what