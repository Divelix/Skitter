package com.divelix.skitter.ui.legacy

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Constants
import ktx.scene2d.Scene2DSkin
import ktx.style.get

class ImgBgButton(
        assets: Assets,
        iconTexture: Texture,
        w: Float = 136f,
        h: Float = 50f,
        iconHeight: Float = 35f,
        onClick: () -> Unit
): Group() {

    init {
        touchable = Touchable.enabled
        setSize(w, h)
        val bg = Image(TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))).apply { setFillParent(true) }
        val aspectRatio = iconTexture.width.toFloat() / iconTexture.height.toFloat()
        val icon = Image(iconTexture).apply {
            setSize(iconHeight * aspectRatio, iconHeight)
            setPosition(w/2 - width/2, h/2 - height/2)
        }
        addActor(bg.apply { touchable = Touchable.disabled })
        addActor(icon.apply { touchable = Touchable.disabled })

        addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                (children[0] as Image).drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_50))
                onClick()
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                (children[0] as Image).drawable = TextureRegionDrawable(Scene2DSkin.defaultSkin.get<Texture>(Constants.BLACK_PIXEL_30))
                super.touchUp(event, x, y, pointer, button)
            }
        })
    }
}