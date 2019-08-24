package com.divelix.skitter.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.JsonReader
import com.divelix.skitter.*
import com.divelix.skitter.utils.B2dContactListener
import com.divelix.skitter.systems.*
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.DynamicData
import com.divelix.skitter.utils.EntityBuilder
import ktx.app.KtxScreen
import ktx.assets.toInternalFile
import java.util.*

class PlayScreen(game: Main): KtxScreen {
    companion object {
        var slowRate = Constants.DEFAULT_SLOW_RATE
        var isPaused = false
    }
    private val context = game.getContext()
    private val assets = context.inject<Assets>()

    private val world = World(Vector2(0f, 0f), true)
    private val engine = PooledEngine()
    private val entityBuilder = EntityBuilder(engine, world, assets)
    private val hud: Hud
    private val blackList = ArrayList<Body>() // list of bodies to kill
    private val camera: OrthographicCamera
    private val playerEntity: Entity

    init {
        val playerReader = JsonReader().parse("json/player_data.json".toInternalFile())
        val specs = playerReader.get("active_gun_specs")
        for (i in 0 until Data.playerData.gun.size)
            Data.playerData.gun[i] = specs[i].asFloat()

//        bg.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
//        bgReg.setRegion(0, 0, Constants.WIDTH, Constants.HEIGHT)

        playerEntity = entityBuilder.createPlayer()
        camera = entityBuilder.createCamera(playerEntity)
        hud = Hud(game, camera)
        entityBuilder.createEnemy(-3f, 7f, 1f, playerEntity)
        entityBuilder.createEnemy(0f, 7f, 1f, playerEntity)
        entityBuilder.createEnemy(3f, 7f, 1f, playerEntity)

        engine.addSystem(CameraSystem())
        engine.addSystem(RenderingSystem(context, camera))
        engine.addSystem(PhysicsSystem(world, blackList))
        engine.addSystem(PhysicsDebugSystem(world, camera))
        engine.addSystem(CollisionSystem())
        engine.addSystem(PlayerSystem())
//        engine.addSystem(EnemySystem())
        engine.addSystem(BulletSystem())
//        engine.addSystem(ClickableSystem(camera))

        ShaderProgram.pedantic = false

        val handler = object: InputAdapter() {
            override fun keyDown(keycode: Int): Boolean {
                when(keycode) {
                    Input.Keys.BACK -> game.screen = GunScreen(game)
                    Input.Keys.SPACE -> isPaused = !isPaused
                    Input.Keys.B -> println(world.bodyCount)
                    Input.Keys.A -> println(Data.dynamicData.aims)
                    Input.Keys.V -> println("HudCam: (${hud.camera.viewportWidth}; ${hud.camera.viewportHeight})")
                    Input.Keys.Z -> println("Table pos: (${hud.rootTable.x}; ${hud.rootTable.y})")
                }
                return true
            }
        }
        val multiplexer = InputMultiplexer(handler, hud.stage, hud.playerCtrl)
        Gdx.input.inputProcessor = multiplexer
        world.setContactListener(B2dContactListener())
    }

    override fun render(delta: Float) {
        engine.update(delta)
        if (!isPaused) {
            shootBullets(Data.dynamicData.aims)
            clearDeadBodies()
        }
        hud.update(delta)
    }

    override fun show() {
        isPaused = false
    }

    override fun pause() {
        isPaused = true
    }

    override fun resume() {
        isPaused = false
    }

    override fun resize(width: Int, height: Int) {
        camera.setToOrtho(false, Constants.WIDTH, Constants.WIDTH * height/width)
//        hud.camera.setToOrtho(false, width.toFloat(), height.toFloat())
        hud.widthRatio = width / Constants.WIDTH
        hud.stage.viewport.update(width, height, true)
        println("resize(): Resolution = ($width; $height) | HEIGHT = ${Constants.WIDTH * height/width} | widthRatio = ${hud.widthRatio}")
    }

    override fun hide() {
        super.hide()
    }

    override fun dispose() {
        hud.dispose()
        engine.clearPools()
    }

    private fun shootBullets(aims: Array<Vector2>) {
        if (isPaused || aims.size == 0) return
        for (aim in aims) {
            if (Data.dynamicData.ammo == 0) break
            entityBuilder.createBullet(playerEntity, aim)
            Data.dynamicData.ammo--
            hud.ammoLabel.setText("${Data.dynamicData.ammo}")
        }
        aims.clear()
    }

    private fun clearDeadBodies() {
        if (blackList.size > 0) {
            for (body in blackList) {
                val entity = body.userData as Entity?
                if (entity != null)
                    engine.removeEntity(entity)
                world.destroyBody(body)
            }
            blackList.clear()
        }
    }
}
