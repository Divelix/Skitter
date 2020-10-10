package com.divelix.skitter.ui.menu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import ktx.scene2d.Scene2DSkin
import ktx.style.get

class TabbedBar(val assets: Assets): Table() {
    val tabs = arrayOf(Tab(Constants.SHIPS_TAB, null), Tab(Constants.GUNS_TAB, null))
    val content = Container<Table>()
    lateinit var activeTab: Tab

    init {
        tabs.forEach { add(it) }
        row()
        add(content).colspan(tabs.size)
    }

    fun makeActive(tab: Tab) {
        tabs.forEach { it.bg.drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30)) }
        tab.bg.drawable = null
        content.actor = tab.content
        activeTab = tab
    }

    inner class Tab(val tabName: String, var content: Table?): Group() {
        private val tabHeight = 66f
        private val iconHeight = 50f
        val bg: Image
        val texture: Texture
        private val icon: Image

        init {
            touchable = Touchable.enabled
            setSize(Constants.STAGE_WIDTH.toFloat() / 2, tabHeight)
            bg = Image(TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))).apply { setFillParent(true) }
            texture = when (tabName) {
                Constants.SHIPS_TAB -> assets.manager.get<Texture>(Constants.SHIP_ICON)
                Constants.GUNS_TAB -> assets.manager.get<Texture>(Constants.GUN_ICON)
                else -> error {"No texture for tab"}
            }
            val aspectRatio = texture.width.toFloat() / texture.height.toFloat()
            icon = Image(texture).apply {
                setSize(iconHeight * aspectRatio, iconHeight)
                setPosition((this@Tab.width - width) / 2f, (this@Tab.height - height) / 2f)
            }
            addActor(bg.apply { touchable = Touchable.disabled })
            addActor(icon.apply { touchable = Touchable.disabled })
        }
    }
}