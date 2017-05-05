package com.example.android.architecture.blueprints.todoapp.data

import android.support.annotation.VisibleForTesting
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class FakeTasksRemoteDataSource private constructor() : TasksDataSource {

    companion object {

        private val TASKS_SERVICE_DATA = LinkedHashMap<String, Task>()

        private var INSTANCE: FakeTasksRemoteDataSource? = null

        fun getInstance(): FakeTasksRemoteDataSource {
            if (INSTANCE == null) {
                INSTANCE = FakeTasksRemoteDataSource()
            }
            return INSTANCE!!
        }

    }

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        callback.onTasksLoaded(ArrayList(TASKS_SERVICE_DATA.values))
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val task = TASKS_SERVICE_DATA[taskId]
        if (task != null) callback.onTaskLoaded(task) else callback.onDataNotAvailable()
    }

    override fun saveTask(task: Task) {
        TASKS_SERVICE_DATA.put(task.id, task)
    }

    override fun completeTask(taskId: String) {
        val cachedTask = TASKS_SERVICE_DATA[taskId]
        if (cachedTask != null) {
            completeTask(cachedTask)
        } else {
            throw NullPointerException("Task with id ($taskId) not found.")
        }
    }

    override fun completeTask(task: Task) {
        TASKS_SERVICE_DATA.put(task.id, task.copy(completed = true))
    }

    override fun activateTask(taskId: String) {
        val cachedTask = TASKS_SERVICE_DATA[taskId]
        if (cachedTask != null) {
            activateTask(cachedTask)
        } else {
            throw NullPointerException("Task with id ($taskId) not found.")
        }
    }

    override fun activateTask(task: Task) {
        TASKS_SERVICE_DATA.put(task.id, task.copy(completed = false))
    }

    override fun clearCompletedTasks() {
        val iterator = TASKS_SERVICE_DATA.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.completed) {
                iterator.remove()
            }
        }
    }

    override fun refreshTasks() {
        /* Not implemented */
    }

    override fun deleteAllTasks() {
        TASKS_SERVICE_DATA.clear()
    }

    override fun deleteTask(taskId: String) {
        TASKS_SERVICE_DATA.remove(taskId)
    }

    @VisibleForTesting
    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            TASKS_SERVICE_DATA.put(task.id, task)
        }
    }


}