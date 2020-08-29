package com.divelix.skitter.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.image
import com.divelix.skitter.utils.BotViewport
import ktx.actors.onTouchUp
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton

class MenuScreen(game: Main): KtxScreen {
    private val context = game.getContext()
    private val batch = context.inject<SpriteBatch>()
    private val assets = context.inject<Assets>()
    private val stage = Stage(BotViewport(Constants.STAGE_WIDTH.toFloat(), Constants.STAGE_HEIGHT.toFloat()), batch)

    init {
        stage += scene2d.table {
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
            row()
            textButton("1").onTouchUp {
                game.screen = PlayScreen(game)
            }
            textButton("2").onTouchUp {
                game.screen = PlayScreen(game)
            }
        }
    }

    override fun show() {
        //TODO add InputHandler on BACK btn up (pop up Dialog)
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(assets.BG_COLOR.r, assets.BG_COLOR.g, assets.BG_COLOR.b, assets.BG_COLOR.a)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height)
    }
}