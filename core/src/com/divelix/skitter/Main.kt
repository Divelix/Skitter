package com.divelix.skitter

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.divelix.skitter.screens.LoadingScreen
import ktx.inject.Context

class Main : Game() {
    private val context = Context()

    override fun create() {
        context.register {
            bindSingleton(SpriteBatch())
            bindSingleton(ShapeRenderer())
            bindSingleton(Assets())
        }
        setScreen(LoadingScreen(this))
    }

    override fun dispose() {
        context.dispose()
    }

    fun getContext(): Context { return context }
}

