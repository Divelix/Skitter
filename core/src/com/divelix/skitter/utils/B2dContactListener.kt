package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.*
import com.divelix.skitter.components.CollisionComponent

class B2dContactListener : ContactListener {

    override fun beginContact(contact: Contact) {
        val fa = contact.fixtureA
        val fb = contact.fixtureB
//        println("${fa.body.type} has hit ${fb.body.type}")
        if(fa.body.userData is Entity && fb.body.userData is Entity) {
            val entityA = fa.body.userData as Entity
            val entityB = fb.body.userData as Entity
            val cCmpA = entityA.getComponent(CollisionComponent::class.java)
            val cCmpB = entityB.getComponent(CollisionComponent::class.java)
            cCmpA.collisionEntity = entityB
            cCmpB.collisionEntity = entityA
        }
        return
    }

    override fun endContact(contact: Contact) {}
    override fun preSolve(contact: Contact, oldManifold: Manifold) {}
    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}

}
// groupIndex:
// 0 -> categoryBits, maskBits
// !0 and !same -> categoryBits, maskBits
// - (same) -> won'stockTable collide no matter what
// + (same) -> will collide no matter what