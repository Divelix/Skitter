package com.divelix.skitter.ui.tabbedmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.data.Constants
import ktx.actors.onClickEvent
import ktx.scene2d.Scene2DSkin
import ktx.style.get

class TabbedMenu(tabs: Array<Tab>) : Table() {
    private val bgDrawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_COLOR_30))
    private var activeTab = tabs[0]
    private val content = Container<Table>(activeTab.contentTable)

    init {
        defaults().growX()
        tabs.forEach {
            add(it.apply {
                background = bgDrawable
                onClickEvent { event, actor -> switchTo(it) }
            })
        }
        row()
        add(content).colspan(tabs.size).width(Constants.STAGE_WIDTH.toFloat())
        // define initial active tab
        switchTo(tabs[0])
    }

    fun switchTo(tab: Tab) {
        activeTab.background = bgDrawable
        activeTab = tab
        activeTab.background = null
        content.actor = activeTab.contentTable
    }
}