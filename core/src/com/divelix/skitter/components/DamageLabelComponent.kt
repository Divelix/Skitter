package com.divelix.skitter.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import com.divelix.skitter.ui.Hud
import com.divelix.skitter.utils.DamageLabelProvider
import ktx.ashley.mapperFor

class DamageLabelComponent: Component, Pool.Poolable {
    val damageLabels = Array<DamageLabelProvider.DamageLabel>()

    override fun reset() {
        damageLabels.clear()
    }

    companion object {
        val mapper = mapperFor<DamageLabelComponent>()
    }
}