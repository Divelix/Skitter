package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.utils.BotViewport
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.vis.table

class MenuScreen(game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(BotViewport(Constants.D_WIDTH.toFloat(), Constants.D_HEIGHT.toFloat()), batch)

    init {
        stage += table {
            setFillParent(true)
            bottom()
            image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.MENU_EQUIP))).cell(padRight = 75f).addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    game.screen = EquipScreen(game)
                    super.touchUp(event, x, y, pointer, button)
                }
            })
            image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.MENU_MOD))).addListener(object: ClickListener() {
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                    game.screen = ModScreen(game)
                    super.touchUp(event, x, y, pointer, button)
                }
            })
            row()
            image(TextureRegionDrawable(assets.manager.get<Texture>(Constants.MENU_PLAY))).cell(padTop = 75f, padBottom = 75f, colspan = 2).addListener(object: ClickListener() {
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
        Gdx.gl.glClearColor(Constants.BG_COLOR.r, Constants.BG_COLOR.g, Constants.BG_COLOR.b, Constants.BG_COLOR.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
}