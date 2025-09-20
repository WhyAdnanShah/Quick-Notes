package com.example.quicknotes.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.quicknotes.data.listsData.ListItemEntity
import com.example.quicknotes.data.listsData.ToDoDatabase
import com.example.quicknotes.data.listsData.TodoEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    val db = Room.databaseBuilder(
        application.applicationContext,
        ToDoDatabase::class.java,
        "todo.db"
    ).fallbackToDestructiveMigration().build()
    private val listItemDao = db.listItemDao()
    var allLists = db.todoDao().getAllToDoLists()
    //List

    //Here we are not only inserting the new TodDo but also using this to return a new Id for ToDo
    fun add(todoEntity: TodoEntity, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val newId = db.todoDao().insert(todoEntity).toInt()
            onResult(newId)
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

    //For Items in the Lists
    fun getListItems(todoId: Int): Flow<List<ListItemEntity>> {
        return listItemDao.getItemsForList(todoId)
    }

    /*          To insert an Item for a 'ToDo', we need an ID of the parent List.
                if ""SELECT * FROM todo WHERE id = :id" returns null, then the parent list does not exist.          */
    fun insertItem(listId: Int, content: String) {
        viewModelScope.launch {
            val todo = db.todoDao().getTodoById(listId)
            if (todo != null) {
                listItemDao.insertItem(ListItemEntity(listId = listId, content = content))
            } else {
                Log.e("Todo", "Parent list does not exist")
            }
        }
    }

    fun toggleItem(item: ListItemEntity) {
        viewModelScope.launch {
            val updated = item.copy(isChecked = !item.isChecked)
            listItemDao.updateItem(updated)
        }
    }

    fun deleteItem(item: ListItemEntity) {
        viewModelScope.launch {
            listItemDao.deleteItem(item)
        }
    }
}