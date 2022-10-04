package com.techinnovator.notesapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techinnovator.notesapp.adapter.SubCategoryAdapter
import com.techinnovator.notesapp.model.Note
import com.techinnovator.notesapp.model.SubCategory
import java.text.DecimalFormat
import java.util.*
import java.util.Collections.sort

class SubCategoryActivity : AppCompatActivity(), SubCategoryAdapter.OnCheckBoxListener, ActionMode.Callback {
    lateinit var recyclerView: RecyclerView
    lateinit var noteViewModel:NoteViewModel
    lateinit var subCategoryAdapter: SubCategoryAdapter
//    lateinit var btnAddItems: ImageButton
    lateinit var txtTotalSpent: TextView
    lateinit var cardView: CardView
    lateinit var txtText: TextView
    lateinit var imgItems: ImageView
    lateinit var cardViewAddItem: CardView

    var items = mutableListOf<SubCategory>()
    var actionMode: ActionMode? = null
    lateinit var v:View
    lateinit var layout: ConstraintLayout
    var p:Int = 0
    var category:String = ""
    var itId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_category)

        if(intent!=null){
            supportActionBar?.title = intent.getStringExtra("category")
            category = intent.getStringExtra("category").toString()
            itId = intent.getIntExtra("id", -1)
        }

        recyclerView = findViewById(R.id.subRecyclerView)
        txtTotalSpent = findViewById(R.id.txtTotalSpent)
        cardView = findViewById(R.id.cardViewTAmount)
        txtText = findViewById(R.id.txtText)
        imgItems = findViewById(R.id.imgItems)
        cardViewAddItem = findViewById(R.id.cardViewAddItem)
//        btnAddItems = findViewById(R.id.btnAddItems)

        txtTotalSpent.movementMethod = ScrollingMovementMethod()
//        btnAddItems = findViewById(R.id.btnAddItems)
//        val linearLayoutManager = object: LinearLayoutManager(this){
//            override fun canScrollVertically(): Boolean{
//                return false
//            }
//        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        subCategoryAdapter = SubCategoryAdapter(items, this, this, window)
        recyclerView.adapter = subCategoryAdapter

        noteViewModel = ViewModelProvider(this, MyViewModelFactory(this)).get(NoteViewModel::class.java)
        noteViewModel.getCategoryItems(category).observe(this, {
            items = it as MutableList<SubCategory>
            if(items.isEmpty()){
                txtText.visibility = VISIBLE
                imgItems.visibility = VISIBLE
            }
            else{
                txtText.visibility = GONE
                imgItems.visibility = GONE
            }
            sort(items, kotlin.Comparator { t1, t2 ->
                return@Comparator t1.text.compareTo(t2.text)
            })
            sort(items, kotlin.Comparator { t1, t2 ->
                var a = 0
                var b = 0
                if(t1.isCheck){
                    a = 1
                }
                if(t2.isCheck){
                    b = 1
                }
                return@Comparator a-b
            })
            subCategoryAdapter.onDataChange(items)
        })

        var ans=0.0
        noteViewModel.getUnCheckedItems(category, true).observe(this, androidx.lifecycle.Observer {
            ans = 0.0
            for (i in it.indices) {
                ans += it[i].price
            }
            if(ans==0.0){
                cardView.visibility = GONE
            }
            else{
                cardView.visibility = VISIBLE
                val df = DecimalFormat("#.##")
                val roundOff = df.format(ans)
//                val roundOff = String.format("%.2f", ans)
                txtTotalSpent.text = "Total Spend: ₹ $roundOff"
            }
        })

//        btnAddItems.setOnClickListener {
//            if(actionMode!=null){
//                actionMode!!.finish()
//                actionMode = null
//            }
//
//            var intent = Intent(this, InsertItemActivity::class.java)
//            intent.putExtra("category", category)
//            startActivity(intent)
//        }

        cardViewAddItem.setOnClickListener {
            if(actionMode!=null){
                actionMode!!.finish()
                actionMode = null
            }

            var intent = Intent(this, InsertItemActivity::class.java)
            intent.putExtra("category", category)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemId = item.itemId
        if(itemId == R.id.add){
            var intent = Intent(this, InsertItemActivity::class.java)
            intent.putExtra("category", category)
            startActivity(intent)
        }
        else if(itemId == android.R.id.home){
            onBackPressed()
        }
        else if(itemId == R.id.deleteList){
            val n = Note(category, "", false)
            n.id = itId
            AlertDialog.Builder(this).setTitle("Delete List").setMessage("Delete this shopping list?")
                    .setIcon(R.drawable.ic_delete_blue)
                    .setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                        noteViewModel.deleteNote(n)
                        noteViewModel.deleteCategoryItems(category)
                        Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                    .setCancelable(false)
                    .show()

        }
        else if(itemId == R.id.deletePurchaseItems){
            if(items.isNotEmpty()){
                var flag = false
                for(item in items){
                    if(item.isCheck){
                        flag = true
                    }
                }
                if(flag){
                    AlertDialog.Builder(this).setTitle("Delete Items").setMessage("Are you want to delete purchased items?")
                            .setIcon(R.drawable.ic_delete_blue)
                            .setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                                noteViewModel.deleteCheckedItems(category, true)
                                Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                            })
                            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                                dialogInterface.dismiss()
                            })
                            .setCancelable(false)
                            .show()
                }
            }
        }
        else if(itemId==R.id.showListName){
            android.app.AlertDialog.Builder(this).setMessage(category).setNegativeButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            }).show()
        }
        return true
    }
    override fun onChecked(isChecked: Boolean, value: Double, subCategory: SubCategory) {
        subCategory.isCheck = isChecked
        subCategory.price = value
        noteViewModel.updateCategoryItem(subCategory)
    }

    override fun onItemLongPress(position: Int, view: View) {
        if(actionMode==null){
            actionMode = startSupportActionMode(this)
        }

        items[position].isSel = !items[position].isSel
        var flag = false
        for(i in items){
            if(i.isSel){
                flag = true
            }
        }
        if(!flag){
            Common.itemFlag = false
            actionMode!!.finish()
        }
        subCategoryAdapter.onDataChange(items)
    }

    override fun onItemClick(subCategory: SubCategory) {
        var intent = Intent(this, CategoryInsertActivity::class.java)
        var id = subCategory.id
        var text = subCategory.text
        var price = subCategory.price
        var cate = subCategory.category
        var isCheck = subCategory.isCheck
        var isSelected = subCategory.isSel

        intent.putExtra("id", id)
        intent.putExtra("text", text)
        intent.putExtra("price", price)
        intent.putExtra("cate", cate)
        intent.putExtra("isCheck", isCheck)
        intent.putExtra("isSelected", isSelected)

        startActivity(intent)
    }

    override fun onItemDelete(subCategory: SubCategory) {
        AlertDialog.Builder(this).setTitle("Delete Items").setMessage("Delete this selected list items?")
                .setIcon(R.drawable.ic_delete_blue)
                .setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                    noteViewModel.deleteItem(subCategory)
                    items.remove(subCategory)
                    Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()

                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .setCancelable(false)
                .show()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        var itemId = item?.itemId
        if(itemId == R.id.delete){
            AlertDialog.Builder(this).setTitle("Delete Items").setMessage("Delete this selected list items?")
                    .setIcon(R.drawable.ic_delete_blue)
                    .setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                        var temp = items
                        for(it in  temp){
                            if(it.isSel){
                                noteViewModel.deleteItem(it)
                            }
                        }
                        Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        Common.itemFlag = false
                        actionMode?.finish()

                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                        Common.itemFlag = false
                        actionMode?.finish()
                        dialogInterface.dismiss()
                    })
                    .setCancelable(false)
                    .show()
        }
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        Common.itemFlag = false
        for(it in items){
            if(it.isSel){
                it.isSel = false
            }
        }
        subCategoryAdapter.onDataChange(items)
    }
}