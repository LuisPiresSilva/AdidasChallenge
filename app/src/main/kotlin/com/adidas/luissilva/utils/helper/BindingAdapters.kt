package com.adidas.luissilva.utils.helper

import android.view.View
import androidx.databinding.BindingAdapter
import com.adidas.luissilva.utils.helper.extensions.showCondition

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("bind:show")
    fun show(view: View, show: Boolean) {
        view.showCondition(show, true)
    }

}