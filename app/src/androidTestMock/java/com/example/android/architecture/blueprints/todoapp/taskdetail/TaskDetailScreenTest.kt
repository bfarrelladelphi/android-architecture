package com.example.android.architecture.blueprints.todoapp.taskdetail

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TestUtils
import com.example.android.architecture.blueprints.todoapp.data.FakeTasksRemoteDataSource
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import org.hamcrest.core.IsNot.not
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
class TaskDetailScreenTest {

    companion object {

        private const val TASK_TITLE = "ATSL"

        private const val TASK_DESCRIPTION = "Rocks"

        private val ACTIVE_TASK = Task(TASK_TITLE, TASK_DESCRIPTION, completed = false)

        private val COMPLETED_TASK = Task(TASK_TITLE, TASK_DESCRIPTION, completed = true)

    }

    @get:Rule val activityTestRule = ActivityTestRule<TaskDetailActivity>(TaskDetailActivity::class.java, true, false)

    @Test fun activeTaskDetails_Displayed() {
        loadActiveTask()

        onView(withId(R.id.taskDetailTitle)).check(matches(withText(TASK_TITLE)))
        onView(withId(R.id.taskDetailDescription)).check(matches(withText(TASK_DESCRIPTION)))
        onView(withId(R.id.taskDetailComplete)).check(matches(not(isChecked())))
    }

    @Test fun completedTaskDetails_Displayed() {
        loadCompletedTask()

        onView(withId(R.id.taskDetailTitle)).check(matches(withText(TASK_TITLE)))
        onView(withId(R.id.taskDetailDescription)).check(matches(withText(TASK_DESCRIPTION)))
        onView(withId(R.id.taskDetailComplete)).check(matches(isChecked()))
    }

    @Test fun orientationChange_menuAndTaskPersist() {
        loadActiveTask()

        onView(withId(R.id.action_delete)).check(matches(isDisplayed()))

        TestUtils.rotateOrientation(activityTestRule.activity)

        onView(withId(R.id.taskDetailTitle)).check(matches(withText(TASK_TITLE)))
        onView(withId(R.id.taskDetailDescription)).check(matches(withText(TASK_DESCRIPTION)))

        onView(withId(R.id.action_delete)).check(matches(isDisplayed()))
    }

    private fun loadActiveTask() {
        startActivityWithStubbedTask(ACTIVE_TASK)
    }

    private fun loadCompletedTask() {
        startActivityWithStubbedTask(COMPLETED_TASK)
    }

    private fun startActivityWithStubbedTask(task: Task) {
        TasksRepository.destroyInstance()
        FakeTasksRemoteDataSource.getInstance().addTasks(task)

        val intent = Intent()
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.id)
        activityTestRule.launchActivity(intent)
    }

}