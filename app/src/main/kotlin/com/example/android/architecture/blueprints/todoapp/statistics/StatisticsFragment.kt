package com.example.android.architecture.blueprints.todoapp.statistics

import android.os.Bundle
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

class StatisticsFragment: Fragment(), StatisticsContract.View {

    companion object {

        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }

    }

    private lateinit var presenter: StatisticsContract.Presenter

    private val statisticsTextView: TextView by lazy { find<TextView>(R.id.statistics) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.statistics_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun setPresenter(presenter: StatisticsContract.Presenter) {
        this.presenter = presenter
    }

    override fun setProgressIndicator(active: Boolean) {
        if (active) {
            statisticsTextView.setText(R.string.loading)
        } else {
            statisticsTextView.text = ""
        }
    }

    override fun showStatistics(numberOfIncompleteTasks: Int, numberOfCompleteTasks: Int) {
        if (numberOfCompleteTasks == 0 && numberOfIncompleteTasks == 0) {
            statisticsTextView.setText(R.string.statistics_no_tasks)
        } else {
            val display = resources.getString(R.string.statistics_active_tasks) + " " + numberOfIncompleteTasks + "\n" +
                    resources.getString(R.string.statistics_completed_tasks) + " " + numberOfCompleteTasks
            statisticsTextView.text = display
        }
    }

    override fun showLoadingStatisticsError() {
        statisticsTextView.setText(R.string.statistics_error)
    }

    override fun isActive(): Boolean {
        return isAdded
    }

}