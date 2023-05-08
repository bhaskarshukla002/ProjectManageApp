package com.example.projectmanageapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanageapp.R
import com.example.projectmanageapp.activities.TaskListActivity
import com.example.projectmanageapp.databinding.ItemTaskBinding
import com.example.projectmanageapp.firebase.FireStoreClass
import com.example.projectmanageapp.models.Board
import com.example.projectmanageapp.models.Task

open class TaskListItemsAdapter(private val context: Context,private var list:ArrayList<Task>):RecyclerView.Adapter<TaskListItemsAdapter.ViewHolder>(){

//    private var onClickListener:OnClickListener?=null
    inner class ViewHolder(binding: ItemTaskBinding):RecyclerView.ViewHolder(binding.root){
            val binding_=binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutParams=LinearLayout.LayoutParams(
            (parent.width*0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins((15.toDp().toPx()),0,(40.toDp().toPx()),0)
        binding.root.layoutParams=layoutParams
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model=list[position]
        if(position == list.size-1){
            holder.binding_.tvAddTaskList.visibility= View.VISIBLE
            holder.binding_.llTaskItem.visibility=View.GONE
        }else{
            holder.binding_.tvAddTaskList.visibility= View.GONE
            holder.binding_.llTaskItem.visibility=View.VISIBLE
        }
        holder.binding_.tvTaskListTitle.text=model.title
        holder.binding_.tvAddTaskList.setOnClickListener{
            holder.binding_.tvAddTaskList.visibility= View.GONE
            holder.binding_.cvAddTaskListName.visibility=View.VISIBLE
        }
        holder.binding_.ibCloseListName.setOnClickListener{
            holder.binding_.tvAddTaskList.visibility= View.VISIBLE
            holder.binding_.cvAddTaskListName.visibility=View.GONE
        }

        holder.binding_.ibDoneListName.setOnClickListener {
            val listName=holder.binding_.etTaskListName.text.toString()
            if(listName.isNotEmpty()){
                if(context is TaskListActivity)
                 context.createTaskList(listName)
            }else{
                Toast.makeText(context,"Please Enter TaskList Name",Toast.LENGTH_SHORT).show()
            }
        }
        holder.binding_.ibEditListName.setOnClickListener {
            holder.binding_.etEditTaskListName.setText(model.title)
            holder.binding_.llTitleView.visibility=View.GONE
            holder.binding_.cvEditTaskListName.visibility=View.VISIBLE
        }
        holder.binding_.ibCloseEditableView.setOnClickListener {
            holder.binding_.llTitleView.visibility=View.VISIBLE
            holder.binding_.cvEditTaskListName.visibility=View.GONE
        }
        holder.binding_.ibDoneEditListName.setOnClickListener {
            val listName = holder.binding_.etEditTaskListName.text.toString()
            if(listName.isNotEmpty()) {
                if(context is TaskListActivity){
                    context.updateTaskList(position,listName,model)
                }
            }
            else{
                Toast.makeText(context,"Please Enter List Name.",Toast.LENGTH_SHORT).show()
            }
        }
        holder.binding_.ibDeleteList.setOnClickListener {

        }
    }

    private fun alertDialogForDeleteList(position: Int,title:String){
        val builder=AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

    }

    private fun Int.toDp(): Int=
        (this/ Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int=
        (this* Resources.getSystem().displayMetrics.density).toInt()
}