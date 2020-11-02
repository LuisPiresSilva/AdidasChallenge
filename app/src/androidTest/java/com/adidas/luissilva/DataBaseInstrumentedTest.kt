package com.adidas.luissilva

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.adidas.luissilva.database.AdidasDB
import com.adidas.luissilva.database.dao.GoalsDAO
import com.adidas.luissilva.database.model.Goal
import com.adidas.luissilva.database.model.Reward
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DataBaseInstrumentedTest {
    private lateinit var goalsDao: GoalsDAO
    private lateinit var db: AdidasDB


    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AdidasDB::class.java).build()
        goalsDao = db.GoalsDAO()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }




    //we only test synchronous queries since async queries (observers) should be duplicated

    @Test
    fun testGoalsInsertReplace() {
        val goal = createGoal1()

        goalsDao.insert(goal)
        goalsDao.insert(goal.apply {
            description = "updated"
        })

        val dbGoal = goalsDao.getGoal(id1)

        assertThat(dbGoal.description, equalTo("updated"))

        goalsDao.insert(goal)
        goalsDao.insert(goal)
        goalsDao.insert(goal)

        val dbGoals = goalsDao.getAllGoals()
        assertThat(dbGoals.size, equalTo(1))
    }



    @Test
    fun testGoalsGetters() {
        val goal = createGoal1()

        goalsDao.insert(goal)

        val byID = goalsDao.getGoal(id1)

        assertThat(byID, equalTo(goal))

        goalsDao.insert(createGoal2())


        val dbGoals = goalsDao.getAllGoals()
        assertThat(dbGoals, equalTo(listOf(goal, createGoal2())))
    }


    @Test
    fun testGoalsUpdate() {
        val goal = createGoal1()

        goalsDao.insert(goal)
        goalsDao.update(goal.apply {
            description = "updated"
        })

        val dbGoal = goalsDao.getGoal(id1)

        assertThat(dbGoal.description, equalTo("updated"))
    }



    @Test
    fun testGoalsDelete() {
        goalsDao.insert(createGoal1())
        goalsDao.insert(createGoal2())

        assertThat(goalsDao.getAllGoals().size, equalTo(2))

        goalsDao.delete(createGoal1())
        goalsDao.delete(createGoal2())

        assertThat(goalsDao.getAllGoals().size, equalTo(0))
    }


    @Test
    fun testClearDB() {
        goalsDao.insert(createGoal1())
        goalsDao.insert(createGoal2())

        assertThat(goalsDao.getAllGoals().size, equalTo(2))

        db.deleteDatabase(false)

        assertThat(goalsDao.getAllGoals().size, equalTo(0))
    }




    private val id1 = "id1"
    private fun createGoal1() = Goal(
            id = id1,
            description = "description",
            goal = 1,
            reward = Reward(trophy = "trophy", points = 1),
            title = "Goal 1",
            type = "step"
    )

    private val id2 = "id2"
    private fun createGoal2() = Goal(
            id = id2,
            description = "description",
            goal = 1,
            reward = Reward(trophy = "trophy", points = 1),
            title = "Goal 2",
            type = "step"
    )
}