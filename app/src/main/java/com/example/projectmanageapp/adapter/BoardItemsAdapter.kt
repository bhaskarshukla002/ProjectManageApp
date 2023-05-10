package com.example.projectmanageapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanageapp.R
import com.example.projectmanageapp.databinding.ItemBoardBinding
import com.example.projectmanageapp.models.Board

open class BoardItemsAdapter(
    private val context:Context,
    private var list:ArrayList<Board>
    )
    :RecyclerView.Adapter<BoardItemsAdapter.ViewHolder>() {

    private var onClickListener: OnClickListener?=null

    inner class ViewHolder(binding: ItemBoardBinding):RecyclerView.ViewHolder(binding.root){
        val ivBoardImage = binding.ivBoardImage
        val tvName = binding.tvName
        val tvCreatedBy=binding.tvCreatedBy
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        Glide.with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_board_place_holder)
            .into(holder.ivBoardImage)
        holder.tvName.text=model.name
        holder.tvCreatedBy.text= "Created by: ${model.createdBy}"
        holder.itemView.setOnClickListener { onClickListener?.onClick(position,model) }
    }

    override fun getItemCount(): Int = list.size

    interface OnClickListener {
        fun onClick(position: Int,model: Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener= onClickListener
    }

}