package com.techinnovator.notesapp.repository

import androidx.lifecycle.LiveData
import com.techinnovator.notesapp.dao.NoteDao
import com.techinnovator.notesapp.model.Note
import com.techinnovator.notesapp.model.SubCategory

class NoteRepository(private val noteDao: NoteDao){
    fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getNotes()
    }

    suspend fun insertNewNote(note: Note){
        noteDao.insertNode(note)
    }

    suspend fun deleteNote(note: Note){
        noteDao.deleteNode(note)
    }

    suspend fun deleteList(id: Int){
        noteDao.deleteNote(id)
    }

    suspend fun deleteCheckedItems(subCategory: String, isCheck: Boolean){
        noteDao.deleteCheckedItems(subCategory, isCheck)
    }

    suspend fun updateNote(note: Note){
        noteDao.updateList(note)
    }

    suspend fun updateListItems(oldCategory: String, newCategory:String){
        noteDao.updateItems(oldCategory, newCategory)
    }
    suspend fun deleteItem(item: SubCategory){
        noteDao.deleteItem(item)
    }

    suspend fun deleteCategoryItems(category: String){
        noteDao.deleteSubCategoryItems(category)
    }

    suspend fun insertNewSubCategory(subCategory: SubCategory){
        noteDao.insertSubCategory(subCategory)
    }

    suspend fun updateSubCategory(subCategory: SubCategory){
        noteDao.updateSubCategory(subCategory)
    }

    fun getAllCategoryItems(category:String): LiveData<List<SubCategory>> {
        return noteDao.getTextFromCategory(category)
    }

    fun getUncheckedItems(category: String, isCheck:Boolean): LiveData<List<SubCategory>> {
        return noteDao.getUncheckedCategories(category, isCheck)
    }

    suspend fun getTotalCost(category: String):Int{
        return noteDao.getTotalCost(category)
    }

    suspend fun getItemByName(name: String): Note{
        return noteDao.getListItemByName(name)
    }
}