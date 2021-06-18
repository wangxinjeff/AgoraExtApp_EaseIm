package com.hyphenate.kotlineaseim

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

class DataGenerator {

    companion object{
        private val textList = arrayOf("聊天","问答","成员(99+)")
        fun getTabView(context: Context, position:Int) : View {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.re_tab_item_layout, null)
            var text1 = view.findViewById<TextView>(R.id.title)
            text1.text = textList[position]
            return view
        }
    }
}