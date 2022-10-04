package com.techinnovator.notesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.techinnovator.notesapp.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class RenameListActivity : AppCompatActivity() {

    lateinit var noteViewModel:NoteViewModel
    lateinit var etRename: TextInputEditText
    lateinit var btnSave: Button
    lateinit var btnCancel: Button
    var id = 0
    var text = ""
    var date = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rename_list)

        title = "Rename list"
        etRename = findViewById(R.id.etRename)
        btnSave = findViewById(R.id.btnRNSave)
        btnCancel = findViewById(R.id.btnRNCancel)

        etRename.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        if(intent!=null){
            id = intent.getIntExtra("id", 0)
            text = intent.getStringExtra("text").toString()
            date = intent.getStringExtra("date").toString()
        }

        etRename.setText(text)
        noteViewModel = ViewModelProvider(this, MyViewModelFactory(this)).get(NoteViewModel::class.java)

        btnSave.setOnClickListener {
            renamed()
        }

        btnCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun renamed(){
        var str = etRename.text.toString()
        CoroutineScope(Dispatchers.IO).launch {
            var res = noteViewModel.getListItemByName(str)
            withContext(Dispatchers.Main){
                if(!res){
                    if(str!=""){
                        var note = Note(str, date, false)
                        note.id = id
                        noteViewModel.updateListItems(text, note.note)
                        noteViewModel.updateNote(note)
                        Toast.makeText(this@RenameListActivity, "List renamed successfully", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    }
                    else{
                        etRename.error = "This field cannot be blank"
//                        Toast.makeText(applicationContext, "List name is empty", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@RenameListActivity, "List name already exists", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tick_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if(itemId==R.id.done){
            renamed()
        }
        else if(itemId == android.R.id.home){
            onBackPressed()
        }
        return true
    }
}