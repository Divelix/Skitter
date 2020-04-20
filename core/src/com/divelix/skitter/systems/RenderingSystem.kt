package com.divelix.skitter.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.Constants
import com.divelix.skitter.Data
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.*
import ktx.ashley.allOf
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.assets.file
import ktx.graphics.use
import ktx.inject.Context

class RenderingSystem(
        context: Context,
        private val camera: OrthographicCamera
) : SortedIteratingSystem(allOf(TransformComponent::class, TextureComponent::class).get(), ZComparator()) {

    private val batch = context.inject<SpriteBatch>()
    private val entities = Array<Entity>()
    private val comparator = ZComparator()
//    private val shader = ShaderProgram(file(Constants.VERTEX_SHADER), file(Constants.FRAGMENT_SHADER))

    private val healthBarReg: TextureRegion

    init {
        val redPixel = Pixmap(1, 1, Pixmap.Format.RGBA8888).apply {
            setColor(1f, 0f, 0f, 1f)
            fill()
        }
        healthBarReg = TextureRegion(Texture(redPixel))
//        ShaderProgram.pedantic = false // SpriteBatch won'stockTable send ALL info to shader program
//        println(if(shader.isCompiled) "shader successfully compiled" else shader.log)
//        batch.shader = shader
    }

    override fun update(deltaTime: Float) {
        Data.renderTime += deltaTime
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        super.update(deltaTime)

        entities.sort(comparator)// TODO probably not needed as super.update already do the sorting (needs check)

//        shader.use {
//            shader.setUniformf("u_time", Data.renderTime)
//        }
        batch.projectionMatrix = camera.combined
//        batch.enableBlending()
        batch.use {
            for (entity in entities) {
                val textureCmp = GameEngine.cmTexture.get(entity)
                val transformCmp = GameEngine.cmTransform.get(entity)

//                if (textureCmp.region == null || transformCmp.isHidden) continue

                val width = transformCmp.size.x
                val height = transformCmp.size.y

                val originX = transformCmp.origin.x
                val originY = transformCmp.origin.y

//            if (typeCmp.name == TypeComponent.BULLET)
//                batch.shader = bulletShader
                batch.draw(textureCmp.region,
                        transformCmp.position.x - originX, transformCmp.position.y - originY,
                        originX, originY,
                        width, height,
                        1f, 1f,
                        transformCmp.rotation)
                if(entity.has(GameEngine.cmHealthBar)) {
                    val healthBarCmp = GameEngine.cmHealthBar.get(entity)
                    val healthCmp = GameEngine.cmHealth.get(entity)
                    batch.draw(healthBarReg,
                            transformCmp.position.x - originX, transformCmp.position.y - originY + transformCmp.size.y,
                            width * healthCmp.health / healthBarCmp.maxValue, healthBarCmp.height)
                }
            }
        }
        entities.clear()
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        entities.add(entity)
    }
}

class ZComparator : Comparator<Entity> {
    private val cmTrans = mapperFor<TransformComponent>()

    override fun compare(entityA: Entity, entityB: Entity): Int {
        val az = cmTrans.get(entityA).position.z
        val bz = cmTrans.get(entityB).position.z
        var res = 0
        if (az > bz) {
            res = 1
        } else if (az < bz) {
            res = -1
        }
        return res
    }
}