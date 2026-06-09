package com.example.cleantodoapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ToDoDao {

    @Query("SELECT * FROM mytodos")
    fun getTodos(): List<TodoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTitle(todo: TodoEntity)


    @Update
    fun update(todo: TodoEntity)

    @Delete
    fun delete(todo: TodoEntity)
}