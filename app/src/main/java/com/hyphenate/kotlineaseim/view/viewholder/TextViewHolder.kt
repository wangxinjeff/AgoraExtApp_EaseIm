package com.hyphenate.kotlineaseim.view.viewholder

import android.view.View
import android.widget.TextView
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.view.`interface`.MessageListItemClickListener

class TextViewHolder(view: View,
                     itemClickListener: MessageListItemClickListener
) : ChatRowViewHolder(view, itemClickListener) {
    val content: TextView = itemView.findViewById(R.id.tv_content)
    override fun onSetUpView() {
        val body = message.body as EMTextMessageBody
        content.text = body.message
    }
}