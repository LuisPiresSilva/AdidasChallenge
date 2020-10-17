package com.adidas.luissilva.ui.widgets

import android.view.View
import androidx.databinding.BindingAdapter
import com.adidas.luissilva.utils.helper.extensions.showCondition

object DataBindingAdapter {

    @JvmStatic
    @BindingAdapter("bind:show")
    fun show(view: View, show: Boolean) {
        view.showCondition(show)
    }

}