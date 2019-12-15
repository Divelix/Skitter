package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.viewport.FitViewport
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.vis.table

class MenuScreen(game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(FitViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    init {
        stage += table {
            setFillParent(true)
            debugAll()
            table {
                defaults().expandX().pad(10f)
                image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.SHIPS_BTN)))
                image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.MODS_BTN))).addListener(object: ClickListener() {
                    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                        game.screen = ModScreen(game)
                        super.touchUp(event, x, y, pointer, button)
                    }
                })
                image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.GUNS_BTN))).addListener(object: ClickListener() {
                    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                        game.screen = GunScreen(game)
                        super.touchUp(event, x, y, pointer, button)
                    }
                })
            }
            row()
            image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.BUCKET_ICON))).addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    game.screen = PlayScreen(game)
                    super.touchUp(event, x, y, pointer, button)
                }
            })
        }
    }

    override fun show() {
        //TODO add InputHandler on BACK btn up (pop up Dialog)
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0.6f, 0.5f, 0.8f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }
}