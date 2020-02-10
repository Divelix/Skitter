package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.utils.DamageLabel

class DamageLabelComponent: Component {
    val damageLabels = Array<DamageLabel>()
}