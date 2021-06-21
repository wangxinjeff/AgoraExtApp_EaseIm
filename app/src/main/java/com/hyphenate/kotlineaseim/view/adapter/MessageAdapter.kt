package com.hyphenate.kotlineaseim.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.view.`interface`.MessageListItemClickListener
import com.hyphenate.kotlineaseim.view.viewholder.ChatRowViewHolder
import com.hyphenate.kotlineaseim.view.viewholder.ImageViewHolder
import com.hyphenate.kotlineaseim.view.viewholder.TextViewHolder

class MessageAdapter(private val fragmentNum: Int) : RecyclerView.Adapter<ChatRowViewHolder>() {

    companion object {
        const val DIRECT_TXT_SEND: Int = 1
        const val DIRECT_TXT_REC: Int = 2
        const val DIRECT_IMG_SEND: Int = 3
        const val DIRECT_IMG_REC: Int = 4
    }

    lateinit var context: Context
    private var data: List<EMMessage> = mutableListOf()
    private lateinit var itemClickListener: MessageListItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRowViewHolder {
        context = parent.context
        return getViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: ChatRowViewHolder, position: Int) {
        if (data.isNotEmpty()) {
            val message = getItem(position)
            holder.setUpView(message)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).direct() == EMMessage.Direct.SEND) {
            when (getItem(position).type) {
                EMMessage.Type.TXT -> DIRECT_TXT_SEND
                EMMessage.Type.IMAGE -> DIRECT_IMG_SEND
                else -> DIRECT_TXT_SEND
            }
        } else {
            when (getItem(position).type) {
                EMMessage.Type.TXT -> DIRECT_TXT_REC
                EMMessage.Type.IMAGE -> DIRECT_IMG_REC
                else -> DIRECT_TXT_SEND
            }
        }
    }

    private fun getItem(position: Int): EMMessage {
        return data[position]
    }

    private fun getViewHolder(parent: ViewGroup, viewType: Int): ChatRowViewHolder {
        return when (viewType) {
            DIRECT_TXT_SEND -> TextViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.send_message_item, parent, false), itemClickListener, fragmentNum
            )
            DIRECT_TXT_REC -> TextViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recv_message_item, parent, false), itemClickListener, fragmentNum
            )
            DIRECT_IMG_SEND -> ImageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.send_img_message_item, parent, false), itemClickListener, fragmentNum
            )
            DIRECT_IMG_REC -> ImageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recv_img_message_item, parent, false), itemClickListener, fragmentNum
            )
            else -> TextViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.send_message_item, parent, false), itemClickListener, fragmentNum
            )
        }
    }

    fun setMessageListItemClickListener(itemClickListener: MessageListItemClickListener) {
        this.itemClickListener = itemClickListener
    }


    fun setData(data: List<EMMessage>) {
        this.data = data
        notifyDataSetChanged()
    }

}