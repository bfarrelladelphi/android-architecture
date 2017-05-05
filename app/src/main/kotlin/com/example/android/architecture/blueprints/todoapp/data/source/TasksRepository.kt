package com.example.android.architecture.blueprints.todoapp.data.source

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource.LoadTasksCallback
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource.GetTaskCallback


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class TasksRepository private constructor(
        private val tasksRemoteDataSource: TasksDataSource,
        private val tasksLocalDataSource: TasksDataSource
): TasksDataSource {

    companion object {

        private var INSTANCE: TasksRepository? = null

        fun getInstance(
                tasksRemoteDataSource: TasksDataSource, tasksLocalDataSource: TasksDataSource
        ): TasksRepository {
            if (INSTANCE == null) {
                INSTANCE = TasksRepository(tasksRemoteDataSource, tasksLocalDataSource)
            }
            return INSTANCE!!
        }


        fun destroyInstance() {
            INSTANCE = null
        }

    }

    val cachedTasks = LinkedHashMap<String, Task>()

    var cacheIsDirty = false

    override fun getTasks(callback: LoadTasksCallback) {
        if (cachedTasks.isNotEmpty() && !cacheIsDirty) {
            callback.onTasksLoaded(ArrayList(cachedTasks.values))
            return
        }

        if (cacheIsDirty) {
            getTasksFromRemoteDataSource(callback)
        } else {
            tasksLocalDataSource.getTasks(object : LoadTasksCallback {
                override fun onTasksLoaded(tasks: List<Task>) {
                    refreshCache(tasks)
                    callback.onTasksLoaded(ArrayList(cachedTasks.values))
                }

                override fun onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback)
                }
            })
        }
    }

    override fun getTask(taskId: String, callback: GetTaskCallback) {
        val cachedTask = cachedTasks[taskId]

        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask)
            return
        }

        tasksLocalDataSource.getTask(taskId, object : GetTaskCallback {
            override fun onTaskLoaded(task: Task) {
                cachedTasks.put(task.id, task)
                callback.onTaskLoaded(task)
            }

            override fun onDataNotAvailable() {
                tasksRemoteDataSource.getTask(taskId, object : GetTaskCallback {
                    override fun onTaskLoaded(task: Task) {
                        cachedTasks.put(task.id, task)
                        callback.onTaskLoaded(task)
                    }

                    override fun onDataNotAvailable() {
                        callback.onDataNotAvailable()
                    }
                })
            }
        })
    }

    override fun saveTask(task: Task) {
        tasksRemoteDataSource.saveTask(task)
        tasksLocalDataSource.saveTask(task)

        cachedTasks.put(task.id, task)
    }

    override fun completeTask(taskId: String) {
        val cachedTask = cachedTasks[taskId]
        if (cachedTask != null) {
            completeTask(cachedTask)
        } else {
            throw NullPointerException("Task with id ($taskId) not found.")
        }
    }

    override fun completeTask(task: Task) {
        tasksRemoteDataSource.completeTask(task)
        tasksLocalDataSource.completeTask(task)

        cachedTasks.put(task.id, task.copy(completed = true))
    }

    override fun activateTask(taskId: String) {
        val cachedTask = cachedTasks[taskId]
        if (cachedTask != null) {
            activateTask(cachedTask)
        } else {
            throw NullPointerException("Task with id ($taskId) not found.")
        }
    }

    override fun activateTask(task: Task) {
        tasksRemoteDataSource.activateTask(task)
        tasksLocalDataSource.activateTask(task)

        cachedTasks.put(task.id, task.copy(completed = false))
    }

    override fun clearCompletedTasks() {
        tasksRemoteDataSource.clearCompletedTasks()
        tasksLocalDataSource.clearCompletedTasks()

        val iterator = cachedTasks.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.completed) {
                iterator.remove()
            }
        }
    }

    override fun refreshTasks() {
        cacheIsDirty = true
    }

    override fun deleteAllTasks() {
        tasksRemoteDataSource.deleteAllTasks()
        tasksLocalDataSource.deleteAllTasks()

        cachedTasks.clear()
    }

    override fun deleteTask(taskId: String) {
        tasksRemoteDataSource.deleteTask(taskId)
        tasksLocalDataSource.deleteTask(taskId)

        cachedTasks.remove(taskId)
    }

    private fun getTasksFromRemoteDataSource(callback: LoadTasksCallback) {
        tasksRemoteDataSource.getTasks(object : LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                refreshCache(tasks)
                refreshLocalDataSource(tasks)
                callback.onTasksLoaded(tasks)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    private fun refreshCache(tasks: List<Task>) {
        cachedTasks.clear()
        for (task in tasks) {
            cachedTasks.put(task.id, task)
        }
        cacheIsDirty = false
    }

    private fun refreshLocalDataSource(tasks: List<Task>) {
        tasksLocalDataSource.deleteAllTasks()
        for (task in tasks) {
            tasksLocalDataSource.saveTask(task)
        }
    }

}