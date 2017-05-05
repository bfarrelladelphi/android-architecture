package com.example.android.architecture.blueprints.todoapp.data.source.local

import android.provider.BaseColumns


/**
 * Created by bfarrell on May 02, 2017
 * 2017 Adelphi University, Office of Information Technology
 * Hagedorn Hall of Enterprise - Lower Level; (516) 877-3331
 */

object TasksPersistenceContract {

    object TaskEntry: BaseColumns {
        val _ID = "id"
        val TABLE_NAME = "task"
        val COLUMN_NAME_ENTRY_ID = "entryid"
        val COLUMN_NAME_TITLE = "title"
        val COLUMN_NAME_DESCRIPTION = "description"
        val COLUMN_NAME_COMPLETED = "completed"
    }

}