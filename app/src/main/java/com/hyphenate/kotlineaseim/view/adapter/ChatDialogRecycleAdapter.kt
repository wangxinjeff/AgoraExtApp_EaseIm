package com.hyphenate.kotlineaseim.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.view.`interface`.OnItemClickListener

class ChatDialogRecycleAdapter(list : List<String>) : RecyclerView.Adapter<ChatDialogRecycleAdapter.ViewHoder>() {

    private val list = list
    lateinit var onItemClickListener: OnItemClickListener

    class ViewHoder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContent:TextView = itemView.findViewById(R.id.tv_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoder {
        return ViewHoder(LayoutInflater.from(parent.context).inflate(R.layout.dialog_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHoder, position: Int) {
        holder.tvContent.text = list[position]
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(it, position)
        }
    }

    override fun getItemCount(): Int = list.size

}