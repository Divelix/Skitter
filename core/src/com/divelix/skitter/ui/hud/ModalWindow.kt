package com.divelix.skitter.ui.hud

import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Align
import com.divelix.skitter.data.Constants
import com.divelix.skitter.gameplay.GameEngine
import ktx.scene2d.KTable
import ktx.scene2d.Scene2DSkin

abstract class ModalWindow(title: String = ""): Window(title, Scene2DSkin.defaultSkin), KTable {
//    private var centerOnAdd = false

    init {
        debugAll()
        padTop(30f) // title height
        setSize(300f, 500f)
        setPosition((Constants.STAGE_WIDTH-width)/2f, (Constants.stageHeight-height)/2f)
        isModal = true
        titleLabel.setAlignment(Align.center)
        titleLabel.setFontScale(0.2f)
//        centerWindow()
        hide()
    }

    fun show() {
        isVisible = true
        GameEngine.isPaused = true
    }

    fun hide() {
        isVisible = false
        GameEngine.isPaused = false
    }

//    override fun setStage(stage: Stage?) {
//        super.setStage(stage)
//        if (stage != null) {
//            stage.keyboardFocus = this
//            if (centerOnAdd) {
//                centerOnAdd = false
//                moveToCenter()
//            }
//        }
//    }
//
//    private fun centerWindow(): Boolean {
//        return if (parent == null) {
//            centerOnAdd = true
//            false
//        } else {
//            moveToCenter()
//            true
//        }
//    }
//
//    private fun moveToCenter() {
//        if (stage != null) setPosition((stage.width-width)/2f, (stage.height-height)/2f)
//    }
}