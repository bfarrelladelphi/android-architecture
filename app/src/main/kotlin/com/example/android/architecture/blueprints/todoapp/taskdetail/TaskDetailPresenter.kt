package com.example.android.architecture.blueprints.todoapp.taskdetail

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository


/**
 * Created by bfarrell on May 03, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class TaskDetailPresenter(
        private val taskId: String,
        private val repository: TasksRepository,
        private val view: TaskDetailContract.View
): TaskDetailContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun start() {
        openTask()
    }

    override fun editTask() {
        if (taskId.isNullOrBlank()) {
            view.showMissingTask()
            return
        }

        view.showEditTask(taskId)
    }

    override fun deleteTask() {
        if (taskId.isNullOrBlank()) {
            view.showMissingTask()
            return
        }

        repository.deleteTask(taskId)
        view.showTaskDeleted()
    }

    override fun completeTask() {
        if (taskId.isNullOrBlank()) {
            view.showMissingTask()
            return
        }

        repository.completeTask(taskId)
        view.showTaskMarkedComplete()
    }

    override fun activateTask() {
        if (taskId.isNullOrBlank()) {
            view.showMissingTask()
            return
        }

        repository.activateTask(taskId)
        view.showTaskMarkedActive()
    }

    private fun openTask() {
        if (taskId.isNullOrBlank()) {
            view.showMissingTask()
            return
        }

        view.setLoadingIndicator(true)
        repository.getTask(taskId, object: TasksDataSource.GetTaskCallback {

            override fun onTaskLoaded(task: Task) {
                if (!view.isActive()) {
                    return
                }

                view.setLoadingIndicator(false)
                showTask(task)
            }

            override fun onDataNotAvailable() {
                if (!view.isActive()) {
                    return
                }

                view.showMissingTask()
            }
        })
    }

    private fun showTask(task: Task) {
        if (task.title.isBlank()) {
            view.hideTitle()
        } else {
            view.showTitle(task.title)
        }

        if (task.description.isBlank()) {
            view.hideDescription()
        } else {
            view.showDescription(task.description)
        }

        view.showCompletionStatus(task.completed)
    }

}