package com.divelix.skitter

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.divelix.skitter.data.Constants
import ktx.scene2d.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.scaledLabel(
        text: CharSequence = "",
        scale: Float = Constants.DEFAULT_LABEL_SCALE,
        style: String = defaultStyle,
        skin: Skin = Scene2DSkin.defaultSkin,
        init: (@Scene2dDsl Label).(S) -> Unit = {}
): Label {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(Label(text, skin, style).apply { setFontScale(scale) }, init)
}

// delete when new version of libktx comes out
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.image(
        texture: Texture,
        init: (@Scene2dDsl Image).(S) -> Unit = {}
): Image {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(Image(texture), init)
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.image(
        drawable: Drawable,
        init: (@Scene2dDsl Image).(S) -> Unit = {}
): Image {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(Image(drawable), init)
}