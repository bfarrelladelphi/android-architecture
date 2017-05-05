package com.example.android.architecture.blueprints.todoapp.tasks

import android.content.Intent
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.design.widget.NavigationView
import android.support.test.espresso.IdlingResource
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.example.android.architecture.blueprints.todoapp.Injection
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.statistics.StatisticsActivity
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.util.find


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class TasksActivity: AppCompatActivity() {

    private val CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY"

    private val toolbar: Toolbar by lazy { find<Toolbar>(R.id.toolbar) }
    private val drawerLayout: DrawerLayout by lazy { find<DrawerLayout>(R.id.drawerLayout) }

    private lateinit var presenter: TasksPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tasks_activity)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)

        val navigationView = find<NavigationView>(R.id.navigationView)
        setupDrawerContent(navigationView)

        var tasksFragment = supportFragmentManager.find<TasksFragment?>(R.id.contentFrame)

        if (tasksFragment == null) {
            tasksFragment = TasksFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.contentFrame, tasksFragment).commit()
        }

        presenter = TasksPresenter(tasksFragment, Injection.provideTasksRepository(this))

        if (savedInstanceState != null) {
            val currentFiltering = savedInstanceState.getSerializable(CURRENT_FILTERING_KEY) as TasksFilterType
            presenter.setFiltering(currentFiltering)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(CURRENT_FILTERING_KEY, presenter.getFiltering())
        super.onSaveInstanceState(outState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            } else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun setupDrawerContent(view: NavigationView) {
        view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_statistics -> {
                    val intent = Intent(this, StatisticsActivity::class.java)
                    startActivity(intent)
                }
            }

            item.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }

    @VisibleForTesting
    fun getCountingIdlingResource(): IdlingResource {
        return EspressoIdlingResource.idlingResource
    }

}