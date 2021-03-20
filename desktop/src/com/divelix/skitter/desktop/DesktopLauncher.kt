package com.divelix.skitter.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.divelix.skitter.data.Constants
import com.divelix.skitter.Main

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        LwjglApplication(Main(), LwjglApplicationConfiguration().apply {
            title = Constants.TITLE
            x = 1180
            y = 0
//            width = TestAIScreen.D_WIDTH // for TestAIScreen
//            height = TestAIScreen.D_HEIGHT
            width = Constants.DESKTOP_WIDTH
            height = Constants.DESKTOP_HEIGHT
//            resizable = false
            forceExit = false // for LWJGL3
        })
    }
}
