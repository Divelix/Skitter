package com.divelix.skitter

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

object Data {
    val playerData = PlayerData(Array(arrayOf(100f, 100f, 10f)), Array(arrayOf(1f, 1f, 1f, 1f, 1f)))
    val dynamicData = DynamicData(Vector2(), Vector2(), 10, Array(10))
}

data class DynamicData(val camPos: Vector2,
                       val dirVec: Vector2,
                       var ammo: Int,
                       val aims: Array<Vector2>)

data class PlayerData(var ship: Array<Float>,
                      var gun: Array<Float>)