package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.divelix.skitter.data.Constants
import com.divelix.skitter.gameplay.components.B2dBodyComponent
import com.divelix.skitter.gameplay.components.BindComponent
import com.divelix.skitter.gameplay.components.CameraComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.log.info
import java.lang.NullPointerException

class CameraSystem: IteratingSystem(allOf(CameraComponent::class).get()) {
    private val camPos = Vector2()
    private val bodPos = Vector2()
    private val difVec = Vector2()
    private val radVec = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val playerBody = try {
            val playerEntity = entity[BindComponent.mapper]!!.entity
            playerEntity[B2dBodyComponent.mapper]!!.body
        } catch (e: NullPointerException) {
//            info(TAG) { "Camera entity lost player bind" }
            entity.remove(BindComponent::class.java)
            return
        }

        if (entity.has(BindComponent.mapper)) {
            val cameraCmp = entity[CameraComponent.mapper]!!
            camPos.set(cameraCmp.camera.position.x, cameraCmp.camera.position.y)
            bodPos.set(playerBody.position)
            if (cameraCmp.needCenter) {
                cameraCmp.camera.position.set(bodPos, 0f)
                cameraCmp.needCenter = false
                return
            }
            difVec.set(bodPos).sub(camPos)
            if (difVec.len2() > Constants.CAMERA_RADIUS_2) {
                radVec.set(Constants.CAMERA_RADIUS, 0f).rotateDeg(difVec.angleDeg())
                camPos.set(bodPos).sub(radVec)
            }
            cameraCmp.camera.position.set(camPos, 0f)
            cameraCmp.camera.update()
        } else {
            info(TAG) { "No bind - camera is free floating" }
        }
    }

    companion object {
        const val TAG = "CameraSystem"
    }
}