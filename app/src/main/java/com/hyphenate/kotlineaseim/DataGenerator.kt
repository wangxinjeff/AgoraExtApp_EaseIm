package com.hyphenate.kotlineaseim

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

class DataGenerator {

    companion object{
        fun getTabView(context: Context, title:String) : View {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.re_tab_item_layout, null)
            var text1 = view.findViewById<TextView>(R.id.title)
            text1.text = title
            return view
        }
    }
}