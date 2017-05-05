package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource


/**
 * Created by bfarrell on May 03, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class StatisticsPresenter(
        private val repository: TasksRepository, private val view: StatisticsContract.View
): StatisticsContract.Presenter {

    init {
        view.setPresenter(this)
    }

    override fun start() {
        loadStatistics()
    }

    private fun loadStatistics() {
        view.setProgressIndicator(true)

        repository.getTasks(object : TasksDataSource.LoadTasksCallback {
            override fun onTasksLoaded(tasks: List<Task>) {
                var activeTasks = 0
                var completedTasks = 0

                if (!EspressoIdlingResource.idlingResource.isIdleNow) {
                    EspressoIdlingResource.decrement()
                }

                for (task in tasks) {
                    if (task.completed) {
                        completedTasks += 1
                    } else {
                        activeTasks += 1
                    }
                }

                if (!view.isActive()) {
                    return
                }

                view.setProgressIndicator(false)
                view.showStatistics(activeTasks, completedTasks)
            }

            override fun onDataNotAvailable() {
                if (!view.isActive()) {
                    return
                }

                view.showLoadingStatisticsError()
            }
        })
    }

}