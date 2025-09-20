package com.example.quicknotes.data.listsData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDAO {
    //To get all Lists
    @Query("SELECT * FROM todo ORDER BY timestamp DESC")
    fun getAllToDoLists(): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity) : Long

    @Update
    suspend fun update(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    @Query("SELECT * FROM todo WHERE id = :id")
    suspend fun getTodoById(id: Int): TodoEntity?
}


@Dao
interface ListItemDao {
    //To get all Items from a List
    @Query("SELECT * FROM list_items WHERE listId = :listId")
    fun getItemsForList(listId: Int): Flow<List<ListItemEntity>>

    @Insert
    suspend fun insertItem(item: ListItemEntity)

    @Update
    suspend fun updateItem(item: ListItemEntity)

    @Delete
    suspend fun deleteItem(item: ListItemEntity)
}