package com.divelix.skitter.ui.scrollmenu

import com.divelix.skitter.ui.tabbedmenu.ModView

interface ModSelector {
    var activeModView: ModView?

    fun selectMod(modView: ModView) {
        if (activeModView != null) activeModView!!.deactivate()
        activeModView = modView
    }
}