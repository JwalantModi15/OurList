package com.techinnovator.notesapp

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techinnovator.notesapp.adapter.NoteAdapter
import com.techinnovator.notesapp.model.Note
import java.util.*

class MainActivity : AppCompatActivity(), NoteAdapter.OnClick, ActionMode.Callback{
    lateinit var recyclerView: RecyclerView
    lateinit var noteViewModel:NoteViewModel
    lateinit var noteAdapter: NoteAdapter
    lateinit var imageButton: ImageButton
    lateinit var imgHome: ImageView
    lateinit var txtHome: TextView
    var noteList = mutableListOf<Note>()
    var actionMode: ActionMode? = null
    lateinit var callback: ActionMode.Callback
    lateinit var v: View
    var p:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = "My Lists"

        recyclerView = findViewById(R.id.recyclerView)
        imageButton = findViewById(R.id.imageButton)
        imgHome = findViewById(R.id.imgHome)
        txtHome = findViewById(R.id.txtHome)

        recyclerView.layoutManager = LinearLayoutManager(this)
        noteViewModel = ViewModelProvider(this, MyViewModelFactory(this)).get(NoteViewModel::class.java)

        noteAdapter = NoteAdapter(noteList, this, noteViewModel, this, this)
        recyclerView.adapter = noteAdapter


        noteViewModel.notes.observe(this, Observer {
            noteList = it as MutableList<Note>
            if (noteList.size > 0) {
                imgHome.visibility = GONE
                txtHome.visibility = GONE
            } else {
                imgHome.visibility = VISIBLE
                txtHome.visibility = VISIBLE
            }
            Collections.reverse(noteList)
            noteAdapter.onDataChange(noteList)
        })


        imageButton.setOnClickListener {
            if(actionMode!=null){
                actionMode!!.finish()
                actionMode = null
            }
            startActivity(Intent(this, InsertActivity::class.java))
        }

    }

    override fun getCategory(category: String, id: Int){
        var intent = Intent(this, SubCategoryActivity::class.java)
        intent.putExtra("category", category)
        intent.putExtra("id", id)
        startActivity(intent)
    }

    override fun itemClick(position: Int, view: View) {
        if(actionMode==null){
            actionMode = startSupportActionMode(this)
        }
        noteList[position].isSelected = !noteList[position].isSelected
        var flag = false
        for(n in noteList){
            if(n.isSelected){
                flag = true
            }
        }
        if(!flag){
            Common.listFlag = false
            actionMode!!.finish()
        }
        noteAdapter.onDataChange(noteList)
    }

    override fun renameList(note: Note) {
        var intent = Intent(this, RenameListActivity::class.java)
        intent.putExtra("text", note.note)
        intent.putExtra("date", note.date)
        intent.putExtra("id", note.id)
        startActivity(intent)

    }

    override fun shareList(note: Note) {
        var noter: Note?
        noter = note
        if(noter!=null){
            var cate = noter.note
            var date = noter.date
            noteViewModel.getCategoryItems(cate).observe(this, Observer {
                if (noter != null) {
                    AlertDialog.Builder(this).setTitle("Share List").setMessage("Share your shopping list with or without price.").setPositiveButton("Without Price", DialogInterface.OnClickListener { dialogInterface, i ->
                        var str = ""
                        var n = 1
                        str += "$cate\n"
                        for (item in it) {
                            str += "$n. ${item.text}\n"
                            n++
                        }
                        str += "Dt: $date"
                        if (str.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, str)
                            startActivityForResult(Intent.createChooser(intent, "Share"), 50)
                        }
                        noter = null

                    }).setNegativeButton("With Price", DialogInterface.OnClickListener { dialogInterface, i ->
                        var str = ""
                        var n = 1
                        str += "$cate\n"
                        for (item in it) {
                            str += "$n. ${item.text}"
                            str += " - ₹ ${item.price}\n"
                            n++
                        }
                        str += "Dt: $date"
                        if (str.isNotEmpty()) {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, str)
                            startActivityForResult(Intent.createChooser(intent, "Share"), 50)
                        }
                        noter = null

                    }).show()
                    noter = null
                }
            })
        }
    }

    override fun deleteList(note: Note) {
        AlertDialog.Builder(this).setTitle("Delete List").setMessage("Delete this shopping list?")
                .setIcon(R.drawable.ic_delete_blue)
                .setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
                    noteViewModel.deleteNote(note)
                    noteViewModel.deleteCategoryItems(note.note)
                    Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                    dialogInterface.dismiss()
                })
                .setCancelable(false)
                .show()

    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        var itemId = item?.itemId
        if(itemId == R.id.delete){
            AlertDialog.Builder(this).setTitle("Delete Lists").setMessage("Delete this selected shopping lists?")
                    .setIcon(R.drawable.ic_delete_blue)
                    .setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->

                        var temp = noteList
                        for (not in temp) {
                            if (not.isSelected) {
                                noteViewModel.deleteNote(not)
                                noteViewModel.deleteCategoryItems(not.note)
                            }
                        }
                        Toast.makeText(this, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        Common.listFlag = false
                        actionMode?.finish()


                    })
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                        Common.listFlag = false
                        actionMode?.finish()
                        dialogInterface.dismiss()
                    })
                    .setCancelable(false)
                    .show()
        }

        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 50){
            actionMode?.finish()
            Common.listFlag = false
        }
    }
    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        Common.listFlag = false

        for(not in noteList){
            if(not.isSelected){
                not.isSelected = false
            }
        }
        noteAdapter.onDataChange(noteList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemId = item.itemId
        if(itemId == R.id.addList){
            if(actionMode!=null){
                actionMode!!.finish()
                actionMode = null
            }
            startActivity(Intent(this, InsertActivity::class.java))
        }
        else if(itemId == R.id.about){
            AlertDialog.Builder(this).setTitle("About OurList App")
                    .setMessage("OurList - My Grocery List App is brought to you by TechInnovator.\n\nEmail: " +
                            "techinnovator15@gmail.com")
                    .setNegativeButton("Rate It", DialogInterface.OnClickListener { dialogInterface, i ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techinnovator.notesapp"))
                        startActivity(intent)
                    })
                    .setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() })
                    .show()
        }
        else if(itemId == R.id.rate){
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.techinnovator.notesapp"))
            startActivity(intent)
        }
        else if(itemId == R.id.privacyPolicy){
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://privacypolicyourlistapp.blogspot.com/2022/08/ourlist-my-grocery-list-app-privacy.html"))
            startActivity(intent)
        }
        return true
    }

}