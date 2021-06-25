package com.hyphenate.kotlineaseim.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hyphenate.kotlineaseim.view.ui.fragment.ChatFragment
import com.hyphenate.kotlineaseim.view.ui.fragment.MembersFragment
import com.hyphenate.kotlineaseim.view.ui.fragment.QAFragment


class ChatViewPager2Adapter(fragment: Fragment):FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3


    override fun createFragment(position: Int): Fragment {
        var fragment = ChatFragment()
        if(position == 1){
            return QAFragment()
        }
        if(position == 2){
            return MembersFragment()
        }
        return fragment
    }


}