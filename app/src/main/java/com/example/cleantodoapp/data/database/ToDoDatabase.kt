package com.example.cleantodoapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun todoDao(): ToDoDao
    companion object{
        fun getDb(context: Context): ToDoDatabase{
        return Room.databaseBuilder(
            context,
            ToDoDatabase::class.java
            ,"task.db")
            .allowMainThreadQueries().build()
        }
    }
}