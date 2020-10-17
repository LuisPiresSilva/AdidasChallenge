package com.adidas.luissilva.ui.base.adapter

import com.adidas.luissilva.data.common.NetworkState

interface IPagedListAdapter {

    fun setNetworkState(newNetworkState: NetworkState?)

    fun setPagedList(pagedObjects: MutableList<*>)

    fun setPagedList(pagedObjects: MutableList<*>, onReady: (previousList: List<Any>, currentList: List<Any>) -> Unit = { _, _ -> })

    fun canLoad() : Boolean

    fun canLoad(hasConnection: Boolean) : Boolean
}