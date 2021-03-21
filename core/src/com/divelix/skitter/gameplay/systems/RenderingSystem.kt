package com.divelix.skitter.gameplay.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Data
import com.divelix.skitter.gameplay.components.*
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.oneOf
import ktx.graphics.use
import ktx.inject.Context
import ktx.log.error

class RenderingSystem(
        context: Context,
        private val camera: OrthographicCamera
) : SortedIteratingSystem(
        allOf(TransformComponent::class).oneOf(TextureComponent::class, TileComponent::class).get(),
        compareBy { entity -> entity[TransformComponent.mapper] }
) {

    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()

    override fun update(deltaTime: Float) {
        Data.renderTime += deltaTime
        assets.frameBuffer.use {
            Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            batch.use(camera.combined) {
                super.update(deltaTime)
            }
        }
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        val transformCmp = entity[TransformComponent.mapper]
        require(transformCmp != null) { "Entity $entity don't have TransformComponent for RenderSystem" }
        val x = transformCmp.position.x
        val y = transformCmp.position.y
        val width = transformCmp.size.x
        val height = transformCmp.size.y
        val originX = transformCmp.origin.x
        val originY = transformCmp.origin.y

        if (entity.has(TextureComponent.mapper)) {
            val textureCmp = entity[TextureComponent.mapper]
            require(textureCmp != null) { "Entity $entity don't have TextureComponent for RenderSystem" }
            if (textureCmp.sprite.texture == null) { error{"No texture"}; return }
            textureCmp.sprite.run {
                setSize(width, height)
                setOrigin(originX, originY)
                setOriginBasedPosition(x, y)
                rotation = transformCmp.rotation
                draw(batch)
            }

            if (entity.has(TowerComponent.mapper)) {
                val towerCmp = entity[TowerComponent.mapper]
                require(towerCmp != null) {"Tower don't have TowerComponent"}
                val aspectRatio = towerCmp.sprite.regionWidth.toFloat() / towerCmp.sprite.regionHeight.toFloat()
                val towerWidth = width / 2
                val towerHeight = towerWidth / aspectRatio
                val towerOriginX = towerWidth / 2
                val towerOriginY = towerHeight / 3 // 3 is this texture specific value
                if (textureCmp.sprite.texture == null) { error{"No texture for tower"}; return }
                towerCmp.sprite.run {
                    setSize(towerWidth, towerHeight)
                    setOrigin(towerOriginX, towerOriginY)
                    setOriginBasedPosition(x, y)
                    rotation = towerCmp.angle - 90f
                    draw(batch)
                }
            }

            if (entity.has(HealthBarComponent.mapper)) {
                val healthBarCmp = entity[HealthBarComponent.mapper]
                val healthCmp = entity[HealthComponent.mapper]
                require(healthBarCmp != null && healthCmp != null) {"Entity has no HealthComponent or HealthBarComponent"}
                if (textureCmp.sprite.texture == null) { error{"No texture for health bar"}; return }
                healthBarCmp.sprite.run {
                    setSize(width * healthCmp.health / healthBarCmp.maxValue, healthBarCmp.height)
                    setOrigin(originX, originY)
                    setOriginBasedPosition(x, y + height)
                    draw(batch)
                }
            }
        } else if (entity.has(TileComponent.mapper)) {
            val tileCmp = entity[TileComponent.mapper]
            require(tileCmp != null) { "Entity $entity don't have TileComponent for RenderSystem" }
            if (tileCmp.tile.region == null) { error { "TileDrawable in $entity has null region" } }
            tileCmp.tile.draw(batch, x - originX, y - originY, width, height)
        }
    }
}