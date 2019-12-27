package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import com.divelix.skitter.Assets
import com.divelix.skitter.Constants
import ktx.collections.*

class TabbedBar(val tabsMap: ObjectMap<String, Table>, val assets: Assets): Table() {
    private val tabs = Array<Tab>(2)
    private val content = Container<Table>()

    private val upColor = Constants.UI_COLOR
    private val downColor = Color(0f, 0f, 0f, 0f)
    private val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
    val upDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(upColor); fill() }))
    val downDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(downColor); fill() }))

    init {
        tabsMap.forEach { (k, v) ->  tabs.add(Tab(k, v))}

        tabs.forEach { add(it) }
        row()
        add(content).colspan(tabs.size)

        makeActive(tabs[0])
    }

    fun makeActive(tab: Tab) {
        tabs.forEach { it.bg.drawable = upDrawable }
        tab.bg.drawable = downDrawable
        content.actor = tab.content
    }

    inner class Tab(name: String, val content: Table?): Group() {
        private val tabHeight = 66f
        private val iconHeight = 50f
        val bg: Image
        val texture: Texture
        private val icon: Image

        init {
            touchable = Touchable.enabled
            setSize(Constants.D_WIDTH.toFloat() / tabsMap.size, tabHeight)
            bg = Image(upDrawable).apply { setFillParent(true) }
            texture = when (name) {
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