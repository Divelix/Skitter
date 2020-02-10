package com.divelix.skitter.utils

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.Data
import com.kotcrab.vis.ui.VisUI
import ktx.actors.along
import ktx.actors.plusAssign
import ktx.actors.then

class DamageLabel: Label("", VisUI.getSkin()), Pool.Poolable {
    val duration = 1f
    var ecsTimer = duration
    val prevPos = Vector2()
    private val shift = Vector2()

    override fun reset() {
        ecsTimer = duration
    }

    fun animate() {
        val removeAction = Actions.run {
            remove()
            Data.damageLabelsPool.free(this)
        }
        val alphaAnim = Actions.alpha(0f) then Actions.fadeIn(duration / 2f) then Actions.fadeOut(duration / 2f)
        val moveAnim = Actions.moveBy(0f, 40f, duration)
        val removeAnim = Actions.delay(duration) then removeAction
        this += alphaAnim along moveAnim along removeAnim
    }

    fun moveTo(nextPos: Vector2) {
        shift.set(nextPos).sub(prevPos)
        moveBy(shift.x, shift.y)
        prevPos.set(nextPos)
    }
}