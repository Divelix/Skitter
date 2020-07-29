package com.divelix.skitter.data

import com.badlogic.gdx.utils.ObjectMap

data class Mod(val index: Int, val name: String, var level: Int, var quantity: Int = 0, val effects: ObjectMap<String, Float>? = null)// TODO add isShip (for icon choosing)