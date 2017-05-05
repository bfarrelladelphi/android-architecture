package com.example.android.architecture.blueprints.todoapp.util

import android.support.test.espresso.IdlingResource
import android.support.test.espresso.IdlingResource.ResourceCallback
import java.util.concurrent.atomic.AtomicInteger


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

class SimpleCountingIdlingResource(private val resourceName: String): IdlingResource {

    private val counter = AtomicInteger(0)

    @Volatile private var callback: ResourceCallback? = null

    override fun getName(): String {
        return resourceName
    }

    override fun isIdleNow(): Boolean {
        return counter.get() == 0
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.callback = callback
    }

    fun increment() {
        counter.getAndIncrement()
    }

    fun decrement() {
        val value = counter.decrementAndGet()
        if (value == 0) {
            callback?.onTransitionToIdle()
        }
        if (value < 0) {
            throw IllegalArgumentException("Counter has been corrupted!")
        }
    }


}