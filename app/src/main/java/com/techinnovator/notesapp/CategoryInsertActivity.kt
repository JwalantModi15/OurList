package com.techinnovator.notesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.techinnovator.notesapp.model.Note
import com.techinnovator.notesapp.model.SubCategory
import java.text.DecimalFormat

class CategoryInsertActivity : AppCompatActivity() {
    lateinit var noteViewModel:NoteViewModel
    lateinit var editText: EditText
    lateinit var btnSave: Button
    lateinit var btnCancel: Button
    lateinit var editLayout: TextInputLayout
    lateinit var etUpdatePrice: EditText

    var id:Int = 0
    var text:String = ""
    var price:Double = 0.0
    var cate:String = ""
    var isCheck:Boolean = false
    var isSelected:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_insert)
        title = "Update List Item"

        editText = findViewById(R.id.editText)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        editLayout = findViewById(R.id.editLayout2)
        etUpdatePrice = findViewById(R.id.etUpdatePrice)
//        var category:String = ""

        editText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        if(intent!=null){
//            category = intent.getStringExtra("category").toString()
            id = intent.getIntExtra("id", -1)
            text = intent.getStringExtra("text").toString()
            price = intent.getDoubleExtra("price", 0.00)
            cate = intent.getStringExtra("cate").toString()
            isCheck = intent.getBooleanExtra("isCheck", false)
            isSelected = intent.getBooleanExtra("isSelected", false)
        }

        if(isCheck){
            editLayout.visibility = VISIBLE
            val df = DecimalFormat("#.##")
            val ans = df.format(price)
            etUpdatePrice.setText(ans)
        }

        noteViewModel = ViewModelProvider(this, MyViewModelFactory(this)).get(NoteViewModel::class.java)
        if(text!="null"){
            editText.setText(text)
            btnSave.setOnClickListener {
                updateDatabase()
            }
        }
        btnCancel.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tick_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            onBackPressed()
        }
        else if(item.itemId == R.id.done){
            updateDatabase()
        }
        return true
    }
    private fun updateDatabase(){
        var str = editText.text.toString()
        if(str!=""){
            if(!isCheck){
                var subCategory = SubCategory(cate, str, isCheck, price, isSelected)
                subCategory.id = id
                noteViewModel.updateCategoryItem(subCategory)
            }
            else{
                var pr = etUpdatePrice.text.toString()
                if(pr==""){
                    var subCategory = SubCategory(cate, str, isCheck, 0.00, isSelected)
                    subCategory.id = id
                    noteViewModel.updateCategoryItem(subCategory)
                }
                else{
                    var subCategory = SubCategory(cate, str, isCheck, pr.toDouble(), isSelected)
                    subCategory.id = id
                    noteViewModel.updateCategoryItem(subCategory)
                }
            }
            onBackPressed()
        }
        else{
            editText.error = "This field cannot be blank"
        }
    }
}