package com.divelix.skitter.utils

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.Constants
import com.divelix.skitter.GameEngine
import com.divelix.skitter.components.DamageLabelComponent
import com.divelix.skitter.components.TransformComponent
import ktx.actors.along
import ktx.actors.plusAssign
import ktx.actors.then
import ktx.actors.txt
import ktx.ashley.get

class DamageLabelProvider(val hudStage: Stage, val playCam: OrthographicCamera) {

    val damageLabelsPool = DamageLabelsPool()
    val temp = Vector3()

    fun makeDamageLabel(damage: Float, damagedEntity: Entity) {
        damageLabelsPool.obtain().run {
            txt = "${damage.toInt()}"
            pack()
            temp.set(damagedEntity[TransformComponent.mapper]!!.position)
            playCam.project(temp)
            val ratio = Constants.D_WIDTH / Gdx.graphics.width.toFloat()
            temp.scl(ratio)
            temp.x -= width / 2f // center label
            prevPos.set(temp.x, temp.y)
            setPosition(temp.x, temp.y)
            hudStage += this
            damagedEntity[DamageLabelComponent.mapper]!!.damageLabels.add(this)
            animate()
        }
    }

    inner class DamageLabelsPool(initialCapacity: Int = 10, max: Int = 20): Pool<DamageLabel>(initialCapacity, max) {
        override fun newObject(): DamageLabel {
            return DamageLabel()
        }
    }

    inner class DamageLabel: ScaledLabel("", "damage-label"), Pool.Poolable {
        val duration = 1f
        var ecsTimer = duration
        val prevPos = Vector2()
        val latestPos = Vector3()
        private val shift = Vector2()

        override fun reset() {
            ecsTimer = duration
        }

        override fun act(delta: Float) {
            moveTo(latestPos)
            super.act(delta)
        }

        fun animate() {
            val removeAction = Actions.run {
                remove()
                damageLabelsPool.free(this)
            }
            val alphaAnim = Actions.alpha(0f) then Actions.fadeIn(duration / 2f) then Actions.fadeOut(duration / 2f)
            val moveAnim = Actions.moveBy(0f, 40f, duration)
            val removeAnim = Actions.delay(duration) then removeAction
            this += alphaAnim along moveAnim along removeAnim
        }

        private fun moveTo(point: Vector3) {
            temp.set(point)
            playCam.project(temp)
            val ratio = Constants.D_WIDTH / Gdx.graphics.width.toFloat()
            temp.scl(ratio)
            temp.x -= width / 2f // center label

            shift.set(temp.x, temp.y).sub(prevPos)
            moveBy(shift.x, shift.y)
            prevPos.set(temp.x, temp.y)
        }
    }
}