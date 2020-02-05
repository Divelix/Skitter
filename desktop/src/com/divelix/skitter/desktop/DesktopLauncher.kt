package com.divelix.skitter.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.divelix.skitter.Constants
import com.divelix.skitter.Main
import com.divelix.skitter.screens.TestAIScreen

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        LwjglApplication(Main(), LwjglApplicationConfiguration().apply {
            title = Constants.TITLE
            width = TestAIScreen.D_WIDTH // for TestAIScreen
            height = TestAIScreen.D_HEIGHT
//            width = Constants.D_WIDTH
//            height = Constants.D_HEIGHT
//            resizable = false
        })
    }
}
