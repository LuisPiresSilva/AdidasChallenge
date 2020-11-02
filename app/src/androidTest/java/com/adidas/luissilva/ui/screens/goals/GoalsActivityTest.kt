package com.adidas.luissilva.ui.screens.goals


import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.adidas.luissilva.R
import com.adidas.luissilva.app.App
import com.adidas.luissilva.app.DATABASE_NAME
import com.adidas.luissilva.database.AdidasDB
import com.adidas.luissilva.database.model.Goal
import com.adidas.luissilva.database.model.Reward
import com.adidas.luissilva.utils.RecyclerViewMatcher
import com.adidas.luissilva.utils.helper.extensions.isVisible
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class GoalsActivityTest {

    @Rule
    @JvmField
    var mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACTIVITY_RECOGNITION")


    companion object {
        @BeforeClass
        fun clearDatabase() {
            ApplicationProvider.getApplicationContext<App>().deleteDatabase(DATABASE_NAME)
        }
    }

    @Test
    fun goalsActivityInitialStateTest() {
        clearDatabase()

        launchActivity<GoalsActivity>().use { scenario ->
                val recyclerView = onView(
                        allOf(withId(R.id.goals_list),
                                childAtPosition(
                                        withClassName(`is`("androidx.core.widget.NestedScrollView")),
                                        0)))
                recyclerView.check(matches(isDisplayed()))

            //list shows generic view
            onView(withRecyclerView(R.id.goals_list)?.atPosition(0)).check(matches(withId(R.id.generic_view)))

            //generic view is showing loading state
            onView(withRecyclerView(R.id.goals_list)?.atPosition(0)).check { view, noViewFoundException ->
                assert(view.findViewById<ProgressBar>(R.id.loading).isVisible())
            }

        }
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


    //we might receive more data after the request but our first object will be what we inserted here
    @Test
    fun goalsActivityDataStateTest() {
        clearDatabase()
        val db = AdidasDB.getInstance(ApplicationProvider.getApplicationContext<App>())
        db.GoalsDAO().insert(createGoal1())


        launchActivity<GoalsActivity>().use { scenario ->

            val recyclerView = onView(
                    allOf(withId(R.id.goals_list),
                            childAtPosition(
                                    withClassName(`is`("androidx.core.widget.NestedScrollView")),
                                    0)))
            recyclerView.check(matches(isDisplayed()))

            //list shows inserted goal
            onView(withRecyclerView(R.id.goals_list)?.atPosition(0)).check(matches(withId(R.id.item_goal_container)))
            onView(withRecyclerView(R.id.goals_list)?.atPositionOnView(0, R.id.item_goal_description)).check(matches(withText(createGoal1().description)))



        }
    }



    fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher? {
        return RecyclerViewMatcher(recyclerViewId)
    }

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }




}
