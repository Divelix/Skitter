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
        val fa = contact.fixtureA // A is a body that was created earlier
        val fb = contact.fixtureB
//        println("${fa.body.name} has hit ${fb.body.name}")
        if(fa.body.userData is Entity && fb.body.userData is Entity) {
            val entityA = fa.body.userData as Entity
            val entityB = fb.body.userData as Entity
            val cCmpA = cmCollision.get(entityA)
            val cCmpB = cmCollision.get(entityB)
            cCmpA.collisionEntity = entityB
            cCmpB.collisionEntity = entityA
        }
        return
    }

    var i = 0
    override fun endContact(contact: Contact) {
//        val entityA = contact.fixtureA.body.userData as Entity
//        val entityB = contact.fixtureB.body.userData as Entity
////        val typeA = entityA.getComponent(TypeComponent::class.java).type
////        val typeB = entityB.getComponent(TypeComponent::class.java).type
//        val typeA = cmType.get(entityA).type
//        val typeB = cmType.get(entityB).type
//        if (typeA == TypeComponent.SPAWN || typeB == TypeComponent.SPAWN) {
//            println("end contact ${++i}") // TODO NPE on enemy destroy
//        }
    }
    override fun preSolve(contact: Contact, oldManifold: Manifold) {}
    override fun postSolve(contact: Contact, impulse: ContactImpulse) {}

}
// groupIndex:
// 0 -> categoryBits, maskBits
// !0 and !same -> categoryBits, maskBits
// - (same) -> won'stockTable collide no matter what
// + (same) -> will collide no matter what