package com.divelix.skitter.ui.menu.scroll

import com.divelix.skitter.ui.menu.ModView

interface ModSelector {
    var selectedModView: ModView?

    fun selectMod(modView: ModView) {
        selectedModView = if (selectedModView == null) {
            modView.apply { activate() }
        } else {
            selectedModView!!.deactivate()
            if (selectedModView == modView) {
                null
            } else {
                modView.apply { activate() }
            }
        }
    }
}