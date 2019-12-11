package com.divelix.skitter.utils

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.viewport.Viewport
import kotlin.math.roundToInt

class TopViewport(worldWidth: Float, worldHeight: Float, cam: OrthographicCamera = OrthographicCamera()): Viewport() {

    init {
        setWorldSize(worldWidth, worldHeight)
        camera = cam
    }

    override fun update(screenWidth: Int, screenHeight: Int, centerCamera: Boolean) {
        val scale = screenWidth / worldWidth
        val viewportWidth = (worldWidth * scale).roundToInt()
        val viewportHeight = (worldHeight * scale).roundToInt()
        setScreenBounds(0, screenHeight - viewportHeight, viewportWidth, viewportHeight)
        apply(centerCamera)
    }
}