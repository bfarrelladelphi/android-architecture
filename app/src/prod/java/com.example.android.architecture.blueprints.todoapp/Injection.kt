package com.example.android.architecture.blueprints.todoapp

import android.content.Context
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.remote.TasksRemoteDataSource


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

object Injection {

    fun provideTasksRepository(context: Context): TasksRepository {
        return TasksRepository.getInstance(TasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(context))
    }

}