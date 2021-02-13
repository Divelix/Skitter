package com.divelix.skitter.ui.menu.equip

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Scaling
import com.divelix.skitter.data.Constants
import com.divelix.skitter.data.RegionName
import ktx.actors.onClick
import ktx.scene2d.KGroup
import ktx.scene2d.Scene2DSkin
import ktx.style.get

class ActionButton(action: () -> Unit): Container<Image>(), KGroup {

    init {
        isVisible = false
        size(326f, 50f)
        touchable = Touchable.enabled
        background = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
        actor = Image(Scene2DSkin.defaultSkin.get<TextureRegion>(RegionName.MOVE_UP_ICON()))
                .apply {
                    setScaling(Scaling.fit)
                }
        onClick { action() }
    }
}