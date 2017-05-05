package com.example.android.architecture.blueprints.todoapp.util

import android.app.Activity
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View


/**
 * Created by bfarrell on May 04, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

inline fun <reified T : View?> View.find(@IdRes id: Int): T = this.findViewById(id) as T

inline fun <reified T : View?> Fragment.find(@IdRes id: Int): T = view!!.find<T>(id)

inline fun <reified T : View?> Activity.find(@IdRes id: Int): T = findViewById(id) as T

inline fun <reified T : Fragment?> FragmentManager.find(tag: String): T = this.findFragmentByTag(tag) as T

inline fun <reified T : Fragment?> FragmentManager.find(@IdRes id: Int): T = this.findFragmentById(id) as T