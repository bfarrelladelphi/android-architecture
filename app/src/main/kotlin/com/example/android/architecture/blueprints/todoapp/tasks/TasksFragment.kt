package com.example.android.architecture.blueprints.todoapp.tasks

import android.content.Intent
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.PopupMenu
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.addedittask.AddEditTaskActivity
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.taskdetail.TaskDetailActivity
import com.example.android.architecture.blueprints.todoapp.util.find


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class TasksFragment: Fragment(), TasksContract.View, TasksAdapter.TaskItemListener {

    companion object {

        fun newInstance(): TasksFragment {
            return TasksFragment()
        }

    }

    private lateinit var presenter: TasksContract.Presenter

    private val adapter: TasksAdapter by lazy { TasksAdapter(ArrayList<Task>(), this) }

    private val swipeRefreshLayout: ScrollChildSwipeRefreshLayout by lazy {
        find<ScrollChildSwipeRefreshLayout>(R.id.refreshLayout)
    }

    private val filteringLabelView: TextView by lazy { find<TextView>(R.id.filteringLabel) }

    private val tasksView: LinearLayout by lazy { find<LinearLayout>(R.id.tasksLayout) }

    private val noTasksView: View by lazy { find<View>(R.id.noTasksLayout) }

    private val noTasksIcon: ImageView by lazy { find<ImageView>(R.id.noTasksIcon) }

    private val noTasksMainView: TextView by lazy { find<TextView>(R.id.noTasksMain) }

    private val noTasksAddView: TextView by lazy { find<TextView>(R.id.noTasksAdd) }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.result(requestCode, resultCode)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tasks_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.find<ListView>(R.id.tasksList)
        listView.adapter = adapter

        noTasksAddView.setOnClickListener { showAddTask() }

        val floatingActionButton = activity.find<FloatingActionButton>(R.id.addTaskButton)
        floatingActionButton.setImageResource(R.drawable.ic_add_white_24dp)
        floatingActionButton.setOnClickListener { presenter.addNewTask() }

        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(context, R.color.colorPrimary),
                ContextCompat.getColor(context, R.color.colorAccent),
                ContextCompat.getColor(context, R.color.colorPrimaryDark)
        )
        swipeRefreshLayout.setScrollUpChild(listView)

        swipeRefreshLayout.setOnRefreshListener { presenter.loadTasks(false) }
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear -> presenter.clearCompletedTasks()
            R.id.action_filter -> showFilteringPopupMenu()
            R.id.action_refresh -> presenter.loadTasks(true)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment, menu)
    }

    override fun setPresenter(presenter: TasksContract.Presenter) {
        this.presenter = presenter
    }

    override fun setLoadingIndicator(active: Boolean) {
        if (view == null) {
            return
        }

        swipeRefreshLayout.post { swipeRefreshLayout.isRefreshing = active }
    }

    override fun showTasks(tasks: List<Task>) {
        adapter.replaceData(tasks)

        tasksView.visibility = View.VISIBLE
        noTasksView.visibility = View.GONE
    }

    override fun showAddTask() {
        val intent = Intent(context, AddEditTaskActivity::class.java)
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK)
    }

    override fun showTaskDetails(taskId: String) {
        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId)
        startActivity(intent)
    }

    override fun showTaskMarkedComplete() {
        showMessage(R.string.task_marked_complete)
    }

    override fun showTaskMarkedActive() {
        showMessage(R.string.task_marked_active)
    }

    override fun showCompletedTasksCleared() {
        showMessage(R.string.completed_tasks_cleared)
    }

    override fun showLoadingTasksError() {
        showMessage(R.string.load_tasks_error)
    }

    override fun showNoTasks() {
        showNoTasksViews(R.string.no_tasks_all, R.drawable.ic_assignment_turned_in_black_24dp, false)
    }

    override fun showActiveFilterLabel() {
        filteringLabelView.setText(R.string.label_active)
    }

    override fun showCompletedFilterLabel() {
        filteringLabelView.setText(R.string.label_completed)
    }

    override fun showAllFilterLabel() {
        filteringLabelView.setText(R.string.label_all)
    }

    override fun showNoActiveTasks() {
        showNoTasksViews(R.string.no_tasks_active, R.drawable.ic_check_circle_black_24dp, false)
    }

    override fun showNoCompletedTasks() {
        showNoTasksViews(R.string.no_tasks_completed, R.drawable.ic_verified_user_black_24dp, false)
    }

    override fun showSuccessfullySavedMessage() {
        showMessage(R.string.successfully_saved_task_message)
    }

    override fun isActive(): Boolean {
        return isAdded
    }

    override fun showFilteringPopupMenu() {
        val popup = PopupMenu(context, activity.find<View>(R.id.action_filter))
        popup.menuInflater.inflate(R.menu.filter_tasks, popup.menu)

        popup.setOnMenuItemClickListener { item -> run {
            when (item.itemId) {
                R.id.filter_active -> presenter.setFiltering(TasksFilterType.ACTIVE_TASKS)
                R.id.filter_completed -> presenter.setFiltering(TasksFilterType.COMPLETED_TASKS)
                else -> presenter.setFiltering(TasksFilterType.ALL_TASKS)
            }
            presenter.loadTasks(false)
            true
        } }

        popup.show()
    }

    override fun onTaskClick(clickedTask: Task) {
        presenter.openTaskDetails(clickedTask)
    }

    override fun onCompleteTaskClick(completedTask: Task) {
        presenter.completeTask(completedTask)
    }

    override fun onActivateTaskClick(activatedTask: Task) {
        presenter.activateTask(activatedTask)
    }

    fun showNoTasksViews(@StringRes text: Int, @DrawableRes icon: Int, showAddView: Boolean) {
        tasksView.visibility = View.GONE
        noTasksView.visibility = View.VISIBLE

        noTasksMainView.setText(text)
        noTasksIcon.setImageResource(icon)
        noTasksAddView.visibility = if (showAddView) View.VISIBLE else View.GONE
    }

    fun showMessage(@StringRes message: Int) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG).show()
    }

}