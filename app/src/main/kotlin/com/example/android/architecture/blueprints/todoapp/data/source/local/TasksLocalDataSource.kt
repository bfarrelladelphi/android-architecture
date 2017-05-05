package com.example.android.architecture.blueprints.todoapp.data.source.local

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksPersistenceContract.TaskEntry


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class TasksLocalDataSource(context: Context): TasksDataSource {

    companion object {

        private var INSTANCE: TasksLocalDataSource? = null

        fun getInstance(context: Context): TasksLocalDataSource {
            if (INSTANCE == null) {
                INSTANCE = TasksLocalDataSource(context)
            }
            return INSTANCE!!
        }

    }

    private val databaseHelper: TasksDatabaseHelper = TasksDatabaseHelper(context)

    override fun getTasks(callback: TasksDataSource.LoadTasksCallback) {
        val tasks = ArrayList<Task>()
        val database = databaseHelper.readableDatabase

        val projection = arrayOf(
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED)

        val cursor = database.query(TaskEntry.TABLE_NAME, projection, null, null, null, null, null)

        if (cursor.count > 0) {
            while (cursor.moveToNext()) {
                val itemId = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION))
                val completed = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1

                tasks.add(Task(itemId, title, description, completed))
            }
        }

        cursor.close()
        database.close()

        if (tasks.isEmpty()) {
            callback.onDataNotAvailable()
        } else {
            callback.onTasksLoaded(tasks)
        }
    }

    override fun getTask(taskId: String, callback: TasksDataSource.GetTaskCallback) {
        val database = databaseHelper.readableDatabase

        val projection = arrayOf(
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED)

        val selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(taskId)

        var task: Task? = null
        val cursor = database.query(TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null)
        if (cursor.count > 0) {
            cursor.moveToFirst()
            val itemId = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION))
            val completed = cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1

            task = Task(itemId, title, description, completed)
        }

        cursor.close()
        database.close()

        if (task == null) {
            callback.onDataNotAvailable()
        } else {
            callback.onTaskLoaded(task)
        }
    }

    override fun saveTask(task: Task) {
        val database = databaseHelper.writableDatabase

        val values = ContentValues()
        values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.id)
        values.put(TaskEntry.COLUMN_NAME_TITLE, task.title)
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.description)
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, task.completed)

        database.insert(TaskEntry.TABLE_NAME, null, values)
        database.close()
    }

    override fun completeTask(taskId: String) {
        val database = databaseHelper.writableDatabase

        val values = ContentValues()
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, true)

        val selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(taskId)

        database.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs)
        database.close()
    }

    override fun completeTask(task: Task) {
        completeTask(task.id)
    }

    override fun activateTask(taskId: String) {
        val database = databaseHelper.writableDatabase

        val values = ContentValues()
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, false)

        val selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(taskId)

        database.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs)
        database.close()
    }

    override fun activateTask(task: Task) {
        activateTask(task.id)
    }

    override fun clearCompletedTasks() {
        val database = databaseHelper.writableDatabase

        val selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?"
        val selectionArgs = arrayOf("1")

        database.delete(TaskEntry.TABLE_NAME, selection, selectionArgs)
        database.close()
    }

    override fun refreshTasks() {
        /* Not implemented */
    }

    override fun deleteAllTasks() {
        val database = databaseHelper.writableDatabase

        database.delete(TaskEntry.TABLE_NAME, null, null)
        database.close()
    }

    override fun deleteTask(taskId: String) {
        val database = databaseHelper.writableDatabase

        val selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?"
        val selectionArgs = arrayOf(taskId)

        database.delete(TaskEntry.TABLE_NAME, selection, selectionArgs)
        database.close()
    }

}