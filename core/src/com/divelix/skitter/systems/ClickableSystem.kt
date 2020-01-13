package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.divelix.skitter.Data
import com.divelix.skitter.components.ClickableComponent
import com.divelix.skitter.components.TransformComponent

class ClickableSystem(val camera: OrthographicCamera): IteratingSystem(Family.all(ClickableComponent::class.java).get()) {
    private val cmClick = ComponentMapper.getFor(ClickableComponent::class.java)
    private val cmTrans = ComponentMapper.getFor(TransformComponent::class.java)

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val clickCmp = cmClick.get(entity)
        val transCmp = cmTrans.get(entity)

        clickCmp.circle.x = transCmp.position.x
        clickCmp.circle.y = transCmp.position.y

        if (Gdx.input.justTouched()) {
            val click = camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
//            if (clickCmp.circle.contains(click.x, click.y)) {
//                println("HIT (${clickCmp.circle.x}; ${clickCmp.circle.y})")
//                Data.dynamicData.aims.add(Vector2(clickCmp.circle.x, clickCmp.circle.y))
//            }
//            Data.dynamicData.aims.add(Vector2(click.x, click.y))
        }
    }
}