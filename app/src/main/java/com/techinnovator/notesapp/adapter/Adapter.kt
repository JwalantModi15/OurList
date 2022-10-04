package com.techinnovator.notesapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.techinnovator.notesapp.Common
import com.techinnovator.notesapp.NoteViewModel
import com.techinnovator.notesapp.R
import com.techinnovator.notesapp.model.Note
import com.techinnovator.notesapp.model.SubCategory
import kotlinx.coroutines.*
import java.text.DecimalFormat

class NoteAdapter(var notes: MutableList<Note>, var onClick: OnClick, var noteViewModel: NoteViewModel, var lifecycleOwner: LifecycleOwner, var context: Context): RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    var ans: Double = 0.00
    private var listObserver:Observer<List<SubCategory>>? = null

    fun onDataChange(n: MutableList<Note>){
        notes = n
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_note_layout, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.textView.text = notes[position].note
        holder.txtDate.text = notes[position].date

        var note = notes[position]

        if(note.isSelected){
            holder.cardView.setBackgroundResource(R.drawable.card_background_main)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                holder.cardView.elevation = 4f
            }
        }
        else{
            holder.cardView.setBackgroundResource(R.drawable.card_background)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                holder.cardView.elevation = 4f
            }
        }

        holder.imgMore.setOnClickListener {
            if(!Common.listFlag){
                var popupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.pop_up_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                    var itemId = it.itemId
                    if(itemId==R.id.popRename){
                        onClick.renameList(note)
                    }
                    else if(itemId == R.id.popShare){
                        onClick.shareList(note)
                    }
                    else if(itemId == R.id.popListName){
                        AlertDialog.Builder(context).setMessage(note.note).setNegativeButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }).show()
                    }
//                    else if(itemId == R.id.popDelete){
//                        AlertDialog.Builder(context).setTitle("Delete List").setMessage("Delete this shopping list?")
//                                .setIcon(R.drawable.ic_delete_blue)
//                                .setPositiveButton("Delete", DialogInterface.OnClickListener { dialogInterface, i ->
//                                    if(listObserver!=null)
//                                    {
//                                        noteViewModel.getUnCheckedItems(notes[position].note, true).removeObserver(listObserver!!)
//                                    }
//                                    noteViewModel.deleteNote(note)
//                                    if(listObserver!=null)
//                                    {
//                                        noteViewModel.getUnCheckedItems(notes[position].note, true).removeObserver(listObserver!!)
//                                    }
//                                    noteViewModel.deleteCategoryItems(note.note)
//                                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
//                                })
//                                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
//                                    dialogInterface.dismiss()
//                                })
//                                .setCancelable(false)
//                                .show()
//
//                    }
                    else if(itemId == R.id.popTotalSpend){
                        AlertDialog.Builder(context).setMessage(ans.toString()).setNegativeButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }).show()
                    }
                    return@OnMenuItemClickListener true
                })
                popupMenu.show()
            }
            else{
                onClick.itemClick(position, it)
            }
        }

        listObserver = Observer<List<SubCategory>> {
            ans = 0.00
            for (i in it.indices) {
                ans += it[i].price
            }
            val df = DecimalFormat("#.##")
            val roundOff = df.format(ans)

//            val roundOff = String.format("%.2f", ans)
            if (roundOff == "0.00") {
                holder.txtTotalCost.text = "Total Spend: ₹ 0"
            } else {
                holder.txtTotalCost.text = "Total Spend: ₹ $roundOff"
            }
        }

        noteViewModel.getUnCheckedItems(notes[position].note, true).observe(lifecycleOwner, listObserver!!)
        noteViewModel.getUnCheckedItems(notes[position].note, true).removeObserver(listObserver!!)

        noteViewModel.getUnCheckedItems(notes[position].note, false).observe(lifecycleOwner, Observer {
            holder.txtTasks.visibility = VISIBLE
            if(it.isEmpty()){
                holder.txtTasks.visibility = GONE
                holder.txtTasks.text = ""
            }
            else{
                holder.txtTasks.visibility = VISIBLE
                holder.txtTasks.text = it.size.toString()
            }
        })
//        holder.listLayout.setOnLongClickListener(View.OnLongClickListener {
//            Common.listFlag = true
//            onClick.itemClick(position, it)
//            return@OnLongClickListener true
//        })

        holder.listLayout.setOnClickListener {
//            if(Common.listFlag){
//                onClick.itemClick(position, it)
//            }
//            else{
            onClick.getCategory(note.note, note.id)
//            }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    class NoteViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtTotalCost:TextView = view.findViewById(R.id.txtTotCost)
        val textView:TextView = view.findViewById(R.id.textView)
        var listLayout:ConstraintLayout = view.findViewById(R.id.listLayout)
        val txtTasks: TextView = view.findViewById(R.id.txtTasks)
        val txtDate:TextView = view.findViewById(R.id.txtDate)
        val cardView:CardView = view.findViewById(R.id.cardView)
        val imgMore: ImageView = view.findViewById(R.id.imgMore)
    }

    interface OnClick{
        fun getCategory(category: String, id: Int)
        fun itemClick(position: Int, view: View)
        fun renameList(note: Note)
        fun shareList(note: Note)
        fun deleteList(note: Note)
    }
}

class SubCategoryAdapter(var categories: MutableList<SubCategory>, var onCheck: OnCheckBoxListener, var context: Context, var window: Window): RecyclerView.Adapter<SubCategoryAdapter.SubCategoryViewHolder>() {

    lateinit var parent:ViewGroup
    fun onDataChange(n: MutableList<SubCategory>){
        categories = n
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoryViewHolder {
        this.parent = parent
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item_layout, parent, false)
        return SubCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
        holder.textView.text = categories[position].text
        holder.checkBox.setOnCheckedChangeListener(null) // it is uses to stop the unnecessary calling to checkbox
        holder.checkBox.isChecked = categories[position].isCheck

        var item = categories[position]

        if(item.isSel){
            holder.cardView.setBackgroundResource(R.drawable.card_item_background_main)
        }
        else{
            holder.cardView.setBackgroundResource(R.drawable.card_item_background)
        }

        holder.checkBox.isEnabled = !Common.itemFlag

        if(holder.checkBox.isChecked){
            holder.textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.txtValue.visibility = VISIBLE
            println(categories[position].price.toString())
            if(categories[position].price.toString()!="0.0"){
                var ans = categories[position].price
                val df = DecimalFormat("#.##")
//                val roundOff = String.format("%.2f", ans)
                var roundOff = df.format(ans)
                holder.txtValue.text = "₹ "+roundOff
            }
            else{
                holder.txtValue.text = ""
                holder.txtValue.visibility = GONE
            }
        }
        else{
            holder.textView.paintFlags = 0
            holder.txtValue.text = ""
            holder.txtValue.visibility = GONE
        }

        var view = LayoutInflater.from(context).inflate(R.layout.note_insert_layout, parent, false)
        val et = view.findViewById<EditText>(R.id.editTextVal)
        holder.checkBox.setOnCheckedChangeListener { compoundButton, b ->
            if(!Common.itemFlag){
                if(b){
                    holder.textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    et.requestFocus()
                    AlertDialog.Builder(context).setView(view)
                            .setTitle("Enter cost of the item: ")
                            .setPositiveButton("Add", DialogInterface.OnClickListener { dialogInterface, i ->
                                var str = et.text.toString()
                                if(str==""){
                                    holder.txtValue.visibility = GONE
                                    onCheck.onChecked(true, 0.00, categories[position])
                                }
                                else{
                                    holder.txtValue.text = str
                                    onCheck.onChecked(true, str.toDouble(), categories[position])
                                }

                            })
                            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->
                                holder.txtValue.visibility = GONE
                                onCheck.onChecked(true, 0.00, categories[position])
                                dialogInterface.dismiss() })
                            .setCancelable(false)
                            .show()

                }
                else{
                    onCheck.onChecked(false, 0.00, categories[position])
                    holder.textView.paintFlags = 0
                    holder.txtValue.text = ""
                    holder.txtValue.visibility = GONE
                }
            }

        }

        holder.cardLayout.setOnLongClickListener(View.OnLongClickListener {
            Common.itemFlag = true
            onCheck.onItemLongPress(position, it)
            return@OnLongClickListener true
        })

        holder.cardLayout.setOnClickListener {
            if(Common.itemFlag) {
                onCheck.onItemLongPress(position, it)
            }
            else{
                onCheck.onItemClick(categories[position])
            }
        }

//        holder.imgEdit.setOnClickListener {
//            if(!Common.itemFlag){
//                onCheck.onItemClick(categories[position])
//            }
//        }

//        holder.imgDelete.setOnClickListener {
//            onCheck.onItemDelete(categories[position])
//        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    class SubCategoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView:TextView = view.findViewById(R.id.textView)
        val checkBox:CheckBox = view.findViewById(R.id.checkBox)
        val txtValue:TextView = view.findViewById(R.id.txtValue)
        val cardView:CardView = view.findViewById(R.id.itemCardView)
        val cardLayout:ConstraintLayout = view.findViewById(R.id.cardLayout)
//        val imgEdit: ImageView = view.findViewById(R.id.imgEdit)

//        val imgDelete: ImageView = view.findViewById(R.id.imgDelete)

    }

    interface OnCheckBoxListener{
        fun onChecked(isChecked: Boolean, value: Double, subCategory: SubCategory)
        fun onItemLongPress(position: Int, view: View)
        fun onItemClick(subCategory: SubCategory)
        fun onItemDelete(subCategory: SubCategory)
    }

}

class ItemsAdapter(var list:List<String>, var onItemClickListener: onClickItemListener): RecyclerView.Adapter<ItemsAdapter.ItemHolder>() {

    fun onDataChange(list:List<String>){
        this.list = list
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.inputs_layout, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.txtItem.text = list[position]

        holder.itemLayout.setOnClickListener {
            onItemClickListener.onItemClick(list[position])
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun clickAnimation(): AlphaAnimation{
        return AlphaAnimation(1F, 0.8F)
    }

    class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        val itemLayout: ConstraintLayout = view.findViewById(R.id.itemLayout)
        val txtItem: TextView = view.findViewById(R.id.txtItem)
    }

    interface onClickItemListener{
        fun onItemClick(item:String)
    }
}