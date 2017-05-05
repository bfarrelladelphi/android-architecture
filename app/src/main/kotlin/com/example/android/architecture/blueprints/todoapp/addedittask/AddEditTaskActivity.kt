package com.example.android.architecture.blueprints.todoapp.addedittask

import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.test.espresso.IdlingResource
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.android.architecture.blueprints.todoapp.Injection
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.find


/**
 * Created by bfarrell on May 03, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class AddEditTaskActivity: AppCompatActivity() {

    companion object {

        const val REQUEST_ADD_TASK = 1

        const val SHOULD_LOAD_DATA_FROM_REPO_KEY = "SHOULD_LOAD_DATA_FROM_REPO_KEY"

    }

    private lateinit var presenter: AddEditTaskPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addtask_activity)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val taskId = intent.getStringExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)
        setToolbarTitle(taskId)

        var addEditTaskFragment = supportFragmentManager.find<AddEditTaskFragment?>(R.id.contentFrame)

        if (addEditTaskFragment == null) {
            addEditTaskFragment = AddEditTaskFragment.newInstance()

            if (intent.hasExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID)) {
                val bundle = Bundle()
                bundle.putString(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId)
                addEditTaskFragment.arguments = bundle
            }

            supportFragmentManager.beginTransaction().add(R.id.contentFrame, addEditTaskFragment).commit()
        }

        var shouldLoadDataFromRepo = true

        if (savedInstanceState != null) {
            shouldLoadDataFromRepo = savedInstanceState.getBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY)
        }

        presenter = AddEditTaskPresenter(taskId, Injection.provideTasksRepository(this),
                addEditTaskFragment, shouldLoadDataFromRepo)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(SHOULD_LOAD_DATA_FROM_REPO_KEY, presenter.isDataMissing())
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setToolbarTitle(taskId: String?) {
        supportActionBar?.setTitle(if (taskId == null) R.string.add_task else R.string.edit_task)
    }

    @VisibleForTesting fun getCountingIdlingResource(): IdlingResource {
        return EspressoIdlingResource.idlingResource
    }

}