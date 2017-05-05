package com.example.android.architecture.blueprints.todoapp.addedittask

import com.example.android.architecture.blueprints.todoapp.BasePresenter
import com.example.android.architecture.blueprints.todoapp.BaseView


/**
 * Created by bfarrell on May 03, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

interface AddEditTaskContract {

    interface Presenter: BasePresenter {

        fun saveTask(title: String, description: String)

        fun populateTask()

        fun isDataMissing(): Boolean

    }

    interface View: BaseView<Presenter> {

        fun showEmptyTaskError()

        fun showTasksList()

        fun setTitle(title: String)

        fun setDescription(description: String)

        fun isActive(): Boolean

    }

}