package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.*
import com.divelix.skitter.utils.ZComparator
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.graphics.use
import ktx.inject.Context
import ktx.log.debug

class RenderingSystem(
        context: Context,
        private val camera: OrthographicCamera
) : SortedIteratingSystem(allOf(TransformComponent::class, TextureComponent::class).get(), ZComparator()) {

    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val entities = Array<Entity>()

    private val healthBarReg: TextureRegion

    init {
        val redPixel = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
            setColor(1f, 0f, 0f, 1f)
            fill()
        }
        healthBarReg = TextureRegion(Texture(redPixel))
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        Data.renderTime += deltaTime

        assets.frameBuffer.use {
            Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            batch.projectionMatrix = camera.combined
            batch.use {
                for (entity in entities) {
                    val textureCmp = entity[TextureComponent.mapper]!!
                    val transformCmp = entity[TransformComponent.mapper]!!

                    val width = transformCmp.size.x
                    val height = transformCmp.size.y

                    val originX = transformCmp.origin.x
                    val originY = transformCmp.origin.y

                    batch.draw(textureCmp.region,
                            transformCmp.position.x - originX, transformCmp.position.y - originY,
                            originX, originY,
                            width, height,
                            1f, 1f,
                            transformCmp.rotation)

                    if (entity.has(TowerComponent.mapper)) {
                        val towerCmp = entity[TowerComponent.mapper]!!
                        val aspectRatio = towerCmp.region.regionWidth.toFloat() / towerCmp.region.regionHeight.toFloat()
                        val towerWidth = width / 2
                        val towerHeight = towerWidth / aspectRatio
                        val towerOriginX = towerWidth / 2
                        val towerOriginY = towerHeight / 3
                        batch.draw(towerCmp.region,
                                transformCmp.position.x - towerOriginX, transformCmp.position.y - towerOriginY,
                                towerOriginX, towerOriginY, towerWidth, towerHeight, 1f, 1f, towerCmp.angle - 90f)
                    }

                    if (entity.has(HealthBarComponent.mapper)) {
                        val healthBarCmp = entity[HealthBarComponent.mapper]!!
                        val healthCmp = entity[HealthComponent.mapper]!!
                        batch.draw(healthBarReg,
                                transformCmp.position.x - originX, transformCmp.position.y - originY + transformCmp.size.y,
                                width * healthCmp.health / healthBarCmp.maxValue, healthBarCmp.height)
                    }
                }
            }
        }
        entities.clear()
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        entities.add(entity)
    }
}