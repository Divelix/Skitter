package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.utils.DamageLabelProvider

class DamageLabelComponent: Component, Pool.Poolable {
    val damageLabels = Array<DamageLabelProvider.DamageLabel>()

    override fun reset() {}
}