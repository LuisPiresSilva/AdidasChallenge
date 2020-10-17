package com.adidas.luissilva.network.models.response.goals


import com.fasterxml.jackson.annotation.JsonProperty

data class GoalsRes(
        @JsonProperty("items")
    val goalRes: List<GoalRes>,
        @JsonProperty("nextPageToken")
    val nextPageToken: String
)