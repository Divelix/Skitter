package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Group
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import ktx.inject.Context
import ktx.scene2d.KGroup
import ktx.scene2d.label
import ktx.scene2d.table

abstract class Page(val context: Context) : Group(), KGroup {
    val assets = context.inject<Assets>()

    init {
        width = Constants.STAGE_WIDTH.toFloat()
        height = Constants.stageHeight
    }

    abstract fun update()
}