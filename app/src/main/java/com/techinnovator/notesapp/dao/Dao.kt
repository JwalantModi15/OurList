package com.techinnovator.notesapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.techinnovator.notesapp.model.Note
import com.techinnovator.notesapp.model.SubCategory

@Dao
interface NoteDao{

    @Insert
    suspend fun insertNode(note: Note)

    @Delete
    suspend fun deleteNode(note: Note)

    @Query("delete from Note where id=:id")
    suspend fun deleteNote(id: Int)

    @Query("delete from SubCategory where category = :category")
    suspend fun deleteSubCategoryItems(category: String)

    @Query("delete from SubCategory where category = :category and isCheck = :isCheck")
    suspend fun deleteCheckedItems(category: String, isCheck: Boolean)

    @Query("select * from Note order by id asc")
    fun getNotes(): LiveData<List<Note>>

    @Query("select sum(price) from SubCategory where category = :category")
    suspend fun getTotalCost(category: String):Int

    @Insert
    suspend fun insertSubCategory(subCategory: SubCategory)

    @Update
    suspend fun updateSubCategory(subCategory: SubCategory)

    @Update
    suspend fun updateList(note: Note)

    @Query("update SubCategory set category = :newCategory where category = :oldCategory")
    suspend fun updateItems(oldCategory: String, newCategory: String)

    @Query("select * from SubCategory where category = :category order by id asc")
    fun getTextFromCategory(category: String): LiveData<List<SubCategory>>

    @Query("select * from SubCategory where category = :category and isCheck = :isCheck order by id asc")
    fun getUncheckedCategories(category: String, isCheck:Boolean):LiveData<List<SubCategory>>

    @Query("select * from Note where note = :name")
    suspend fun getListItemByName(name: String):Note

    @Delete
    suspend fun deleteItem(item: SubCategory)
}