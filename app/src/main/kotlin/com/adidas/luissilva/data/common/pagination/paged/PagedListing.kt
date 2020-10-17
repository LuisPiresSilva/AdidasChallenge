package com.adidas.luissilva.data.common.pagination.paged

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.adidas.luissilva.data.common.NetworkState

data class PagedListing<M, T>(
        override val networkState: LiveData<NetworkState>,
        override val initialState: LiveData<NetworkState>,
        override val refresh: () -> Unit,
        override val retry: () -> Unit,

        val meta: MutableLiveData<M>,
        // the LiveData of paged lists for the UI to observe
        val list: LiveData<PagedList<T>>
) : Listing<M, T>