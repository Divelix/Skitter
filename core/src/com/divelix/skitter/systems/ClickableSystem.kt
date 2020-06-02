package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.ClickableComponent
import com.divelix.skitter.components.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get

class ClickableSystem(val camera: OrthographicCamera): IteratingSystem(allOf(ClickableComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val clickCmp = entity[ClickableComponent.mapper]!!
        val transCmp = entity[TransformComponent.mapper]!!

        clickCmp.circle.x = transCmp.position.x
        clickCmp.circle.y = transCmp.position.y

        if (Gdx.input.justTouched()) {
            val click = camera.unproject(Vector3(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
            if (clickCmp.circle.contains(click.x, click.y)) {
                println("HIT (${clickCmp.circle.x}; ${clickCmp.circle.y})")
//                Data.dynamicData.aims.add(Vector2(clickCmp.circle.x, clickCmp.circle.y))
            }
//            Data.dynamicData.aims.add(Vector2(click.x, click.y))
        }
    }
}