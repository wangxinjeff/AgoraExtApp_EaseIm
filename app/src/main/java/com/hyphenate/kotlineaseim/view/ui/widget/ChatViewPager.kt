package com.hyphenate.kotlineaseim.view.ui.widget

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.DataGenerator
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.livedatas.LiveDataBus
import com.hyphenate.kotlineaseim.view.adapter.ChatViewPagerAdapter
import com.hyphenate.kotlineaseim.view.ui.fragment.ChatFragment
import com.hyphenate.kotlineaseim.view.ui.fragment.MembersFragment
import com.hyphenate.kotlineaseim.view.ui.fragment.QAFragment


class ChatViewPager : Fragment(), EMMessageListener {
    companion object {
        const val TAG = "ChatViewPager"
    }

    private lateinit var tabLayout: TabLayout
    private var chooseTab = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chat_view2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager: ViewPager = view.findViewById(R.id.viewPager)
        val fragmentList = listOf<Fragment>(ChatFragment(), QAFragment(), MembersFragment())
        val titleList = listOf<String>("聊天", "问答", "成员(99+)")
        val viewPagerAdapter =
            activity?.let { ChatViewPagerAdapter(it.supportFragmentManager, fragmentList) }
        viewPager.adapter = viewPagerAdapter
        viewPager.offscreenPageLimit = 2
        tabLayout = view.findViewById(R.id.tab_layout)
        for (index in fragmentList.indices)
            tabLayout.addTab(
                tabLayout.newTab().setCustomView(context?.let {
                    DataGenerator.getTabView(
                        it.applicationContext,
                        titleList[index]
                    )
                })
            )

        recoverItem()
        chooseFirst()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                recoverItem()
                chooseTab(tab)
                tab?.position?.let {
                    chooseTab = it
                    viewPager.setCurrentItem(it, true)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
    }

    private fun chooseFirst() {
        val view = tabLayout.getTabAt(0)?.customView
        view?.findViewById<TextView>(R.id.title)?.setTextColor(Color.BLUE)
        view?.findViewById<TextView>(R.id.title)?.typeface =
            Typeface.defaultFromStyle(Typeface.BOLD)
    }

    /**
     * 重置状态
     */
    private fun recoverItem() {
        for (i in 0..2) {
            val title = tabLayout.getTabAt(i)?.view?.findViewById<TextView>(R.id.title)
            title?.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            title?.setTextColor(Color.BLACK)
        }
    }

    /**
     * 选中状态
     */
    private fun chooseTab(tab: TabLayout.Tab?) {
        val title = tab?.view?.findViewById<TextView>(R.id.title)
        val unread = tab?.view?.findViewById<ImageView>(R.id.iv_tips)
        title?.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        title?.setTextColor(Color.BLUE)
        unread?.visibility = View.INVISIBLE
    }

    private fun showChatUnread() {
        activity?.runOnUiThread {
            val unread = tabLayout.getTabAt(0)?.view?.findViewById<ImageView>(R.id.iv_tips)
            unread?.visibility = View.VISIBLE
        }

    }

    private fun showQAUnread() {
        activity?.runOnUiThread {
            val unread = tabLayout.getTabAt(1)?.view?.findViewById<ImageView>(R.id.iv_tips)
            unread?.visibility = View.VISIBLE
        }
    }

    override fun onMessageReceived(messages: MutableList<EMMessage>?) {
        messages?.let {
            for (message in messages) {
                if (message.type == EMMessage.Type.TXT || message.type == EMMessage.Type.IMAGE) {
                    val msgType =
                        message.getIntAttribute(EaseConstant.MSG_TYPE, EaseConstant.NORMAL_MSG)
                    if (msgType == EaseConstant.ANSWER_MSG) {
                        if(chooseTab != 1)
                            showQAUnread()
                        LiveDataBus.get().with(EaseConstant.CHAT_MESSAGE)
                            .postValue(EaseConstant.QA_MESSAGE)
                    } else {
                        if(chooseTab != 0)
                            showChatUnread()
                        LiveDataBus.get().with(EaseConstant.CHAT_MESSAGE)
                            .postValue(EaseConstant.NORMAL_MESSAGE)
                    }
                }
            }

        }


    }

    override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageRead(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageRecalled(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageChanged(message: EMMessage?, change: Any?) {
    }

    override fun onResume() {
        super.onResume()
        EMClient.getInstance().chatManager().addMessageListener(this)
    }

    override fun onStop() {
        super.onStop()
        EMClient.getInstance().chatManager().removeMessageListener(this)
    }
}