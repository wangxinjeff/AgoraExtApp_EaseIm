package com.hyphenate.kotlineaseim.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hyphenate.kotlineaseim.view.ui.fragment.ChatFragment
import com.hyphenate.kotlineaseim.view.ui.fragment.MembersFragment
import com.hyphenate.kotlineaseim.view.ui.fragment.QAFragment


class ChatViewPagerAdapter(private val fragmentManager: FragmentManager, private val mList: List<Fragment>):FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int = mList.size

    override fun getItem(position: Int): Fragment {
        return mList[position]
    }

    override fun getItemId(position: Int): Long {
        return mList[position].hashCode().toLong()
    }

    override fun getItemPosition(`object`: Any): Int {
        //第一种方法是直接返回POSITION_NONE
        //第二种就是先判断是否发生了修改再判断
        val index = mList.indexOf(`object`)
        if (index == -1) {
            return PagerAdapter.POSITION_NONE
        }
        return PagerAdapter.POSITION_UNCHANGED
    }
}