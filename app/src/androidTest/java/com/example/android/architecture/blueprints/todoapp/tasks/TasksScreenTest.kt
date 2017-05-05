package com.example.android.architecture.blueprints.todoapp.tasks

import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.widget.ListView
import com.example.android.architecture.blueprints.todoapp.Injection
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.TestUtils
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
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
class TasksScreenTest {

    companion object {

        private const val TITLE1 = "TITLE1"

        private const val DESCRIPTION = "DESCR"

        private const val TITLE2 = "TITLE2"

    }

    @Rule val tasksActivityTestRule = object : ActivityTestRule<TasksActivity>(TasksActivity::class.java) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()

            Injection.provideTasksRepository(InstrumentationRegistry.getTargetContext())
                    .deleteAllTasks()
        }
    }

    private fun withItemText(itemText: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            override fun describeTo(description: Description) {
                description.appendText("is isDescendantOfA LV with text " + itemText)
            }

            override fun matchesSafely(item: View): Boolean {
                return Matchers.allOf(isDescendantOfA(isAssignableFrom(ListView::class.java)),
                        withText(itemText)).matches(item)
            }

        }
    }

    @Test fun clickAddButton_opensAddTask() {
        onView(withId(R.id.addTaskButton)).perform(click())

        onView(withId(R.id.addTaskTitle)).check(matches(isDisplayed()))
    }

    @Test fun editTask() {
        createTask(TITLE1, DESCRIPTION)

        onView(withText(TITLE1)).perform(click())

        onView(withId(R.id.editTaskButton)).perform(click())

        val editTaskTitle = TITLE2
        val editTaskDescription = "New description"

        onView(withId(R.id.addTaskTitle))
                .perform(replaceText(editTaskTitle), closeSoftKeyboard())
        onView(withId(R.id.addTaskDescription))
                .perform(replaceText(editTaskDescription), closeSoftKeyboard())

        onView(withId(R.id.editTaskDoneButton)).perform(click())

        onView(withItemText(editTaskTitle)).check(matches(isDisplayed()))

        onView(withItemText(TITLE1)).check(doesNotExist())
    }

    @Test fun addTaskToTaskList() {
        createTask(TITLE1, DESCRIPTION)

        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
    }

    @Test fun markTaskAsComplete() {
        viewAllTasks()

        createTask(TITLE1, DESCRIPTION)

        clickCheckBoxForTask(TITLE1)

        viewAllTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        viewActiveTasks()
        onView(withItemText(TITLE1)).check(matches(not(isDisplayed())))
        viewCompletedTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
    }

    @Test fun markTaskAsActive() {
        viewAllTasks()

        createTask(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)

        clickCheckBoxForTask(TITLE1)

        viewAllTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        viewActiveTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        viewCompletedTasks()
        onView(withItemText(TITLE1)).check(matches(not(isDisplayed())))
    }

    @Test fun showAllTasks() {
        createTask(TITLE1, DESCRIPTION)
        createTask(TITLE2, DESCRIPTION)

        viewAllTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        onView(withItemText(TITLE2)).check(matches(isDisplayed()))
    }

    @Test fun showActiveTasks() {
        createTask(TITLE1, DESCRIPTION)
        createTask(TITLE2, DESCRIPTION)

        viewActiveTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        onView(withItemText(TITLE2)).check(matches(isDisplayed()))
    }

    @Test fun showCompletedTasks() {
        createTask(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)
        createTask(TITLE2, DESCRIPTION)
        clickCheckBoxForTask(TITLE2)

        viewCompletedTasks()
        onView(withItemText(TITLE1)).check(matches(isDisplayed()))
        onView(withItemText(TITLE2)).check(matches(isDisplayed()))
    }

    @Test fun clearCompletedTasks() {
        viewAllTasks()

        createTask(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)
        createTask(TITLE2, DESCRIPTION)
        clickCheckBoxForTask(TITLE2)

        openActionBarOverflowOrOptionsMenu(getTargetContext())
        onView(withText(R.string.menu_clear)).perform(click())

        onView(withItemText(TITLE1)).check(matches(not(isDisplayed())))
        onView(withItemText(TITLE2)).check(matches(not(isDisplayed())))
    }

    @Test fun createOneTask_deleteTask() {
        viewAllTasks()

        createTask(TITLE1, DESCRIPTION)

        onView(withText(TITLE1)).perform(click())

        onView(withId(R.id.action_delete)).perform(click())

        viewAllTasks()
        onView(withText(TITLE1)).check(matches(not(isDisplayed())))
    }

    @Test fun createTwoTasks_deleteOneTask() {
        createTask(TITLE1, DESCRIPTION)
        createTask(TITLE2, DESCRIPTION)

        onView(withText(TITLE2)).perform(click())

        onView(withId(R.id.action_delete)).perform(click())

        viewAllTasks()
        onView(withText(TITLE1)).check(matches(isDisplayed()))
        onView(withText(TITLE2)).check(doesNotExist())
    }

    @Test fun markTaskAsCompleteInDetail_taskIsCompleteInList() {
        viewAllTasks()

        createTask(TITLE1, DESCRIPTION)

        onView(withText(TITLE1)).perform(click())

        onView(withId(R.id.taskDetailComplete)).perform(click())

        onView(withContentDescription(getToolbarNavigationContentDescription())).perform(click())

        onView(allOf(withId(R.id.complete), hasSibling(withText(TITLE1)))).check(matches(isChecked()))
    }

    @Test fun markTaskAsActiveInDetail_taskIsActiveInList() {
        viewAllTasks()

        createTask(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)

        onView(withText(TITLE1)).perform(click())

        onView(withId(R.id.taskDetailComplete)).perform(click())

        onView(withContentDescription(getToolbarNavigationContentDescription())).perform(click())

        onView(allOf(withId(R.id.complete), hasSibling(withText(TITLE1)))).check(matches(not(isChecked())))
    }

    @Test fun markTaskAsACompleteAndActiveInDetail_taskIsActiveInList() {
        viewAllTasks()

        createTask(TITLE1, DESCRIPTION)

        onView(withText(TITLE1)).perform(click())

        onView(withId(R.id.taskDetailComplete)).perform(click())

        onView(withId(R.id.taskDetailComplete)).perform(click())

        onView(withContentDescription(getToolbarNavigationContentDescription())).perform(click())

        onView(allOf(withId(R.id.complete), hasSibling(withText(TITLE1)))).check(matches(not(isChecked())))
    }

    @Test fun markTaskAsActiveAndCompleteInDetail_taskIsCompleteInList() {
        viewAllTasks()

        createTask(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)

        onView(withText(TITLE1)).perform(click())

        onView(withId(R.id.taskDetailComplete)).perform(click())

        onView(withId(R.id.taskDetailComplete)).perform(click())

        onView(withContentDescription(getToolbarNavigationContentDescription())).perform(click())

        onView(allOf(withId(R.id.complete), hasSibling(withText(TITLE1)))).check(matches(isChecked()))
    }

    @Test fun orientationChange_filterActivePersists() {
        createTask(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)

        viewActiveTasks()

        onView(withText(TITLE1)).check(matches(not(isDisplayed())))

        TestUtils.rotateOrientation(tasksActivityTestRule.activity)

        onView(withText(TITLE1)).check(doesNotExist())
    }

    @Test fun orientationChange_filterCompletedPersists() {
        createTask(TITLE1, DESCRIPTION)
        clickCheckBoxForTask(TITLE1)

        viewCompletedTasks()

        onView(withText(TITLE1)).check(matches(isDisplayed()))

        TestUtils.rotateOrientation(tasksActivityTestRule.activity)

        onView(withText(TITLE1)).check(matches(isDisplayed()))
        onView(withText(R.string.label_completed)).check(matches(isDisplayed()))
    }

    @Test fun orientationChangeDuringEdit_changePersists() {
        createTask(TITLE1, DESCRIPTION)

        onView(withText(TITLE1)).perform(click())

        onView(withId(R.id.editTaskButton)).perform(click())

        onView(withId(R.id.addTaskTitle)).perform(replaceText(TITLE2), closeSoftKeyboard())

        TestUtils.rotateOrientation(TestUtils.getCurrentActivity())

        onView(withId(R.id.addTaskTitle)).check(matches(withText(TITLE2)))
    }

    @Test fun orientationChangeDuringEdit_noDuplicate() {
        createTask(TITLE1, DESCRIPTION)

        onView(withText(TITLE1)).perform(click())

        onView(withId(R.id.editTaskButton)).perform(click())

        TestUtils.rotateOrientation(TestUtils.getCurrentActivity())

        onView(withId(R.id.addTaskTitle)).perform(replaceText(TITLE2), closeSoftKeyboard())
        onView(withId(R.id.addTaskDescription)).perform(replaceText(DESCRIPTION), closeSoftKeyboard())

        onView(withItemText(TITLE2)).check(matches(isDisplayed()))
        onView(withItemText(TITLE1)).check(doesNotExist())
    }

    private fun viewAllTasks() {
        onView(withId(R.id.action_filter)).perform(click())
        onView(withText(R.string.navigation_all)).perform(click())
    }

    private fun viewActiveTasks() {
        onView(withId(R.id.action_filter)).perform(click())
        onView(withText(R.string.navigation_active)).perform(click())
    }

    private fun viewCompletedTasks() {
        onView(withId(R.id.action_filter)).perform(click())
        onView(withText(R.string.navigation_completed)).perform(click())
    }

    private fun createTask(title: String, description: String) {
        onView(withId(R.id.addTaskButton)).perform(click())

        onView(withId(R.id.addTaskTitle))
                .perform(ViewActions.typeText(title), closeSoftKeyboard())
        onView(withId(R.id.addTaskDescription))
                .perform(ViewActions.typeText(description), closeSoftKeyboard())

        onView(withId(R.id.editTaskDoneButton)).perform(click())
    }

    private fun clickCheckBoxForTask(title: String) {
        onView(allOf(withId(R.id.complete), hasSibling(withText(title)))).perform(click())
    }

    private fun getToolbarNavigationContentDescription(): String {
        return TestUtils.getToolbarNavigationContentDescription(tasksActivityTestRule.activity, R.id.toolbar)
    }

}