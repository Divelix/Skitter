package com.divelix.skitter.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.components.*
import ktx.ashley.has
import ktx.assets.file
import ktx.graphics.use
import ktx.inject.Context

class RenderingSystem(context: Context, private val camera: OrthographicCamera) : SortedIteratingSystem(Family.all(TransformComponent::class.java, TextureComponent::class.java).get(), ZComparator()) {

    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val entities = Array<Entity>()
    private val comparator = ZComparator()
    private val bulletShader = ShaderProgram(file(Constants.VERTEX_SHADER), file(Constants.FRAGMENT_SHADER))

    private val cmType: ComponentMapper<TypeComponent> = ComponentMapper.getFor(TypeComponent::class.java)
    private val cmTexture: ComponentMapper<TextureComponent> = ComponentMapper.getFor(TextureComponent::class.java)
    private val cmTransform: ComponentMapper<TransformComponent> = ComponentMapper.getFor(TransformComponent::class.java)
    private val cmHealthBar: ComponentMapper<HealthBarComponent> = ComponentMapper.getFor(HealthBarComponent::class.java)
    private val cmHealth: ComponentMapper<HealthComponent> = ComponentMapper.getFor(HealthComponent::class.java)

    private var timer = 0f

    private val bg = assets.manager.get<Texture>(Constants.BACKGROUND_IMAGE)
    private val bgReg = TextureRegion(bg)
    private val healthBarReg: TextureRegion

    init {
        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        bgReg.setRegion(0, 0, 5000, 5000)
        val redPixel = Pixmap(1, 1, Pixmap.Format.RGBA8888) .apply {
            setColor(1f, 0f, 0f, 1f)
            fill()
        }
        healthBarReg = TextureRegion(Texture(redPixel))
//        camera.position.set(Constants.B2D_WIDTH / 2f, Constants.B2D_HEIGHT / 2f, 0f)
        ShaderProgram.pedantic = false // SpriteBatch won'stockTable send ALL info to shader program
        println(if(bulletShader.isCompiled) "shader successfully compiled" else bulletShader.log)
        batch.shader = bulletShader
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        entities.sort(comparator)

        bulletShader.use {
            timer += deltaTime
            bulletShader.setUniformf("u_time", timer)
        }
        batch.projectionMatrix = camera.combined
//        batch.enableBlending()
        batch.use {
//            batch.draw(bgReg, -200f, -200f, 400f, 400f)

            for (entity in entities) {
                val textureCmp = cmTexture.get(entity)
                val transformCmp = cmTransform.get(entity)

                if (textureCmp.region == null || transformCmp.isHidden) continue

//            val width = texture.region!!.regionWidth.toFloat()
//            val height = texture.region!!.regionHeight.toFloat()
                val width = transformCmp.size.x
                val height = transformCmp.size.y

                val originX = transformCmp.origin.x
                val originY = transformCmp.origin.y

//            if (typeCmp.name == TypeComponent.BULLET)
//                batch.shader = bulletShader
                batch.draw(textureCmp.region!!,
                        transformCmp.position.x - originX, transformCmp.position.y - originY,
                        originX, originY,
                        width, height,
                        1f, 1f,
                        transformCmp.rotation)
                if(entity.has(cmHealthBar)) {
                    val healthBarCmp = cmHealthBar.get(entity)
                    val healthCmp = cmHealth.get(entity)
                    batch.draw(healthBarReg,
                            transformCmp.position.x - originX, transformCmp.position.y - originY + transformCmp.size.y,
                            width * healthCmp.health / 100, healthBarCmp.height)
                }
//            batch.shader = null
            }
//            batch.flush()
        }
        entities.clear()
    }

    public override fun processEntity(entity: Entity, deltaTime: Float) {
        entities.add(entity)
    }
}

class ZComparator : Comparator<Entity> {
    private val cmTrans: ComponentMapper<TransformComponent> = ComponentMapper.getFor(TransformComponent::class.java)

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