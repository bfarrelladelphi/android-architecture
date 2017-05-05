package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.data.Task


/**
 * Main entry point for accessing tasks data.
 * <p>
 *     For simplicity, only getTasks() and getTask() have callbacks. Consider adding callbacks to
 *     other methods to inform the user of network/database errors or successful operations.
 *     For example, when a new task is created, it's synchronously stored in cache but usually
 *     every operation on database or network should be executed in a different thread.
 */

interface TasksDataSource {

    interface LoadTasksCallback {

        fun onTasksLoaded(tasks: List<Task>)

        fun onDataNotAvailable()

    }

    interface GetTaskCallback {

        fun onTaskLoaded(task: Task)

        fun onDataNotAvailable()

    }

    fun getTasks(callback: LoadTasksCallback)

    fun getTask(taskId: String, callback: GetTaskCallback)

    fun saveTask(task: Task)

    fun completeTask(taskId: String)

    fun completeTask(task: Task)

    fun activateTask(taskId: String)

    fun activateTask(task: Task)

    fun clearCompletedTasks()

    fun refreshTasks()

    fun deleteAllTasks()

    fun deleteTask(taskId: String)

}