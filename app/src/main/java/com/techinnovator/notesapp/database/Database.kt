package com.techinnovator.notesapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.techinnovator.notesapp.dao.NoteDao
import com.techinnovator.notesapp.model.Note
import com.techinnovator.notesapp.model.SubCategory

@Database(entities = [Note::class, SubCategory::class], version = 14, exportSchema = false)
abstract class NoteDatabase:RoomDatabase(){
    abstract fun getDao():NoteDao

    companion object{
        private var noteDatabase: NoteDatabase? = null

        fun getDatabaseInstance(context: Context): NoteDatabase?{
            if(noteDatabase==null){
                synchronized(NoteDatabase::class){
                    noteDatabase = Room.databaseBuilder(context.applicationContext, NoteDatabase::class.java, "note-db").fallbackToDestructiveMigration().build()
                }
            }
            return noteDatabase
        }
    }
}