package com.divelix.skitter.ui.scrollmenu

import com.divelix.skitter.ui.tabbedmenu.ModView

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