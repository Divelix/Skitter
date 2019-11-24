package com.divelix.skitter.utils

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.*
import com.divelix.skitter.components.CollisionComponent
import com.divelix.skitter.components.TypeComponent

class B2dContactListener : ContactListener {
    private val cmCollision = ComponentMapper.getFor(CollisionComponent::class.java)
    private val cmType = ComponentMapper.getFor(TypeComponent::class.java)

    override fun beginContact(contact: Contact) {
        val bodyA = contact.fixtureA.body // A is a body that was created earlier
        val bodyB = contact.fixtureB.body
        if (bodyA.userData is Entity && bodyB.userData is Entity) {
            val entityA = bodyA.userData as Entity
            val entityB = bodyB.userData as Entity
            val cCmpA = cmCollision.get(entityA)
            val cCmpB = cmCollision.get(entityB)
            cCmpA.collisionEntity = entityB
            cCmpB.collisionEntity = entityA
        }
    }

    override fun endContact(contact: Contact) {
        val bodyA = contact.fixtureA.body // A is a body that was created earlier
        val bodyB = contact.fixtureB.body
        if (bodyA.userData is Entity && bodyB.userData is Entity) {
            val entityA = bodyA.userData as Entity
            val entityB = bodyB.userData as Entity
            val tCmpA = cmType.get(entityA)
            val tCmpB = cmType.get(entityB)
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