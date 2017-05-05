package com.example.android.architecture.blueprints.todoapp.data

import java.util.UUID


/**
 * Immutable model class for a Task.
 */

data class Task(
        val title: String,
        val description: String,
        val id: String = UUID.randomUUID().toString(),
        val completed: Boolean = false
) {

    fun isBlank(): Boolean {
        return title.isBlank() && description.isBlank()
    }

}