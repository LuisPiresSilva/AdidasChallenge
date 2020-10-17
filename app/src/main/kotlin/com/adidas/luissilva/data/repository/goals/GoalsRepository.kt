package com.adidas.luissilva.data.repository.goals


import com.adidas.luissilva.data.common.CallResult
import com.adidas.luissilva.network.models.response.goals.GoalsRes
import javax.inject.Singleton

@Singleton
interface GoalsRepository {

    fun fetchGoalsSync(): CallResult<GoalsRes>


}