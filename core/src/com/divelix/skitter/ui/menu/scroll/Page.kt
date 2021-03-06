package com.divelix.skitter.ui.menu.scroll

import com.badlogic.gdx.scenes.scene2d.Group
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import ktx.inject.Context
import ktx.scene2d.KGroup

abstract class Page(val context: Context) : Group(), KGroup {
    val assets = context.inject<Assets>()

    init {
        width = Constants.STAGE_WIDTH.toFloat()
        height = Constants.stageHeight
    }
}