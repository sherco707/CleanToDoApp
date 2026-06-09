package com.example.cleantodoapp.data.database

import com.example.cleantodoapp.domain.entity.Todo

class Mapper {

    fun todotoTodoentity(todo: Todo): TodoEntity{

        return TodoEntity(id = todo.id,title = todo.title )
    }
    fun todoentityToTodo(todoEntity: TodoEntity): Todo{
        return Todo (id = todoEntity.id,title = todoEntity.title)
    }

}