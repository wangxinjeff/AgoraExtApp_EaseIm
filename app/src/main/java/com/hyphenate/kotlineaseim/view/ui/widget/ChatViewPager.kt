package com.hyphenate.kotlineaseim.view.ui.widget

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.DataGenerator
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.livedatas.LiveDataBus
import com.hyphenate.kotlineaseim.utils.CommonUtil
import com.hyphenate.kotlineaseim.view.`interface`.InputMsgListener
import com.hyphenate.kotlineaseim.view.`interface`.OnDialogItemClickListener
import com.hyphenate.kotlineaseim.view.adapter.ChatViewPagerAdapter
import com.hyphenate.util.EMLog
import com.hyphenate.util.PathUtil
import com.hyphenate.util.UriUtils
import com.hyphenate.util.VersionUtils
import java.io.File



class ChatViewPager : Fragment(){
    companion object{
        const val TAG = "ChatViewPager"
    }

    private lateinit var tabLayout : TabLayout

    private var tabList : MutableList<TabLayout.Tab> = mutableListOf()
    private lateinit var context: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager2: ViewPager2 = view.findViewById(R.id.viewPager)
        val viewPagerAdapter = ChatViewPagerAdapter(this)
        viewPager2.adapter = viewPagerAdapter
        tabLayout = view.findViewById(R.id.tab_layout)
        val tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            if(position == 0) {
                tab.customView = DataGenerator.getTabView(context.applicationContext, 0)
                tab.customView?.findViewById<RelativeLayout>(R.id.rl_tips)?.visibility = View.VISIBLE
                tab.customView?.findViewById<TextView>(R.id.title)?.setTextColor(Color.RED)
                tabList.add(0, tab)
            }else{
                tab.customView = DataGenerator.getTabView(context.applicationContext, 1)
                tab.customView?.findViewById<RelativeLayout>(R.id.rl_tips)?.visibility = View.GONE
                tab.customView?.findViewById<TextView>(R.id.title)?.setTextColor(Color.parseColor("#333333"))
                tabList.add(1, tab)
            }
        }
        recoverItem()
        tabLayoutMediator.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                recoverItem()
                chooseTab(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    /**
     * 重置状态
     */
    private fun recoverItem() {
        for (i in 0..1){
            var text1 = tabLayout?.getTabAt(i)?.view?.findViewById<TextView>(R.id.title)
            var rlTips = tabLayout?.getTabAt(i)?.view?.findViewById<RelativeLayout>(R.id.rl_tips)
            var textTips = tabLayout?.getTabAt(i)?.view?.findViewById<TextView>(R.id.text_tips)
            text1?.setTextColor(Color.BLACK)
        }
    }

    /**
     * 选中状态
     */
    private fun chooseTab(tab: TabLayout.Tab?) {
        var text1 = tab?.view?.findViewById<TextView>(R.id.title)
        var rlTips = tab?.view?.findViewById<RelativeLayout>(R.id.rl_tips)
        var textTips = tab?.view?.findViewById<TextView>(R.id.text_tips)
        rlTips?.visibility = View.VISIBLE
        text1?.setTextColor(Color.RED)
    }
}