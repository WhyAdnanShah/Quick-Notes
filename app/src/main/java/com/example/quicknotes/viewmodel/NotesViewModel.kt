package com.example.quicknotes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.quicknotes.data.NoteDatabase
import com.example.quicknotes.data.NotesEntity
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    val db = Room.databaseBuilder(
        application.applicationContext,
        NoteDatabase::class.java,
        "notes.db"
    ).fallbackToDestructiveMigration().build()

    var allNotes = db.noteDao().getAllNotes()

    fun addNote(NotesEntity: NotesEntity) {
        viewModelScope.launch {
            db.noteDao().insert(NotesEntity)
        }
    }
    fun update(notesEntity: NotesEntity){
        viewModelScope.launch {
            db.noteDao().update(notesEntity)
        }
    }

    fun delete(notesEntity: NotesEntity){
        viewModelScope.launch {
            db.noteDao().delete(notesEntity)
        }
    }
}