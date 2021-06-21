package com.divelix.skitter.desktop

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.divelix.skitter.data.Constants
import com.divelix.skitter.Main

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        Lwjgl3Application(Main(), Lwjgl3ApplicationConfiguration().apply {
            setTitle(Constants.TITLE)
            setWindowPosition(1000, 100)
            setWindowSizeLimits(
                Constants.DESKTOP_WIDTH,
                Constants.DESKTOP_HEIGHT,
                Constants.DESKTOP_WIDTH,
                Constants.DESKTOP_HEIGHT
            )
        })
    }
}
