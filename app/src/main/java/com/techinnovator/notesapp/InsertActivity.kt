package com.techinnovator.notesapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.techinnovator.notesapp.model.Note
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class InsertActivity : AppCompatActivity() {
    lateinit var noteViewModel:NoteViewModel
    lateinit var editText: TextInputEditText
    lateinit var editLayout: TextInputLayout
    lateinit var btnSave: Button
    lateinit var btnCancel: Button

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)

        title = "Add List"
        editText = findViewById(R.id.editText)

        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        editLayout = findViewById(R.id.editLayout)

        editText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        noteViewModel = ViewModelProvider(this, MyViewModelFactory(this)).get(NoteViewModel::class.java)
        var context = this
        btnSave.setOnClickListener {
            var text = editText.text.toString()
            if(text==""){
                editText.error = "This field cannot be blank"
            }
            else{
                CoroutineScope(Dispatchers.IO).launch {
                    var res = noteViewModel.getListItemByName(text)
                    withContext(Dispatchers.Main){
                        if(!res){
                            var date = Date()
                            var simpleDateFormat = SimpleDateFormat("dd-MM-yyyy")
                            var dateTime = simpleDateFormat.format(date)
                            var note = Note(text, dateTime, false)
                            noteViewModel.insertNote(note)
//                            var intent = Intent(context, SubCategoryActivity::class.java)
//                            intent.putExtra("category", text)
//                            intent.putExtra("id", note.id)
//                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(context, "List name already exists", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

        }

        btnCancel.setOnClickListener {
            onBackPressed()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            onBackPressed()
        }
        return true
    }
}

