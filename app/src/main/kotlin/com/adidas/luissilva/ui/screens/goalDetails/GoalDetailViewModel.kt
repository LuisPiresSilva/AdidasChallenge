package com.adidas.luissilva.ui.screens.goalDetails

import androidx.lifecycle.ViewModel
import com.adidas.luissilva.database.dao.GoalsDAO
import javax.inject.Inject

class GoalDetailViewModel @Inject constructor(
        private val goalsDAO: GoalsDAO,

) : ViewModel() {

    fun getGoal(id: String) = goalsDAO.getGoal(id)

}