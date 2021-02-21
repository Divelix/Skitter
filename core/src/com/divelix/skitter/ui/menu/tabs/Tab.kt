package com.divelix.skitter.ui.menu.tabs

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Scaling
import ktx.actors.onClickEvent

class Tab(iconTexture: Texture, val contentTable: Table) : Table() {
    init {
        touchable = Touchable.enabled
        add(Image(iconTexture).apply { setScaling(Scaling.fit) }).size(50f).pad(8f)
        onClickEvent { _ -> this@Tab.background = null }
    }
}