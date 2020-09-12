package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.scenes.scene2d.Group
import com.divelix.skitter.data.Constants
import ktx.scene2d.KGroup
import ktx.scene2d.label
import ktx.scene2d.table

abstract class Page : Group(), KGroup {
    init {
        width = Constants.STAGE_WIDTH.toFloat()
        height = Constants.STAGE_HEIGHT.toFloat()
    }

    abstract fun update()
}