package com.adidas.luissilva.ui.screens.goals

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.adidas.luissilva.app.GLOBAL_CALLS_INTERVAL
import com.adidas.luissilva.database.dao.GoalsDAO
import com.adidas.luissilva.database.model.Goal
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class GoalsViewModel @Inject constructor(
        private val goalsDAO: GoalsDAO,

) : ViewModel() {

    var fetchGoalsTime = LocalDateTime.now().minusHours(GLOBAL_CALLS_INTERVAL)

    val goals = MutableLiveData<List<Goal>>(emptyList())




    private val dbGoals = goalsDAO.getAllGoals()
    private val goalsObserver = Observer<List<Goal>> {
        it?.let {
            goals.postValue(it)
        }
    }

    init {
        dbGoals.observeForever(goalsObserver)
    }




    override fun onCleared() {
        super.onCleared()
        dbGoals.removeObserver(goalsObserver)
    }
}