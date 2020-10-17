package com.adidas.luissilva.data.repository.goals

import com.adidas.luissilva.data.common.SynchronousNetworkBoundResource
import com.adidas.luissilva.data.converters.toGoals
import com.adidas.luissilva.database.AdidasDB
import com.adidas.luissilva.network.ApiProvider
import com.adidas.luissilva.network.models.response.goals.GoalsRes
import com.adidas.luissilva.utils.authentication.AccountUtils
import timber.log.Timber

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalsRepositoryImpl @Inject constructor(
        private val accountUtils: AccountUtils,
        private val apiProvider: ApiProvider,
        private val adidasDB: AdidasDB
) : GoalsRepository {



    override fun fetchGoalsSync() = object : SynchronousNetworkBoundResource<GoalsRes, Void>(accountUtils) {
        override fun createCall() = apiProvider.goalsSync()

        override fun saveCallResult(data: GoalsRes?) {
            data?.goalRes?.toGoals()?.let {
                adidasDB.GoalsDAO().insert(it)
            }
        }
    }.initCall()

}