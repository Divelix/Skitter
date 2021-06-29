package com.divelix.skitter.data.binders

import com.divelix.skitter.data.*
import com.divelix.skitter.data.JsonProcessor
import ktx.assets.toInternalFile
import ktx.json.fromJson

object AliasBinder {
    val equipsData = JsonProcessor.fromJson<EquipsData>("json/equips.json".toInternalFile())
    val modsData = JsonProcessor.fromJson<ModsData>("json/mods.json".toInternalFile())

    fun getEquip(equipAlias: EquipAlias): Equip {
        return equipsData.equips.single { it.type == equipAlias.type && it.index == equipAlias.index }
    }

    fun getMod(modAlias: ModAlias): Mod {
        return modsData.mods.single { it.type == modAlias.type && it.index == modAlias.index }
    }
}