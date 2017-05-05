package com.example.android.architecture.blueprints.todoapp.data

import android.support.test.InstrumentationRegistry
import android.support.test.filters.LargeTest
import android.support.test.runner.AndroidJUnit4
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.mockito.Mockito


/**
 * Created by bfarrell on May 04, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksLocalDataSourceTest {

    companion object {

        private const val TITLE = "title"

        private const val TITLE2 = "title2"

        private const val TITLE3 = "title3"

    }

    private var localDataSource: TasksLocalDataSource? = null

    @Before fun setup() {
        localDataSource = TasksLocalDataSource.getInstance(InstrumentationRegistry.getTargetContext())
    }

    @After fun cleanUp() {
        localDataSource?.deleteAllTasks()
    }

    @Test fun testPreconditions() {
        Assert.assertNotNull(localDataSource)
    }

    @Test fun saveTask_retrievesTask() {
        val newTask = Task(TITLE, "")

        localDataSource?.saveTask(newTask)
        localDataSource?.getTask(newTask.id, object : TasksDataSource.GetTaskCallback {
            override fun onTaskLoaded(task: Task) {
                Assert.assertThat(task, Is.`is`(newTask))
            }

            override fun onDataNotAvailable() {
                Assert.fail("Callback error")
            }
        })
    }

    @Test fun completeTask_retrievedTaskIsComplete() {
        val callback = Mockito.mock(TasksDataSource.GetTaskCallback::class.java)

        val newTask = Task(TITLE, "")
        localDataSource?.saveTask(newTask)

        localDataSource?.completeTask(newTask)

        localDataSource?.getTask(newTask.id, callback)

        Mockito.verify(callback, Mockito.never()).onDataNotAvailable()
        Mockito.verify(callback).onTaskLoaded(newTask)

        Assert.assertThat(newTask.completed, Is.`is`(true))
    }

    @Test fun activateTask_retrievedTaskIsActive() {
        val callback = Mockito.mock(TasksDataSource.GetTaskCallback::class.java)

        val newTask = Task(TITLE, "")
        localDataSource?.saveTask(newTask)
        localDataSource?.completeTask(newTask)

        localDataSource?.activateTask(newTask)

        localDataSource?.getTask(newTask.id, callback)

        Mockito.verify(callback, Mockito.never()).onDataNotAvailable()
        Mockito.verify(callback).onTaskLoaded(newTask)

        Assert.assertThat(newTask.completed, Is.`is`(false))
    }

    @Test fun clearCompletedTask_taskNotRetrievable() {
        val callback1 = Mockito.mock(TasksDataSource.GetTaskCallback::class.java)
        val callback2 = Mockito.mock(TasksDataSource.GetTaskCallback::class.java)
        val callback3 = Mockito.mock(TasksDataSource.GetTaskCallback::class.java)

        val newTask1 = Task(TITLE, "")
        localDataSource?.saveTask(newTask1)
        localDataSource?.completeTask(newTask1)

        val newTask2 = Task(TITLE2, "")
        localDataSource?.saveTask(newTask2)
        localDataSource?.completeTask(newTask2)

        val newTask3 = Task(TITLE3, "")
        localDataSource?.saveTask(newTask3)
        localDataSource?.completeTask(newTask3)

        localDataSource?.clearCompletedTasks()

        localDataSource?.getTask(newTask1.id, callback1)
        Mockito.verify(callback1).onDataNotAvailable()
        Mockito.verify(callback1, Mockito.never()).onTaskLoaded(newTask1)

        localDataSource?.getTask(newTask2.id, callback2)
        Mockito.verify(callback2).onDataNotAvailable()
        Mockito.verify(callback2, Mockito.never()).onTaskLoaded(newTask2)

        localDataSource?.getTask(newTask3.id, callback3)
        Mockito.verify(callback3).onDataNotAvailable()
        Mockito.verify(callback3, Mockito.never()).onTaskLoaded(newTask3)
    }

    @Test fun deleteAllTasks_emptyListOfRetrievedTasks() {
        val newTask = Task(TITLE, "")
        localDataSource?.saveTask(newTask)
        val callback = Mockito.mock(TasksDataSource.LoadTasksCallback::class.java)

        localDataSource?.deleteAllTasks()

        localDataSource?.getTasks(callback)
        Mockito.verify(callback).onDataNotAvailable()
        Mockito.verify(callback, Mockito.never()).onTasksLoaded(Matchers.anyListOf(Task::class.java))
    }

    @Test fun getTasks_retrieveSavedTasks() {
        val newTask1 = Task(TITLE, "")
        localDataSource?.saveTask(newTask1)
        val newTask2 = Task(TITLE2, "")
        localDataSource?.saveTask(newTask2)

        localDataSource?.getTasks(object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                Assert.assertNotNull(tasks)
                Assert.assertTrue(tasks.size >= 2)

                var newTask1IdFound = false
                var newTask2IdFound = false

                for (task in tasks) {
                    if (task.id == newTask1.id) {
                        newTask1IdFound = true
                    }
                    if (task.id == newTask2.id) {
                        newTask2IdFound = true
                    }
                }

                Assert.assertTrue(newTask1IdFound)
                Assert.assertTrue(newTask2IdFound)
            }

            override fun onDataNotAvailable() {
                Assert.fail()
            }

        })

    }

}