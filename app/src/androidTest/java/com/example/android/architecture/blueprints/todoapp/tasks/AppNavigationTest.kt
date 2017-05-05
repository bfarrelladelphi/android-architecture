package com.example.android.architecture.blueprints.todoapp.tasks

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.NoActivityResumedException
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerActions.open
import android.support.test.espresso.contrib.DrawerMatchers.isClosed
import android.support.test.espresso.contrib.NavigationViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.view.GravityCompat
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TestUtils
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by bfarrell on May 04, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class AppNavigationTest {

    @get:Rule val activityTestRule = ActivityTestRule<TasksActivity>(TasksActivity::class.java)

    @Test fun clickOnStatisticsNavigationItem_showsStatisticsScreen() {
        openStatisticsScreen()
        onView(withId(R.id.statistics))
                .check(matches(isDisplayed()))
    }

    @Test fun clickOnListNavigationItem_showsListScreen() {
        openStatisticsScreen()
        openTasksScreen()
        onView(withId(R.id.tasksContainer))
                .check(matches(isDisplayed()))
    }

    @Test fun clickOnAndroidHomeIcon_opensNavigation() {
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(GravityCompat.START)))

        onView(withContentDescription(TestUtils
                .getToolbarNavigationContentDescription(activityTestRule.activity, R.id.toolbar)))
                .perform(click())
    }

    @Test fun backFromStatistics_navigatesToTasks() {
        openStatisticsScreen()
        pressBack()
        onView(withId(R.id.tasksContainer)).check(matches(isDisplayed()))
    }

    @Test fun backFromTasks_exitsApp() {
        assertPressingBackExitsApp()
    }

    @Test fun backFromTasksAfterStatistics_exitsApp() {
        openStatisticsScreen()
        openTasksScreen()
        assertPressingBackExitsApp()
    }

    private fun assertPressingBackExitsApp() {
        try {
            pressBack()
            Assert.fail("Should kill the app and throw an exception")
        } catch (exception: NoActivityResumedException) {
            // Test succeeded
        }
    }

    private fun openTasksScreen() {
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(GravityCompat.START)))
                .perform(open())

        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.action_list))
    }

    private fun openStatisticsScreen() {
        onView(withId(R.id.drawerLayout))
                .check(matches(isClosed(GravityCompat.START)))
                .perform(open())

        onView(withId(R.id.navigationView))
                .perform(NavigationViewActions.navigateTo(R.id.action_statistics))
    }

}