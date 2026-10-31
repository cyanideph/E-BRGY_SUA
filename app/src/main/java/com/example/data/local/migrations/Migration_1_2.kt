package com.example.data.local.migrations

import androidx.room.Migration
import androidx.room.Placeholder

val MIGRATION_1_2: Migration = Migration(1, 2) { database ->
    // Add new tables/entities here as needed
    // Example: database.execSQL("ALTER TABLE table_name ADD COLUMN column_name TYPE")
}