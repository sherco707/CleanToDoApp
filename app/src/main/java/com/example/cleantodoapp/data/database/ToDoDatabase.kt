package com.example.cleantodoapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [TodoEntity::class], version = 2, exportSchema = true)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun todoDao(): ToDoDao

    companion object {
        private const val DATABASE_NAME = "task.db"

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE mytodos ADD COLUMN createdAt INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE mytodos ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun create(context: Context): ToDoDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ToDoDatabase::class.java,
                DATABASE_NAME,
            )
                .addMigrations(MIGRATION_1_2)
                .build()
        }
    }
}
