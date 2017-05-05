package com.example.android.architecture.blueprints.todoapp.addedittask

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.util.find


/**
 * Created by bfarrell on May 03, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class AddEditTaskFragment: Fragment(), AddEditTaskContract.View {

    companion object {

        const val ARGUMENT_EDIT_TASK_ID = "ARGUMENT_EDIT_TASK_ID"

        fun newInstance(): AddEditTaskFragment {
            return AddEditTaskFragment()
        }

    }

    private lateinit var presenter: AddEditTaskContract.Presenter

    private val titleTextView: TextView by lazy { find<TextView>(R.id.addTaskTitle) }

    private val descriptionTextView: TextView by lazy { find<TextView>(R.id.addTaskDescription) }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val floatingActionButton = activity.find<FloatingActionButton>(R.id.editTaskDoneButton)
        floatingActionButton.setImageResource(R.drawable.ic_done_white_24dp)
        floatingActionButton.setOnClickListener {
            presenter.saveTask(titleTextView.text.toString(), descriptionTextView.text.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.addtask_fragment, container, false)
    }

    override fun setPresenter(presenter: AddEditTaskContract.Presenter) {
        this.presenter = presenter
    }

    override fun showEmptyTaskError() {
        Snackbar.make(titleTextView, R.string.empty_task_message, Snackbar.LENGTH_LONG).show()
    }

    override fun showTasksList() {
        activity.setResult(Activity.RESULT_OK)
        activity.finish()
    }

    override fun setTitle(title: String) {
        titleTextView.text = title
    }

    override fun setDescription(description: String) {
        descriptionTextView.text = description
    }

    override fun isActive(): Boolean {
        return isAdded
    }

}