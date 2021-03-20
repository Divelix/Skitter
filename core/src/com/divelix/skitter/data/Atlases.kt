package com.divelix.skitter.data

import java.util.*

enum class Atlases {
    UI, EQUIPS, MODS, GAMEPLAY, ENEMIES;

    operator fun invoke() = toString().toLowerCase(Locale.ROOT)
}