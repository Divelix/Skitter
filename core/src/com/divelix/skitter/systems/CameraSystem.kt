package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.Constants
import com.divelix.skitter.components.*
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class CameraSystem: IteratingSystem(allOf(CameraComponent::class).get()) {
    private val cmCamera = mapperFor<CameraComponent>()
    private val cmBind = mapperFor<BindComponent>()
    private val cmBody = mapperFor<B2dBodyComponent>()
    private val camPos = Vector2()
    private val bodPos = Vector2()
    private val difVec = Vector2()
    private val radVec = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val cameraCmp = cmCamera.get(entity)
        val bindCmp = cmBind.get(entity)
        bodPos.set(cmBody.get(bindCmp.entity).body.position)
        difVec.set(bodPos).sub(camPos)
        if (difVec.len2() > Constants.CAMERA_RADIUS_2) {
            radVec.set(Constants.CAMERA_RADIUS, 0f).rotate(difVec.angle())
            camPos.set(bodPos).sub(radVec)
        }
        cameraCmp.camera.position.set(camPos, 0f)
        cameraCmp.camera.update()
    }
}