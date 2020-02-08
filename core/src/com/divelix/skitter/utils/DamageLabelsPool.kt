package com.divelix.skitter.utils

import com.badlogic.gdx.utils.Pool

class DamageLabelsPool(initialCapacity: Int = 10, max: Int = 20): Pool<DamageLabel>(initialCapacity, max) {
    override fun newObject(): DamageLabel {
        return DamageLabel()
    }

}