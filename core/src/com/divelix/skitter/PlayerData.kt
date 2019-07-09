package com.divelix.skitter

import com.badlogic.gdx.utils.Array

data class PlayerData(var name: String = "DefaultName",
                      var level: Int = 1,
                      var coins: Int = 0,
                      var hp: Int = 100,
                      var mods: Array<Mod>)