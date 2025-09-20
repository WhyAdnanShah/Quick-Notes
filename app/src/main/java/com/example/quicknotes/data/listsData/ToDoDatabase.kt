package com.example.quicknotes.data.listsData

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TodoEntity::class, ListItemEntity::class], version = 2, exportSchema = false)
abstract class ToDoDatabase : RoomDatabase() {
    abstract fun todoDao(): ToDoDAO
    abstract fun listItemDao(): ListItemDao
}