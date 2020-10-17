package com.adidas.luissilva.database.base

import androidx.room.*

/*
Only use base in very basic models
 */
abstract class BaseDAO<T> {

    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(obj: T) : Long

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(vararg obj: T)

    /**
     * Insert an array of objects in the database.
     *
     * @param obj: List<T> the objects to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(obj: List<T>) : List<Long>

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    abstract fun update(obj: T) : Int

    /**
     * Update an object from the database.
     *
     * @param obj: List<T> the object to be updated
     */
    @Update
    abstract fun update(obj: List<T>) : Int

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    abstract fun delete(obj: T) : Int


}