package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.divelix.skitter.Data
import com.divelix.skitter.components.BindComponent
import com.divelix.skitter.components.DamageLabelComponent
import com.divelix.skitter.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor
import java.lang.NullPointerException

class DamageLabelSystem(val camera: OrthographicCamera): IteratingSystem(allOf(DamageLabelComponent::class).get()) {
    private val cmDamage = mapperFor<DamageLabelComponent>()
    private val cmTrans = mapperFor<TransformComponent>()

    private val vec3 = Vector3()
    private val nextPos = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val damageCmp = cmDamage.get(entity)
        val transCmp = cmTrans.get(entity)

        vec3.set(transCmp.position)
        camera.project(vec3)
        nextPos.set(vec3.x, vec3.y)
        for (label in damageCmp.damageLabels) {
            label.moveTo(nextPos)
            label.ecsTimer -= deltaTime
            if (label.ecsTimer <= 0f) {
                damageCmp.damageLabels.removeValue(label, true)
            }
        }
    }
}