package com.example.android.architecture.blueprints.todoapp.data.source.remote

import android.os.Handler
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class TasksRemoteDataSource private constructor(): TasksDataSource {

    companion object {

        private const val SERVICE_LATENCY_IN_MILLIS = 5000L

        private var TASKS_SERVICE_DATA = LinkedHashMap<String, Task>()

        private var INSTANCE: TasksRemoteDataSource? = null

        init {
            addTask("Build tower in Pisa", "Ground looks good, no foundation work required.")
            addTask("Finish bridge in Tacoma", "Found awesome girders at half the cost!")
        }

        fun getInstance(): TasksRemoteDataSource {
            if (INSTANCE == null) {
                INSTANCE = TasksRemoteDataSource()
            }
            return INSTANCE!!
        }

        private fun addTask(title: String, description: String) {
            val task = Task(title = title, description = description)
            TASKS_SERVICE_DATA.put(task.id, task)
        }

    }

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        val handler = Handler()
        handler.postDelayed({ callback.onTasksLoaded(ArrayList(TASKS_SERVICE_DATA.values)) }, SERVICE_LATENCY_IN_MILLIS)
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val task = TASKS_SERVICE_DATA[taskId]
        val handler = Handler()

        if (task != null) {
            handler.postDelayed({ callback.onTaskLoaded(task) }, SERVICE_LATENCY_IN_MILLIS)
        } else {
            handler.postDelayed({ callback.onDataNotAvailable() }, SERVICE_LATENCY_IN_MILLIS)
        }
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

}