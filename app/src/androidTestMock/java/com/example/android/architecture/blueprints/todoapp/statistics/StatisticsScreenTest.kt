package com.example.android.architecture.blueprints.todoapp.statistics

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.FakeTasksRemoteDataSource
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import org.hamcrest.Matchers.containsString
import org.junit.Before
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
class StatisticsScreenTest {

    @get:Rule val activityTestRule = ActivityTestRule<StatisticsActivity>(StatisticsActivity::class.java, true, false)

    @Before fun intentWithStubbedTaskId() {
        TasksRepository.destroyInstance()
        FakeTasksRemoteDataSource.getInstance().addTasks(Task("TITLE1", "", completed = false))
        FakeTasksRemoteDataSource.getInstance().addTasks(Task("TITLE2", "", completed = true))

        val intent = Intent()
        activityTestRule.launchActivity(intent)
    }

    @Test fun tasks_showNonEmptyMessage() {
        val expectedActiveTaskText = InstrumentationRegistry.getTargetContext()
                .getString(R.string.statistics_active_tasks)
        onView(withText(containsString(expectedActiveTaskText))).check(matches(isDisplayed()))

        val expectedCompletedTaskText = InstrumentationRegistry.getTargetContext()
                .getString(R.string.statistics_completed_tasks)
        onView(withText(containsString(expectedCompletedTaskText))).check(matches(isDisplayed()))
    }
}