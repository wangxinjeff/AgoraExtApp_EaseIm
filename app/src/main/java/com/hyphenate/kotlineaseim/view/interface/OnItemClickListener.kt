package com.hyphenate.kotlineaseim.view.`interface`

import android.view.View

interface OnItemClickListener {

    /**
     * 条目点击
     */
    fun onItemClick(view: View, position:Int)
}