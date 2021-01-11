package com.divelix.skitter.ui.scrollmenu

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.*
import com.divelix.skitter.image
import com.divelix.skitter.scaledLabel
import com.divelix.skitter.ui.tabbedmenu.*
import com.divelix.skitter.ui.tabbedmenu.stockTable
import ktx.collections.gdxArrayOf
import ktx.inject.Context
import ktx.scene2d.*
import ktx.style.get

class ModPage(context: Context, val playerData: Player, val modsData: ModsData) : Page(context), ModSelector {
    override var selectedModView: ModView? = null
        set(value) {
            field = value
            storeTable.setMod(value)
        }
    private val storeTable by lazy { StoreTable(modsData) }

    init {
        val tabbedMenu = TabbedMenu(gdxArrayOf(
                Tab(assets.manager.get(Constants.SHIP_ICON), stockTable(playerData.mods, assets, ::selectMod)),
                Tab(assets.manager.get(Constants.GUN_ICON), stockTable(playerData.mods, assets, ::selectMod))
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