package com.example.android.architecture.blueprints.todoapp

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.support.annotation.IdRes
import android.support.test.InstrumentationRegistry
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import android.support.test.runner.lifecycle.Stage
import android.support.v7.widget.Toolbar
import com.example.android.architecture.blueprints.todoapp.util.find


/**
 * Created by bfarrell on May 04, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

object TestUtils {

    private fun rotateToLandscape(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun rotateToPortrait(activity: Activity) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    fun rotateOrientation(activity: Activity) {
        val currentOrientation = activity.resources.configuration.orientation
        when (currentOrientation) {
            Configuration.ORIENTATION_LANDSCAPE -> rotateToPortrait(activity)
            Configuration.ORIENTATION_PORTRAIT -> rotateToLandscape(activity)
            else -> rotateToLandscape(activity)
        }
    }

    fun getToolbarNavigationContentDescription(activity: Activity, @IdRes toolbarId: Int): String {
        val toolbar = activity.find<Toolbar?>(toolbarId)
        if (toolbar != null) {
            return toolbar.navigationContentDescription as String
        } else {
            throw RuntimeException("No toolbar found.")
        }
    }

    fun getCurrentActivity(): Activity {
        val resumedActivity = arrayOfNulls<Activity>(1)

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(Stage.RESUMED)
            if (resumedActivities.iterator().hasNext()) {
                resumedActivity[0] = resumedActivities.iterator().next()
            } else {
                throw IllegalStateException("No Activity in stage RESUMED")
            }
        }

        return resumedActivity[0] as Activity
    }

}