package com.divelix.skitter.ui.menu.scroll

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.*
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.menu.ModView
import com.divelix.skitter.ui.menu.tabs.Tab
import com.divelix.skitter.ui.menu.tabs.TabbedMenu
import com.divelix.skitter.ui.menu.stockTable
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ktx.scene2d.*
import ktx.style.get

class ModPage(context: Context, val playerData: PlayerData) : Page(context), ModSelector {
    override var selectedModView: ModView? = null
        set(value) {
            field = value
            storeTable.setMod(value)
        }
    private val storeTable by lazy { ShowcaseTable() }

    init {
        val tabbedMenu = TabbedMenu(gdxArrayOf(
                Tab(assets.manager.get(Constants.SHIP_ICON), stockTable(ModType.SHIP_MOD, playerData.mods, ::selectMod)),
                Tab(assets.manager.get(Constants.GUN_ICON), stockTable(ModType.GUN_MOD, playerData.mods, ::selectMod))
        ))
        table {
            setFillParent(true)
            top()
            defaults().expandX()
            table {
                right().pad(12f)
                background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                scaledLabel("2500", style = "mod-name")
            }.cell(fillX = true)
            row()
            add(this@ModPage.storeTable)
            row()
            add(tabbedMenu)
        }
    }

    override fun update() {
//        nameLabel.setText(playerData.name)
    }
}