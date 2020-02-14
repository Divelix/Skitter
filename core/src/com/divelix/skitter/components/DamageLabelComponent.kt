package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array
import com.divelix.skitter.ui.Hud

class DamageLabelComponent: Component {
    val damageLabels = Array<Hud.DamageLabel>()
}