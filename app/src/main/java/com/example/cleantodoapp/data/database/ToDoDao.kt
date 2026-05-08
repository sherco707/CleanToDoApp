package com.example.cleantodoapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {

    @Query("SELECT * FROM mytodos where id=1")
    fun getTodos(): TodoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(todo: TodoEntity)

    @Update
     fun update(todo: TodoEntity)

    @Delete
     fun delete(todo: TodoEntity)
}