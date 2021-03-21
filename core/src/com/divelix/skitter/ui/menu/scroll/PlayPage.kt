package com.divelix.skitter.ui.menu.scroll

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.divelix.skitter.Main
import com.divelix.skitter.data.ActivePlayerData
import com.divelix.skitter.data.RegionName
import com.divelix.skitter.screens.PlayScreen
import ktx.actors.onClickEvent
import ktx.inject.Context
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.image
import ktx.scene2d.table
import ktx.style.get

class PlayPage(context: Context, val activePlayerData: ActivePlayerData) : Page(context) {
    val game = context.inject<Main>()

    init {
        table {
            setFillParent(true)
            image(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.MENU_PLAY())).onClickEvent { _ -> this@PlayPage.setPlayScreen()}
        }
    }

    private fun setPlayScreen() {
        game.screen = PlayScreen(game, activePlayerData)
    }
}