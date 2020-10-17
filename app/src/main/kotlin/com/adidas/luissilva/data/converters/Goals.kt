package com.adidas.luissilva.data.converters

import com.adidas.luissilva.database.model.Goal
import com.adidas.luissilva.database.model.Reward
import com.adidas.luissilva.network.models.response.goals.GoalRes
import com.adidas.luissilva.network.models.response.goals.GoalsRes

fun List<GoalRes>.toGoals() =  mapNotNull { it.toGoal() }

fun GoalRes.toGoal() = Goal(
        id = id,
        description = description,
        goal = goal,
        reward = Reward(trophy = rewardRes.trophy, points = rewardRes.points),
        title = title,
        type = type
)