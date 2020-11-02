package com.adidas.luissilva

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.adidas.luissilva.database.dao.GoalsDAO
import com.adidas.luissilva.database.model.Goal
import com.adidas.luissilva.database.model.Reward
import com.adidas.luissilva.ui.screens.goalDetails.GoalDetailState
import com.adidas.luissilva.ui.screens.goalDetails.GoalDetailViewModel
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GoalDetailVMUnitTest {

    @Mock
    private lateinit var context: Application

    @Mock
    private lateinit var goalsDAO: GoalsDAO

    @InjectMocks
    private lateinit var viewModel: GoalDetailViewModel


    private lateinit var goalDetailStateObserver: Observer<GoalDetailState>
    private lateinit var goalObserver: Observer<Goal>

    private var goalDataNull: Goal? = null
    private var goalData: Goal = createGoal1()
    private fun createGoal1() = Goal(
            id = "id1",
            description = "description",
            goal = 1,
            reward = Reward(trophy = "trophy", points = 1),
            title = "Goal 1",
            type = "step"
    )


    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        `when`(context.applicationContext).thenReturn(context)

        mockData()
        setupObservers()
    }


    @Test
    fun `initial state`() {
        with(viewModel) {
            goalDetailState.observeForever(goalDetailStateObserver)
            goal.observeForever(goalObserver)
        }


        assertNotNull(viewModel.goalDetailState.value)
        assert(viewModel.goalDetailState.value is GoalDetailState.Loading)

    }



    private fun setupObservers() {
        goalDetailStateObserver = mock(Observer::class.java) as Observer<GoalDetailState>
        goalObserver = mock(Observer::class.java) as Observer<Goal>

    }

    private fun mockData() {

    }


}