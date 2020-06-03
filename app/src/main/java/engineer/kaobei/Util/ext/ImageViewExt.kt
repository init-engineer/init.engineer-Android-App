package engineer.kaobei.Util.ext

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * Created by Kimi.Peng on 2020/6/3.
 */
fun ImageView.viewLoading(resource: String) {
    Glide.with(this)
        .load(resource)
        .into(this)
}

fun ImageView.viewLoading(drawable: Drawable?) {
    Glide.with(this)
        .load(drawable)
        .into(this)
}

fun ImageView.viewLoadingWithTransition(
    resource: String, drawableTransitionOptions: DrawableTransitionOptions = DrawableTransitionOptions.withCrossFade()
) {
    Glide.with(this)
        .load(resource)
        .transition(drawableTransitionOptions)
        .into(this)
}

fun ImageView.viewLoadingWithTransition(
    drawable: Drawable?, drawableTransitionOptions: DrawableTransitionOptions = DrawableTransitionOptions.withCrossFade()
) {
    Glide.with(this)
        .load(drawable)
        .transition(drawableTransitionOptions)
        .into(this)
}

