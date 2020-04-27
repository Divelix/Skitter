package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.Constants
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.*
import ktx.ashley.allOf
import java.lang.NullPointerException

class CameraSystem: IteratingSystem(allOf(CameraComponent::class).get()) {
    private val camPos = Vector2()
    private val bodPos = Vector2()
    private val difVec = Vector2()
    private val radVec = Vector2()

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val cameraCmp = GameEngine.cmCamera.get(entity)
        val bindCmp = GameEngine.cmBind.get(entity)
        camPos.set(cameraCmp.camera.position.x, cameraCmp.camera.position.y)
        try {
            bodPos.set(GameEngine.cmBody.get(bindCmp.entity).body.position)
        } catch (e: NullPointerException) {
            return
        }
        if (cameraCmp.needCenter) {
            cameraCmp.camera.position.set(bodPos, 0f)
            cameraCmp.needCenter = false
            return
        }
        difVec.set(bodPos).sub(camPos)
        if (difVec.len2() > Constants.CAMERA_RADIUS_2) {
            radVec.set(Constants.CAMERA_RADIUS, 0f).rotate(difVec.angle())
            camPos.set(bodPos).sub(radVec)
        }
        cameraCmp.camera.position.set(camPos, 0f)
        cameraCmp.camera.update()
    }
}