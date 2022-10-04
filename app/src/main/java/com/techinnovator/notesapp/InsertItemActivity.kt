 package com.techinnovator.notesapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.RecognizerIntent.EXTRA_RESULTS
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.techinnovator.notesapp.adapter.ItemsAdapter
import com.techinnovator.notesapp.model.SubCategory
import java.util.*
import java.util.Collections.sort


 class InsertItemActivity : AppCompatActivity(), ItemsAdapter.onClickItemListener {
     lateinit var etInput: EditText
     lateinit var btnAdd: Button
     lateinit var btnSpeechToText: ImageButton
     lateinit var noteViewModel: NoteViewModel
     lateinit var recyclerView: RecyclerView
     lateinit var list:List<String>
     lateinit var adapter: ItemsAdapter
     lateinit var txtSugg: TextView
     lateinit var adRequest:AdRequest
     var category:String = ""
     lateinit var mAdView: AdView

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_item)
        title = "Add List Item"

        etInput = findViewById(R.id.etInput)
        btnAdd = findViewById(R.id.btnAdd)
        recyclerView = findViewById(R.id.optionsRecyclerView)
        btnSpeechToText = findViewById(R.id.btnSpeechToText)
        txtSugg = findViewById(R.id.noSuggestions)

        etInput.requestFocus()

        if(intent!=null) {
            category = intent.getStringExtra("category").toString()
        }

        list = resources.getStringArray(R.array.list_items).toList()
         sort(list)

        adapter = ItemsAdapter(list, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        recyclerView.adapter = adapter

        noteViewModel = ViewModelProvider(this, MyViewModelFactory(this)).get(NoteViewModel::class.java)

         etInput.addTextChangedListener {
             if (it != null) {
                 if(it.isEmpty()){
                     adapter.onDataChange(list)
                 }
                 else{
                     var t: MutableList<String> = mutableListOf()
                     for (l in list) {
                         if (l.contains(it.trim())) {
                             t.add(l)
                         }
                     }
                     if(t.isEmpty()){
                         adapter.onDataChange(t)
                         recyclerView.visibility = GONE
                         txtSugg.visibility = VISIBLE
                     }
                     else {
                         recyclerView.visibility = VISIBLE
                         txtSugg.visibility = GONE
                         adapter.onDataChange(t)
                     }
                 }
             }
         }

         btnAdd.setOnClickListener {
             var text = etInput.text.toString()
             if(text!=""){
                 if(category!=""){
                     var subCategory = SubCategory(category, text, false, 0.00, false)
                     noteViewModel.insertCategoryItem(subCategory)
                     onBackPressed()
                 }

             }
             else{
                 Toast.makeText(this, "List item is empty", Toast.LENGTH_SHORT).show()
             }
         }

         btnSpeechToText.setOnClickListener {
             var intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
             intent.putExtra(
                     RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                     RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
             )
             intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
             intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak item name")
             try {
                 startActivityForResult(intent, 10)
             }
             catch (e: Exception){

             }
         }

         mAdView = findViewById(R.id.adView)
         Handler().postDelayed(Runnable {
             MobileAds.initialize(this) {}
             mAdView.visibility = VISIBLE
             adRequest = AdRequest.Builder().build()
             mAdView.loadAd(adRequest)
         }, 8000)

         mAdView.adListener = object : AdListener(){
             override fun onAdClicked() {
                 // Code to be executed when the user clicks on an ad.
                 super.onAdClicked()
                 mAdView.visibility = View.GONE
                 Handler().postDelayed(Runnable {
                     mAdView.visibility = View.VISIBLE
                 }, 60000*2)
             }

             override fun onAdClosed() {
                 // Code to be executed when the user is about to return
                 // to the app after tapping on an ad.
             }

             override fun onAdFailedToLoad(adError : LoadAdError) {
                 // Code to be executed when an ad request fails.
                 super.onAdFailedToLoad(adError)
                 mAdView.loadAd(adRequest)
             }

             override fun onAdImpression() {
                 // Code to be executed when an impression is recorded
                 // for an ad.
             }

             override fun onAdLoaded() {
                 // Code to be executed when an ad finishes loading.
                 super.onAdLoaded()
             }

             override fun onAdOpened() {
                 // Code to be executed when an ad opens an overlay that
                 // covers the screen.
                 super.onAdOpened()
             }

         }

    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         if(requestCode==10 && resultCode== RESULT_OK){
             if(data!=null){
                 var str = data.getStringArrayListExtra(EXTRA_RESULTS)?.get(0).toString()
                 if(str=="null"){
                     Toast.makeText(this, "Unable to listen!", Toast.LENGTH_SHORT).show()
                 }
                 else{
                     etInput.setText("")
                     etInput.setText(str.toLowerCase())
                 }

             }
         }
     }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            onBackPressed()
        }
        return true
    }

    override fun onItemClick(item: String) {
        if(category!=""){
            var subCategory = SubCategory(category, item, false, 0.00, false)
            noteViewModel.insertCategoryItem(subCategory)
            onBackPressed()
        }
    }

}