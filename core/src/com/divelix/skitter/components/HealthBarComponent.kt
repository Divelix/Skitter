package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

class HealthBarComponent: Component {
    val height = 0.2f
    var maxValue = 100f
}