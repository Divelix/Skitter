package com.divelix.skitter.ui

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.divelix.skitter.Assets

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
        val bg = Image(assets.bgDrawable).apply { setFillParent(true) }
        val aspectRatio = iconTexture.width.toFloat() / iconTexture.height.toFloat()
        val icon = Image(iconTexture).apply {
            setSize(iconHeight * aspectRatio, iconHeight)
            setPosition(w/2 - width/2, h/2 - height/2)
        }
        addActor(bg.apply { touchable = Touchable.disabled })
        addActor(icon.apply { touchable = Touchable.disabled })

        addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                (children[0] as Image).drawable = assets.downDrawable
                onClick()
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                (children[0] as Image).drawable = assets.bgDrawable
                super.touchUp(event, x, y, pointer, button)
            }
        })
    }
}