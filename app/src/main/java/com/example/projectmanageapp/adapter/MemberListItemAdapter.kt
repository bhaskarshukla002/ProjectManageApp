package com.example.projectmanageapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projectmanageapp.R
import com.example.projectmanageapp.databinding.ItemMemberBinding
import com.example.projectmanageapp.models.User


open class MemberListItemAdapter(
    private val context: Context,
    private val list : ArrayList<User>) :
    RecyclerView.Adapter<MemberListItemAdapter.ViewHolder>()   {

    inner class ViewHolder(binding:ItemMemberBinding):RecyclerView.ViewHolder(binding.root){
        val binding=binding
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=ItemMemberBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]

        Glide.with(context)
            .load(model.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(holder.binding.ivMemberImage)

        holder.binding.tvMemberName.text = model.name
        holder.binding.tvMemberEmail.text = model.email

    }
}