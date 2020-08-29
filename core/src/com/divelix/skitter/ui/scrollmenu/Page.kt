package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.scenes.scene2d.Group
import com.divelix.skitter.data.Constants

abstract class Page : Group() {
    init {
        width = Constants.STAGE_WIDTH.toFloat()
        height = Constants.STAGE_HEIGHT.toFloat()
    }

    abstract fun update()
}