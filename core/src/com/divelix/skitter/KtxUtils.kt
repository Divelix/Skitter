package com.divelix.skitter

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.divelix.skitter.data.Assets
import com.divelix.skitter.data.Mod
import com.divelix.skitter.ui.menu.ModIcon
import ktx.scene2d.*
import ktx.style.*
import ktx.style.defaultStyle
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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


// delete when new version of libktx comes out
@Scene2dDsl
class KContainer<T : Actor>(actor: T? = null) : Container<T>(actor), KGroup {
    @Suppress("UNCHECKED_CAST")
    override fun addActor(actor: Actor?) {
        this.actor == null || throw IllegalStateException("Container may store only a single child.")
        this.actor = actor as T
    }
}

// delete when new version of libktx comes out
@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S, A : Actor> KWidget<S>.container(
        actor: A,
        init: KContainer<A>.(S) -> Unit = {}
): KContainer<A> {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(KContainer(actor), init)
}

@SkinDsl
@OptIn(ExperimentalContracts::class)
inline fun Skin.image(
        name: String = defaultStyle,
        init: (@SkinDsl Image).(Image) -> Unit = {}
) {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    val img = Image()
    img.init(img)
    return add(img, name)
}

//@SkinDsl
//@OptIn(ExperimentalContracts::class)
//fun Skin.image(
//        name: String = defaultStyle,
//        drawable: Drawable
//) {
//    val img = Image(drawable)
//    return add(img, name)
//}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.image(
        name: String,
        skin: Skin = Scene2DSkin.defaultSkin,
        init: (@Scene2dDsl Image).(S) -> Unit = {}
): Image {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(skin[name], init)
}