package com.example.android.architecture.blueprints.todoapp.addedittask

import android.content.Intent
import android.content.res.Resources
import android.support.annotation.IdRes
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.clearText
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.Toolbar
import android.view.View
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TestUtils
import com.example.android.architecture.blueprints.todoapp.data.FakeTasksRemoteDataSource
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import org.hamcrest.Description
import org.hamcrest.Matcher
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
class AddEditTaskScreenTest {

    companion object {

        private const val TASK_ID = "1"

        fun withToolbarTitle(@IdRes resId: Int): Matcher<View> {
            return object : BoundedMatcher<View, Toolbar>(Toolbar::class.java) {
                override fun describeTo(description: Description) {
                    description.appendText("with toolbar title from resource id: ")
                    description.appendValue(resId)
                }

                override fun matchesSafely(toolbar: Toolbar): Boolean {
                    var expectedText = ""
                    try {
                        expectedText = toolbar.resources.getString(resId)
                    } catch (ignored: Resources.NotFoundException) {
                        /* View could be form a context unaware of the resource id. */
                    }
                    return expectedText == toolbar.title
                }
            }
        }

    }

    @get:Rule val activityTestRule = ActivityTestRule<AddEditTaskActivity>(AddEditTaskActivity::class.java, false, false)

    @Test fun emptyTask_isNotSaved() {
        launchNewTaskActivity(null)

        onView(withId(R.id.addTaskTitle)).perform(clearText())
        onView(withId(R.id.addTaskDescription)).perform(clearText())

        onView(withId(R.id.editTaskDoneButton)).perform(click())

        onView(withId(R.id.addTaskTitle)).check(matches(isDisplayed()))
    }

    @Test fun toolbarTitleNewTask_persistsRotation() {
        launchNewTaskActivity(null)

        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.add_task)))

        TestUtils.rotateOrientation(activityTestRule.activity)

        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.add_task)))
    }

    @Test fun toolbarTitleEditTask_persistsRotation() {
        TasksRepository.destroyInstance()
        FakeTasksRemoteDataSource.getInstance().addTasks(Task("TITLE1", "", TASK_ID, false))
        launchNewTaskActivity(TASK_ID)

        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.edit_task)))

        TestUtils.rotateOrientation(activityTestRule.activity)

        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.edit_task)))
    }

    private fun launchNewTaskActivity(taskId: String?) {
        val intent = Intent(InstrumentationRegistry.getInstrumentation().targetContext,
                AddEditTaskActivity::class.java)
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId)
        activityTestRule.launchActivity(intent)
    }

}