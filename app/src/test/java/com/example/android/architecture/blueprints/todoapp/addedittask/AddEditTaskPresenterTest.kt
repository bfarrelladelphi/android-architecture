package com.example.android.architecture.blueprints.todoapp.addedittask

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Matchers.any
import org.mockito.Matchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations


/**
 * Created by bfarrell on May 04, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

open class AddEditTaskPresenterTest {

    @Mock private lateinit var tasksRepository: TasksRepository

    @Mock private lateinit var addEditTasksView: AddEditTaskContract.View

    @Captor private lateinit var getTaskCallbackCaptor: ArgumentCaptor<TasksDataSource.GetTaskCallback>

    private lateinit var addEditTaskPresenter: AddEditTaskPresenter

    @Before fun setupMocksAndView() {
        MockitoAnnotations.initMocks(this)
        `when`(addEditTasksView.isActive()).thenReturn(true)
    }

    @Test fun createPresenter_setsThePresenterToView() {
        addEditTaskPresenter = AddEditTaskPresenter(null, tasksRepository, addEditTasksView, true)

        verify(addEditTasksView).setPresenter(addEditTaskPresenter)
    }

    @Test fun saveNewTaskToRepository_showsSuccessMessage() {
        addEditTaskPresenter = AddEditTaskPresenter(null, tasksRepository, addEditTasksView, true)

        addEditTaskPresenter.saveTask("New Task Title", "Some task description")

        verify(tasksRepository).saveTask(any(Task::class.java))
        verify(addEditTasksView).showTasksList()
    }

    @Test fun saveTask_emptyTaskShowsError() {
        addEditTaskPresenter = AddEditTaskPresenter(null, tasksRepository, addEditTasksView, true)

        addEditTaskPresenter.saveTask("", "")

        verify(addEditTasksView).showEmptyTaskError()
    }

    @Test fun saveExistingTaskToRepository_showsSuccessMessage() {
        addEditTaskPresenter = AddEditTaskPresenter("1", tasksRepository, addEditTasksView, true)

        addEditTaskPresenter.saveTask("Existing Task Title", "Some task description")

        verify(tasksRepository).saveTask(any(Task::class.java))
        verify(addEditTasksView).showTasksList()
    }

    @Test fun populateTask_callsRepoAndUpdatesView() {
        val testTask = Task("TITLE", "DESCRIPTION")

        addEditTaskPresenter = AddEditTaskPresenter(testTask.id, tasksRepository, addEditTasksView, true)

        addEditTaskPresenter.populateTask()

        verify(tasksRepository).getTask(eq(testTask.id), getTaskCallbackCaptor.capture())

        getTaskCallbackCaptor.value.onTaskLoaded(testTask)

        verify(addEditTasksView).setTitle(testTask.title)
        verify(addEditTasksView).setDescription(testTask.description)
        assertThat(addEditTaskPresenter.isDataMissing(), `is`(false))
    }

}