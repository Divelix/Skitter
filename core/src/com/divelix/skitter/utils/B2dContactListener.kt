package com.divelix.skitter.utils

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.*
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.components.*
import ktx.ashley.has

class B2dContactListener : ContactListener {
    private val cmCollision = ComponentMapper.getFor(CollisionComponent::class.java)
    private val cmType = ComponentMapper.getFor(TypeComponent::class.java)
    private val cmDecay = ComponentMapper.getFor(DecayComponent::class.java)
    private val cmSlow = ComponentMapper.getFor(SlowComponent::class.java)
    private val cmMove = ComponentMapper.getFor(MoveComponent::class.java)

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

    override fun endContact(contact: Contact) {//TODO seems like a hardcode a little, ponder on it later
        val bodyA = contact.fixtureA.body // A is a body that was created earlier
        val bodyB = contact.fixtureB.body
        if (bodyA.userData is Entity && bodyB.userData is Entity) {
            val entityA = bodyA.userData as Entity
            val entityB = bodyB.userData as Entity
            val tCmpA = cmType.get(entityA)
            val tCmpB = cmType.get(entityB)
            if (tCmpA == null || tCmpB == null) return // kostyl to filter bullets (cause NPE as they die on beginContact())
            if (tCmpA.type == TypeComponent.PUDDLE) {
                if (entityB.has(cmDecay)) entityB.remove(DecayComponent::class.java)
                if (entityB.has(cmSlow)) entityB.remove(SlowComponent::class.java)
                val moveCmp = cmMove.get(entityB)
                when (tCmpB.type) {
                    TypeComponent.PLAYER -> moveCmp.speed = Data.playerData.ship.speed
                    TypeComponent.ENEMY -> moveCmp.speed = Constants.ENEMY_SPEED
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