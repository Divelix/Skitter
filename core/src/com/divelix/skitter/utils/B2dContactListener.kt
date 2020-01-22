package com.divelix.skitter.utils

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.*
import com.divelix.skitter.components.*

class B2dContactListener : ContactListener {
    private val cmCollision = ComponentMapper.getFor(CollisionComponent::class.java)

    override fun beginContact(contact: Contact) {
        val bodyA = contact.fixtureA.body // A is a body that was created earlier
        val bodyB = contact.fixtureB.body
        val entityA = bodyA.userData as Entity
        val entityB = bodyB.userData as Entity
        val cCmpA = cmCollision.get(entityA)
        val cCmpB = cmCollision.get(entityB)
        cCmpA.collisionEntity = entityB
        cCmpB.collisionEntity = entityA
        cCmpA.isBeginContact = true
        cCmpB.isBeginContact = true
        cCmpA.collidedCategoryBits = contact.fixtureB.filterData.categoryBits
        cCmpB.collidedCategoryBits = contact.fixtureA.filterData.categoryBits
    }

    override fun endContact(contact: Contact) {
        val bodyA = contact.fixtureA.body // A is a body that was created earlier
        val bodyB = contact.fixtureB.body
        val entityA = bodyA.userData as Entity
        val entityB = bodyB.userData as Entity
        val cCmpA = cmCollision.get(entityA) ?: return
        val cCmpB = cmCollision.get(entityB) ?: return // :? return - filter bullets (cause NPE as they die on beginContact())
        cCmpA.collisionEntity = entityB
        cCmpB.collisionEntity = entityA
        cCmpA.isBeginContact = false
        cCmpB.isBeginContact = false
        cCmpA.collidedCategoryBits = contact.fixtureB.filterData.categoryBits
        cCmpB.collidedCategoryBits = contact.fixtureA.filterData.categoryBits
    }
    override fun preSolve(contact: Contact, oldManifold: Manifold) {}
    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}

}
// groupIndex:
// 0 -> categoryBits, maskBits
// !0 and !same -> categoryBits, maskBits
// - (same) -> won'stockTable collide no matter what
// + (same) -> will collide no matter what