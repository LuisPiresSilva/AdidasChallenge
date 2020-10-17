package com.adidas.luissilva.network.models.response.goals


import com.fasterxml.jackson.annotation.JsonProperty

data class GoalRes(
        @JsonProperty("description")
    val description: String,
        @JsonProperty("goal")
    val goal: Int,
        @JsonProperty("id")
    val id: String,
        @JsonProperty("reward")
    val rewardRes: RewardRes,
        @JsonProperty("title")
    val title: String,
        @JsonProperty("type")
    val type: String
)