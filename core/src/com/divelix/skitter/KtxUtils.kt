package com.divelix.skitter

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.divelix.skitter.data.Constants
import ktx.collections.GdxFloatArray
import ktx.collections.GdxIntArray
import ktx.scene2d.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun gdxFloatArrayOf(vararg elements: Float): GdxFloatArray = GdxFloatArray(elements)
fun gdxIntArrayOf(vararg elements: Int): GdxIntArray = GdxIntArray(elements)

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