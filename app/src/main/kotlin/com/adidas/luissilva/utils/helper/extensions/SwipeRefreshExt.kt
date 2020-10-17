package com.adidas.luissilva.utils.helper.extensions

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

fun SwipeRefreshLayout.stopRefreshing() {
    if (isRefreshing) {
        isRefreshing = false
    }
}