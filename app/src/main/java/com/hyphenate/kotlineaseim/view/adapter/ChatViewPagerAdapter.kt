package com.hyphenate.kotlineaseim.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hyphenate.kotlineaseim.view.ui.fragment.ChatFragment
import com.hyphenate.kotlineaseim.view.ui.fragment.QAFragment


class ChatViewPagerAdapter(fragment: Fragment):FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2


    override fun createFragment(position: Int): Fragment {
        var fragment = ChatFragment()
        if(position == 1){
            return QAFragment()
        }
        return fragment
    }


}