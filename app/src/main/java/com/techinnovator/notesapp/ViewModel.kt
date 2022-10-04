package com.techinnovator.notesapp

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.techinnovator.notesapp.database.NoteDatabase
import com.techinnovator.notesapp.model.Note
import com.techinnovator.notesapp.model.SubCategory
import com.techinnovator.notesapp.repository.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(context: Context):ViewModel(){
    private val noteRepository: NoteRepository
    var notes:LiveData<List<Note>>
    lateinit var categoryItems: LiveData<List<SubCategory>>
    lateinit var unCheckedItems: LiveData<List<SubCategory>>

    init {
        val noteDao = NoteDatabase.getDatabaseInstance(context)?.getDao()
        noteRepository = NoteRepository(noteDao!!)
        notes = noteRepository.getAllNotes()
    }

    fun getCategoryItems(category: String): LiveData<List<SubCategory>> {
        categoryItems = noteRepository.getAllCategoryItems(category)
        return categoryItems
    }

    fun getUnCheckedItems(category: String, isCheck:Boolean): LiveData<List<SubCategory>>{
        unCheckedItems = noteRepository.getUncheckedItems(category, isCheck)
        return unCheckedItems
    }

    fun getTotalCost(category: String):Int{
        var ans:Int=0
        viewModelScope.launch (Dispatchers.IO) {
            ans = noteRepository.getTotalCost(category)
        }
        return ans
    }

    suspend fun getListItemByName(name: String): Boolean {
        return noteRepository.getItemByName(name)!=null
    }

    fun insertCategoryItem(category: SubCategory){
        viewModelScope.launch (Dispatchers.IO){
            noteRepository.insertNewSubCategory(category)
        }
    }
    fun updateCategoryItem(category: SubCategory){
        viewModelScope.launch (Dispatchers.IO){
            noteRepository.updateSubCategory(category)
        }
    }

    fun updateListItems(oldCategory: String, newCategory: String){
        viewModelScope.launch (Dispatchers.IO){
            noteRepository.updateListItems(oldCategory, newCategory)
        }
    }

    fun insertNote(note:Note){
        viewModelScope.launch (Dispatchers.IO){
            noteRepository.insertNewNote(note)
        }
    }

    fun deleteNote(note:Note){
        viewModelScope.launch (Dispatchers.IO){
            noteRepository.deleteNote(note)
        }
    }

    fun deleteList(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.deleteList(id)
        }
    }
    fun updateNote(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            noteRepository.updateNote(note)
        }
    }
    fun deleteItem(item:SubCategory){
        viewModelScope.launch(Dispatchers.IO){
            noteRepository.deleteItem(item)
        }
    }
    fun deleteCategoryItems(category: String){
        viewModelScope.launch (Dispatchers.IO){
            noteRepository.deleteCategoryItems(category)
        }
    }
    fun deleteCheckedItems(category: String, isCheck: Boolean){
        viewModelScope.launch (Dispatchers.IO){
            noteRepository.deleteCheckedItems(category, isCheck)
        }
    }
}

class MyViewModelFactory(private val context: Context):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteViewModel(context) as T
    }
}