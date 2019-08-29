package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.components.*

class CameraSystem: IteratingSystem(Family.all(CameraComponent::class.java).get()) {
    private val cmCamera = ComponentMapper.getFor(CameraComponent::class.java)
    private val cmBind = ComponentMapper.getFor(BindComponent::class.java)
    private val camPos = Vector2()
    private val bodPos = Vector2()
    private val difVec = Vector2()
    private val radVec = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val cameraCmp = cmCamera.get(entity)
        val bindCmp = cmBind.get(entity)
        bodPos.set(bindCmp.entity.getComponent(B2dBodyComponent::class.java).body.position)
        difVec.set(bodPos).sub(camPos)
        if (difVec.len2() > Constants.CAMERA_RADIUS_2) {
            radVec.set(Constants.CAMERA_RADIUS, 0f).rotate(difVec.angle())
            camPos.set(bodPos).sub(radVec)
        }
        cameraCmp.camera.position.set(camPos, 0f)
        cameraCmp.camera.update()
    }
}