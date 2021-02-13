package com.divelix.skitter.ui.menu.scroll

import com.badlogic.gdx.graphics.Texture
import com.divelix.skitter.Main
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.PlayerData
import com.divelix.skitter.image
import com.divelix.skitter.screens.PlayScreen
import ktx.actors.onClickEvent
import ktx.inject.Context
import ktx.scene2d.table

class PlayPage(context: Context, val playerData: PlayerData) : Page(context) {
    val game = context.inject<Main>()

    init {
        table {
            setFillParent(true)
            image(this@PlayPage.assets.manager.get<Texture>(Constants.MENU_PLAY)).onClickEvent { event ->
                this@PlayPage.game.screen = PlayScreen(this@PlayPage.game)
            }
        }
    }

    override fun update() {}
}