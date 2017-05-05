package com.example.android.architecture.blueprints.todoapp.addedittask

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository


/**
 * Created by bfarrell on May 03, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class AddEditTaskPresenter(
        private val taskId: String? = null,
        private val repository: TasksRepository,
        private val view: AddEditTaskContract.View,
        shouldLoadDataFromRepository: Boolean
): AddEditTaskContract.Presenter, TasksDataSource.GetTaskCallback {

    private var isDataMissing = shouldLoadDataFromRepository

    init {
        view.setPresenter(this)
    }

    override fun start() {
        if (!isNewTask() && isDataMissing) {
            populateTask()
        }
    }

    override fun saveTask(title: String, description: String) {
        if (isNewTask()) {
            createTask(title, description)
        } else {
            updateTask(title, description)
        }
    }

    override fun populateTask() {
        if (isNewTask()) {
            throw RuntimeException("populateTask() was called, but task is new.")
        } else {
            repository.getTask(taskId!!, this)
        }
    }

    override fun isDataMissing(): Boolean {
        return isDataMissing
    }

    override fun onTaskLoaded(task: Task) {
        if (view.isActive()) {
            view.setTitle(task.title)
            view.setDescription(task.description)
        }
        isDataMissing = false
    }

    override fun onDataNotAvailable() {
        if (view.isActive()) {
            view.showEmptyTaskError()
        }
    }

    private fun isNewTask(): Boolean {
        return taskId.isNullOrBlank()
    }

    private fun createTask(title: String, description: String) {
        val task = Task(title = title, description = description)
        if (task.isBlank()) {
            view.showEmptyTaskError()
        } else {
            repository.saveTask(task)
            view.showTasksList()
        }
    }

    private fun updateTask(title: String, description: String) {
        if (isNewTask()) {
            throw RuntimeException("updateTask() was called, but task is new.")
        } else {
            repository.saveTask(Task(taskId!!, title, description))
            view.showTasksList()
        }
    }

}