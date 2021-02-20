package com.divelix.skitter.utils

import com.divelix.skitter.data.*
import ktx.assets.toLocalFile
import ktx.json.fromJson

object AliasBinder {
    val equipsData = JsonProcessor.fromJson<EquipsData>("json/equips.json".toLocalFile())
    val modsData = JsonProcessor.fromJson<ModsData>("json/mods.json".toLocalFile())

    fun getEquip(equipAlias: EquipAlias): Equip {
        return equipsData.equips.single { it.type == equipAlias.type && it.index == equipAlias.index }
    }

    fun getMod(modAlias: ModAlias): Mod {
        return modsData.mods.single { it.type == modAlias.type && it.index == modAlias.index }
    }
}