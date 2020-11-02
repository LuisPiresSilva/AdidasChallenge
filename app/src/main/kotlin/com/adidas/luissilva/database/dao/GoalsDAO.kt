package com.adidas.luissilva.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.adidas.luissilva.database.base.BaseDAO
import com.adidas.luissilva.database.model.GOAL_TABLE
import com.adidas.luissilva.database.model.Goal

@Dao
abstract class GoalsDAO : BaseDAO<Goal>() {

    @Query("SELECT * FROM goal")
    abstract fun getAllGoals(): List<Goal>

    @Query("SELECT * FROM goal WHERE id =:id")
    abstract fun getGoal(id: String): Goal

    @Query("SELECT * FROM goal")
    abstract fun getAllGoalsObs(): LiveData<List<Goal>>

    @Query("SELECT * FROM goal WHERE id =:id")
    abstract fun getGoalObs(id: String): LiveData<Goal>


    //It depends but this is what i usually use when i want to update by field
    //and make it easier to update by field in the caller
    //(obviously that it requires all fields to be mapped out here)

    /*
    how to use sample:
    updateGoalByProperty(goal, listOf(goal::description.name))
     */
    @Transaction
    open fun updateGoalByProperty(goal: Goal, propertiesToUpdate: List<String>) {
        propertiesToUpdate.forEach {
            when (it) {
                goal::description.name -> { updateDescription(goal.id, goal.description) }
            }
        }
    }

    @Query("UPDATE $GOAL_TABLE SET description = :description WHERE id = :id")
    abstract fun updateDescription(id: String, description: String): Int
}