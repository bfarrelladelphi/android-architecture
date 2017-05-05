package com.example.android.architecture.blueprints.todoapp.custom.action

import android.content.res.Resources
import android.support.design.widget.NavigationView
import android.support.test.espresso.PerformException
import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.util.HumanReadables
import android.view.Menu
import android.view.View
import org.hamcrest.Matcher
import org.hamcrest.Matchers


/**
 * Created by bfarrell on May 04, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class NavigationViewActions private constructor() {

    companion object {

        fun navigateTo(menuItemId: Int): ViewAction {
            return object : ViewAction {

                override fun getDescription(): String {
                    return "click on menu item with id"
                }

                override fun getConstraints(): Matcher<View> {
                    return Matchers.allOf(
                            ViewMatchers.isAssignableFrom(NavigationView::class.java),
                            ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                            ViewMatchers.isDisplayingAtLeast(90))
                }

                override fun perform(uiController: UiController, view: View) {
                    val navigationView = view as NavigationView
                    val menu = navigationView.menu

                    if (menu.findItem(menuItemId) == null) {
                        throw PerformException.Builder()
                                .withActionDescription(this.description)
                                .withViewDescription(HumanReadables.describe(view))
                                .withCause(RuntimeException(getErrorMessage(menu, view)))
                                .build()
                    }

                    menu.performIdentifierAction(menuItemId, 0)
                    uiController.loopMainThreadUntilIdle()
                }

                private fun getErrorMessage(menu: Menu, view: View): String {
                    val NEW_LINE = System.getProperty("line.separator")
                    val errorMessage = StringBuilder("Menu item was not found, " +
                            "available menu items:").append(NEW_LINE)

                    for (position in 0 until menu.size()) {
                        errorMessage.append("[MenuItem] position=").append(position)
                        val menuItem = menu.getItem(position)
                        if (menuItem != null) {
                            if (menuItem.title != null) {
                                errorMessage.append(menuItem.title)
                            }
                            if (view.resources != null) {
                                try {
                                    errorMessage.append(", id=")
                                    val menuItemResourceName = view.resources
                                            .getResourceName(menuItem.itemId)
                                    errorMessage.append(menuItemResourceName)
                                } catch (exception: Resources.NotFoundException) {
                                    errorMessage.append("not found")
                                }
                            }
                            errorMessage.append(NEW_LINE)
                        }
                    }
                    return errorMessage.toString()
                }

            }
        }

    }

}