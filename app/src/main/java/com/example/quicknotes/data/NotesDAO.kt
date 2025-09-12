package com.example.quicknotes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NotesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NotesEntity)

    @Delete
    suspend fun delete(note: NotesEntity)

    @Update
    suspend fun update(note: NotesEntity)
}