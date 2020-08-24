package com.divelix.skitter

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.Json
import com.divelix.skitter.data.Assets
import com.divelix.skitter.screens.LoadingScreen
import ktx.inject.Context
import ktx.inject.register

class Main : Game() {
    private val context = Context()

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        context.register {
            bindSingleton(SpriteBatch())
            bindSingleton(ShapeRenderer())
            bindSingleton(Assets())
            bindSingleton(Json())
        }
        setScreen(LoadingScreen(this))
    }

    override fun dispose() {
        context.dispose()
    }

    fun getContext(): Context { return context }
}

