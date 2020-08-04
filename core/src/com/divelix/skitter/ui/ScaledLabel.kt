package com.divelix.skitter.ui

import com.kotcrab.vis.ui.widget.VisLabel
import ktx.style.defaultStyle

// TODO get rid of this ugly crutch
open class ScaledLabel(text: String = "", styleName: String = defaultStyle, scale: Float = 0.5f): VisLabel(text, styleName) {
    init {
        this.setFontScale(scale)
    }
}