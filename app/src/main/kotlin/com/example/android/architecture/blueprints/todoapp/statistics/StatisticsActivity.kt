package com.example.android.architecture.blueprints.todoapp.statistics

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.NavUtils
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.example.android.architecture.blueprints.todoapp.Injection
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.util.find


/**
 * Created by bfarrell on May 03, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class StatisticsActivity: AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics_activity)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(R.string.statistics_title)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = find<DrawerLayout>(R.id.drawerLayout)
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark)

        val navigationView = find<NavigationView>(R.id.navigationView)
        setupDrawerContent(navigationView)

        var statisticsFragment = supportFragmentManager.find<StatisticsFragment?>(R.id.contentFrame)
        if (statisticsFragment == null) {
            statisticsFragment = StatisticsFragment.newInstance()
            supportFragmentManager.beginTransaction().add(R.id.contentFrame, statisticsFragment).commit()
        }

        StatisticsPresenter(Injection.provideTasksRepository(this), statisticsFragment)
    }

    private fun setupDrawerContent(view: NavigationView) {
        view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_list -> NavUtils.navigateUpFromSameTask(this)
            }

            item.isChecked = true
            drawerLayout.closeDrawers()
            true
        }
    }

}