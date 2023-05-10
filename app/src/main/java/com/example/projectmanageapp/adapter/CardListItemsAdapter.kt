package com.example.projectmanageapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectmanageapp.databinding.ItemCardBinding
import com.example.projectmanageapp.models.Board
import com.example.projectmanageapp.models.Card


open class CardListItemsAdapter(
    private val context: Context,
    private var list:ArrayList<Card>
    ): RecyclerView.Adapter<CardListItemsAdapter.ViewHolder>(){

    private var onClickListener: OnClickListener?=null
    inner class ViewHolder(binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root){
        val binding_=binding
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int =list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val model=list[position]

        holder.binding_.tvCardName.text=model.name

    }
    interface OnClickListener {
        fun onClick(position: Int,model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener= onClickListener
    }
}