package com.adidas.luissilva.ui.screens.goalDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.adidas.luissilva.database.dao.GoalsDAO
import com.adidas.luissilva.database.model.Goal
import javax.inject.Inject

class GoalDetailViewModel @Inject constructor(
        private val goalsDAO: GoalsDAO,

) : ViewModel() {

    val goalDetailState = MutableLiveData<GoalDetailState>(GoalDetailState.Loading())

    private val _goal = MutableLiveData<Goal>()
    val goal : LiveData<Goal> = _goal

    private val goalObserver = Observer<Goal> {
        _goal.postValue(it)
    }

    private lateinit var goalLivaData :LiveData<Goal>
    fun getGoal(id: String)  {
        if(::goalLivaData.isInitialized) {
            goalLivaData.removeObserver(goalObserver)
        }
        goalLivaData = goalsDAO.getGoalObs(id)
        goalLivaData.observeForever(goalObserver)
    }

    override fun onCleared() {
        goalLivaData.removeObserver(goalObserver)
        super.onCleared()
    }
}



sealed class GoalDetailState {

    abstract fun getStatus(): StatusEnum

    data class Loading(val state: StatusEnum = StatusEnum.LOADING) : GoalDetailState() {
        override fun getStatus() = state
    }

    data class Error(val message: String, val btn_text: String, val action: () -> Unit, val state: StatusEnum = StatusEnum.ERROR) : GoalDetailState() {
        override fun getStatus() = state
    }

    data class Results(val progress: Int, val state: StatusEnum = StatusEnum.RESULTS) : GoalDetailState() {
        override fun getStatus() = state
    }

    enum class StatusEnum {
        LOADING, RESULTS, ERROR
    }
}