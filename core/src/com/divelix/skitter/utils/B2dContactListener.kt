package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.*
import com.divelix.skitter.components.*
import ktx.ashley.mapperFor

class B2dContactListener : ContactListener {
    private val cmCollision = mapperFor<CollisionComponent>()

    override fun beginContact(contact: Contact) = processContact(contact, true)

    override fun endContact(contact: Contact) = processContact(contact, false)

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