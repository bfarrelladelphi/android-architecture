package com.example.android.architecture.blueprints.todoapp.taskdetail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskActivity
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskFragment
import com.example.android.architecture.blueprints.todoapp.util.find


/**
 * Created by bfarrell on May 03, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class TaskDetailFragment: Fragment(), TaskDetailContract.View {

    companion object {

        private const val ARGUMENT_TASK_ID = "ARGUMENT_TASK_ID"

        private const val REQUEST_EDIT_TASK = 1

        fun newInstance(taskId: String): TaskDetailFragment {
            val fragment = TaskDetailFragment()

            val bundle = Bundle()
            bundle.putString(ARGUMENT_TASK_ID, taskId)
            fragment.arguments = bundle

            return fragment
        }

    }

    private lateinit var presenter: TaskDetailContract.Presenter

    private val titleTextView: TextView by lazy { find<TextView>(R.id.taskDetailTitle) }

    private val descriptionTextView: TextView by lazy { find<TextView>(R.id.taskDetailDescription) }

    private val completedCheckBox: CheckBox by lazy { find<CheckBox>(R.id.taskDetailComplete) }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.taskdetail_fragment, container, false)
        setHasOptionsMenu(true)

        val floatingActionButton = activity.find<FloatingActionButton>(R.id.editTaskButton)
        floatingActionButton.setOnClickListener { presenter.editTask() }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_EDIT_TASK && resultCode == Activity.RESULT_OK) {
                activity.finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> {
                presenter.deleteTask()
                return true
            }
            else -> return false
        }
    }

    override fun setPresenter(presenter: TaskDetailContract.Presenter) {
        this.presenter = presenter
    }

    override fun setLoadingIndicator(active: Boolean) {
        if (active) {
            titleTextView.text = ""
            descriptionTextView.setText(R.string.loading)
        }
    }

    override fun showMissingTask() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideTitle() {
        titleTextView.visibility = View.GONE
    }

    override fun showTitle(title: String) {
        titleTextView.visibility = View.VISIBLE
        titleTextView.text = title
    }

    override fun hideDescription() {
        descriptionTextView.visibility = View.GONE
    }

    override fun showDescription(description: String) {
        descriptionTextView.visibility = View.VISIBLE
        descriptionTextView.text = description
    }

    override fun showCompletionStatus(complete: Boolean) {
        completedCheckBox.isChecked = complete
        completedCheckBox.setOnCheckedChangeListener { buttonView, isChecked -> run {
            if (isChecked) presenter.completeTask()  else presenter.activateTask()
        } }
    }

    override fun showEditTask(taskId: String) {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId)
        startActivityForResult(intent, REQUEST_EDIT_TASK)
    }

    override fun showTaskDeleted() {
        activity.finish()
    }

    override fun showTaskMarkedComplete() {
        Snackbar.make(view!!, R.string.task_marked_complete, Snackbar.LENGTH_LONG).show()
    }

    override fun showTaskMarkedActive() {
        Snackbar.make(view!!, R.string.task_marked_active, Snackbar.LENGTH_LONG).show()
    }

    override fun isActive(): Boolean {
        return isAdded
    }

}