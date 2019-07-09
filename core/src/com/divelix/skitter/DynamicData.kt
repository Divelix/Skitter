package com.divelix.skitter

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array


data class DynamicData(val camPos: Vector2,
                       val dirVec: Vector2,
                       var ammo: Int,
                       val aims: Array<Vector2>)