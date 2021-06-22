package com.hyphenate.kotlineaseim.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.model.User

class MembersAdapter: RecyclerView.Adapter<MembersAdapter.ViewHolder>() {
    private var data: List<User> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.member_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = if(getItem(position).name.isNotEmpty()) getItem(position).name else getItem(position).id
    }

    override fun getItemCount(): Int = data.size

    fun setData(data: List<User>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun getItem(position: Int): User{
        return data[position]
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val avatar = view.findViewById<ImageView>(R.id.iv_avatar)
        val name = view.findViewById<TextView>(R.id.tv_name)
        val role = view.findViewById<TextView>(R.id.tv_role)
    }
}