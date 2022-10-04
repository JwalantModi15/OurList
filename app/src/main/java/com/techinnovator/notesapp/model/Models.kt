package com.techinnovator.notesapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Note")
data class Note(var note:String, var date:String, var isSelected: Boolean){
    @PrimaryKey(autoGenerate = true)
    var id = 0
}

@Entity(tableName = "SubCategory")
data class SubCategory(var category:String, var text:String, var isCheck: Boolean, var price: Double, var isSel: Boolean){
    @PrimaryKey(autoGenerate = true)
    var id = 0
}