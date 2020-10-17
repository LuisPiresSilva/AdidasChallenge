package com.adidas.luissilva.network.models.response.goals


import com.fasterxml.jackson.annotation.JsonProperty

data class RewardRes(
    @JsonProperty("points")
    val points: Int,
    @JsonProperty("trophy")
    val trophy: String
)