package com.example.quicknotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.quicknotes.data.listsData.ToDoDatabase
import com.example.quicknotes.data.listsData.TodoEntity
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    val db = Room.databaseBuilder(
        application.applicationContext,
        ToDoDatabase::class.java,
        "todo.db"
    ).fallbackToDestructiveMigration().build()

    var allLists = db.todoDao().getAllToDoLists()

    fun add(todoEntity: TodoEntity) {
        viewModelScope.launch {
            db.todoDao().insert(todoEntity)
        }
    }
    fun update(todoEntity: TodoEntity){
        viewModelScope.launch {
            db.todoDao().update(todoEntity)
        }
    }

    fun delete(todoEntity: TodoEntity){
        viewModelScope.launch {
            db.todoDao().delete(todoEntity)
        }
    }
}