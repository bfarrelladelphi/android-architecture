package com.example.android.architecture.blueprints.todoapp.util



/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    val idlingResource = SimpleCountingIdlingResource(RESOURCE)

    fun increment() {
        idlingResource.increment()
    }

    fun decrement() {
        idlingResource.decrement()
    }

}