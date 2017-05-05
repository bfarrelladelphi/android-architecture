package com.example.android.architecture.blueprints.todoapp.tasks

import android.app.Activity
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskActivity
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class TasksPresenter(
        private val view: TasksContract.View, private val repository: TasksRepository
): TasksContract.Presenter {

    private var firstLoad = true

    private var currentFiltering = TasksFilterType.ALL_TASKS

    init {
        view.setPresenter(this)
    }

    override fun start() {
        loadTasks(false)
    }

    override fun result(requestCode: Int, resultCode: Int) {
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            view.showSuccessfullySavedMessage()
        }
    }

    override fun loadTasks(forceUpdate: Boolean) {
        loadTasks(forceUpdate || firstLoad, true)
        firstLoad = false
    }

    fun loadTasks(forceUpdate: Boolean, showLoading: Boolean) {
        if (showLoading) {
            view.setLoadingIndicator(true)
        }

        if (forceUpdate) {
            repository.refreshTasks()
        }

        EspressoIdlingResource.increment()

        repository.getTasks(object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                val tasksToShow = ArrayList<Task>()

                if (!EspressoIdlingResource.idlingResource.isIdleNow) {
                    EspressoIdlingResource.decrement()
                }

                for (task in tasks) {
                    when (currentFiltering) {
                        TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                        TasksFilterType.ACTIVE_TASKS -> if (!task.completed) tasksToShow.add(task)
                        TasksFilterType.COMPLETED_TASKS -> if (task.completed) tasksToShow.add(task)
                    }
                }

                if (!view.isActive()) {
                    return
                }

                if (showLoading) {
                    view.setLoadingIndicator(false)
                }

                processTasks(tasksToShow)
            }

            override fun onDataNotAvailable() {
                if (!view.isActive()) {
                    return
                }
                view.showLoadingTasksError()
            }

        })
    }

    override fun addNewTask() {
        view.showAddTask()
    }

    override fun openTaskDetails(requestedTask: Task) {
        view.showTaskDetails(requestedTask.id)
    }

    override fun completeTask(completedTask: Task) {
        repository.completeTask(completedTask)
        view.showTaskMarkedComplete()
        loadTasks(false, false)
    }

    override fun activateTask(activeTask: Task) {
        repository.activateTask(activeTask)
        view.showTaskMarkedActive()
        loadTasks(false, false)
    }

    override fun clearCompletedTasks() {
        repository.clearCompletedTasks()
        view.showCompletedTasksCleared()
        loadTasks(false, false)
    }

    override fun setFiltering(requestType: TasksFilterType) {
        currentFiltering = requestType
    }

    override fun getFiltering(): TasksFilterType {
        return currentFiltering
    }

    private fun processTasks(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            processEmptyTasks()
        } else {
            view.showTasks(tasks)
            showFilterLabel()
        }
    }

    private fun showFilterLabel() {
        when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> view.showActiveFilterLabel()
            TasksFilterType.COMPLETED_TASKS -> view.showCompletedFilterLabel()
            TasksFilterType.ALL_TASKS -> view.showAllFilterLabel()
        }
    }

    private fun processEmptyTasks() {
        when (currentFiltering) {
            TasksFilterType.ACTIVE_TASKS -> view.showNoActiveTasks()
            TasksFilterType.COMPLETED_TASKS -> view.showNoCompletedTasks()
            TasksFilterType.ALL_TASKS -> view.showNoTasks()
        }
    }

}