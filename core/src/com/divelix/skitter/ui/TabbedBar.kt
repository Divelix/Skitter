package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants

class TabbedBar(val assets: Assets): Table() {
    val tabs = arrayOf(Tab(Constants.SHIPS_TAB, null), Tab(Constants.GUNS_TAB, null))
    private val content = Container<Table>()
    lateinit var activeTab: Tab

    private val upColor = Constants.UI_COLOR
    private val downColor = Color(0f, 0f, 0f, 0f)
    private val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
    val upDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(upColor); fill() }))
    val downDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(downColor); fill() }))

    init {
        tabs.forEach { add(it) }
        row()
        add(content).colspan(tabs.size)
    }

    fun makeActive(tab: Tab) {
        tabs.forEach { it.bg.drawable = upDrawable }
        tab.bg.drawable = downDrawable
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
            setSize(Constants.D_WIDTH.toFloat() / 2, tabHeight)
            bg = Image(upDrawable).apply { setFillParent(true) }
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