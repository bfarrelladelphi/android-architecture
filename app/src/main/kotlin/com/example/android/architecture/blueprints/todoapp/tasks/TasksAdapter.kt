package com.example.android.architecture.blueprints.todoapp.tasks

import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class TasksAdapter(private val tasks: MutableList<Task>, private val listener: TaskItemListener): BaseAdapter() {

    interface TaskItemListener {

        fun onTaskClick(clickedTask: Task)

        fun onCompleteTaskClick(completedTask: Task)

        fun onActivateTaskClick(activatedTask: Task)

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(parent.context)
            view = inflater.inflate(R.layout.tasks_item, parent, false)
        }

        val task = getItem(position)

        val titleTextView = view?.findViewById(R.id.title) as TextView
        titleTextView.text = if (task.title.isNotBlank()) task.title else task.description

        val completeCheckBox = view.findViewById(R.id.complete) as CheckBox
        completeCheckBox.isChecked = task.completed

        if (task.completed) {
            ViewCompat.setBackground(view, ContextCompat.getDrawable(parent.context, R.drawable.list_completed_touch_feedback))
        } else {
            ViewCompat.setBackground(view, ContextCompat.getDrawable(parent.context, R.drawable.touch_feedback))
        }

        completeCheckBox.setOnClickListener { v ->
            run {
                if (!task.completed) {
                    listener.onCompleteTaskClick(task)
                } else {
                    listener.onActivateTaskClick(task)
                }
            }
        }

        view.setOnClickListener { v ->
            run {
                listener.onTaskClick(task)
            }
        }

        return view
    }

    override fun getItem(position: Int): Task {
        return tasks[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return tasks.size
    }

    fun replaceData(tasks: List<Task>) {
        this.tasks.clear()
        this.tasks.addAll(tasks)
        notifyDataSetChanged()
    }

}