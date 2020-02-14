package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.divelix.skitter.Constants
import com.divelix.skitter.components.DamageLabelComponent
import com.divelix.skitter.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class DamageLabelSystem(val camera: OrthographicCamera): IteratingSystem(allOf(DamageLabelComponent::class).get()) {
    private val cmDamage = mapperFor<DamageLabelComponent>()
    private val cmTrans = mapperFor<TransformComponent>()

    private val temp = Vector3()
    private val nextPos = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val damageCmp = cmDamage.get(entity)
        val transCmp = cmTrans.get(entity)

        temp.set(transCmp.position)
        camera.project(temp)
        val ratio = Constants.D_WIDTH / Gdx.graphics.width.toFloat()
        temp.scl(ratio)
        nextPos.set(temp.x, temp.y)
        for (label in damageCmp.damageLabels) {
            label.moveTo(nextPos)
            label.ecsTimer -= deltaTime
            if (label.ecsTimer <= 0f) {
                damageCmp.damageLabels.removeValue(label, true)
            }
        }
    }
}