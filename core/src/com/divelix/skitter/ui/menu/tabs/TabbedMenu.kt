package com.divelix.skitter.ui.menu.tabs

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Constants
import ktx.actors.onClickEvent
import ktx.scene2d.Scene2DSkin
import ktx.style.get

class TabbedMenu(val tabs: Array<Tab>) : Table() {
    private val bgDrawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
    private var activeTab = tabs[0]
    private val content = Container(activeTab.contentTable)

    init {
        defaults().growX()
        tabs.forEach {
            add(it.apply {
                background = bgDrawable
                onClickEvent { _ -> switchTo(it) }
            })
        }
        row()
        add(content).colspan(tabs.size).width(Constants.STAGE_WIDTH.toFloat())
        // define initial active tab
        switchTo(tabs[0])
    }

    private fun switchTo(tab: Tab) {
        activeTab.background = bgDrawable
        activeTab = tab
        activeTab.background = null
        content.actor = activeTab.contentTable
    }
}