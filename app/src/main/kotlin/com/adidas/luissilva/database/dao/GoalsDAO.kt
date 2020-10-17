package com.adidas.luissilva.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.adidas.luissilva.database.base.BaseDAO
import com.adidas.luissilva.database.model.Goal

@Dao
abstract class GoalsDAO : BaseDAO<Goal>() {

    @Query("SELECT * FROM goal")
    abstract fun getAllGoals(): LiveData<List<Goal>>

    @Query("SELECT * FROM goal WHERE id =:id")
    abstract fun getGoal(id: String): LiveData<Goal>




}