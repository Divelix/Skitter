package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
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

class TabbedBar(tabsMap: ObjectMap<String, Table>, val assets: Assets): Table() {
    val tabs = Array<Tab>(2)
    val content = Container<Table>()

    init {
        tabsMap.forEach { (k, v) ->  tabs.add(Tab(k, v))}

        tabs.forEach { add(it) }
        row()
        add(content)
//        makeActive(tabsMap.get(Constants.SHIPS_TAB))
    }

    inner class Tab(tabName: String, tabContent: Table?): Group() {
        val tabHeight = 66f
        val iconHeight = 50f
        val upColor = Constants.UI_COLOR
        val downColor = Color(0f, 0f, 0f, 0f)
        val bg: Image
        val texture: Texture
        val icon: Image

        init {
            setSize(Constants.WIDTH/2f, tabHeight)
            val bgPixel = Pixmap(1, 1, Pixmap.Format.Alpha)
            val upDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(upColor); fill() }))
            val downDrawable = TextureRegionDrawable(Texture(bgPixel.apply { setColor(downColor); fill() }))
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
            addActor(bg)
            addActor(icon)
            addListener(object: ClickListener() {
                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                    content.actor = tabContent
                    return super.touchDown(event, x, y, pointer, button)
                }
            })
        }
    }
}