package com.divelix.skitter.utils

import com.kotcrab.vis.ui.widget.VisLabel
import ktx.style.defaultStyle

open class ScaledLabel(text: String = "", styleName: String = defaultStyle, scale: Float = 0.5f): VisLabel(text, styleName) {
    init {
        this.setFontScale(scale)
    }
}