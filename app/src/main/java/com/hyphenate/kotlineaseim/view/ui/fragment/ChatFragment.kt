package com.hyphenate.kotlineaseim.view.ui.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.livedatas.LiveDataBus
import com.hyphenate.kotlineaseim.utils.CommonUtil
import com.hyphenate.kotlineaseim.view.`interface`.MessageListItemClickListener
import com.hyphenate.kotlineaseim.view.adapter.MessageAdapter
import com.hyphenate.kotlineaseim.view.ui.widget.InputMsgView
import com.hyphenate.util.EMLog

class ChatFragment : BaseFragment() {
    //伴生对象
    companion object {
        const val TAG = "ChatFragment"
    }

    private lateinit var inputMsgView: InputMsgView
    private val adapter = MessageAdapter(EaseConstant.FRAGMENT_CHAT)
    private lateinit var announcementView: TextView
    private lateinit var fragmentView : RelativeLayout
    private lateinit var announceDetailsView: ScrollView
    private lateinit var announceContent: TextView
    private lateinit var btnView: TextView

    override fun getLayoutId(): Int {
        return R.layout.fragment_chat
    }

    override fun initView(view: View) {
        fragmentView = view.findViewById(R.id.fragment_view)
        inputMsgView = view.findViewById(R.id.input_view)
        recyclerView = view.findViewById(R.id.rv_list)
//        searchBar = view.findViewById(R.id.search_bar)
        announcementView = view.findViewById(R.id.tv_announcement)
        announceDetailsView = view.findViewById(R.id.sv_announcement)
        announceContent = view.findViewById(R.id.announcement_content)
        btnView = view.findViewById(R.id.btn_announcement)
        val layoutManager = LinearLayoutManager(context.applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun initListener() {
        inputMsgView.addInputMsgListener(this)
        adapter.setMessageListItemClickListener(object : MessageListItemClickListener {
            override fun onResendClick(message: EMMessage): Boolean {
                return true
            }

            override fun onMessageCreate(message: EMMessage) {
                EMLog.e(TAG, "onMessageCreate")
            }

            override fun onMessageSuccess(message: EMMessage) {
                EMLog.e(TAG, "onMessageSuccess")
            }

            override fun onMessageError(message: EMMessage, code: Int, error: String?) {
                EMLog.e(TAG, "onMessageError")
            }

            override fun onMessageInProgress(message: EMMessage, progress: Int) {
                EMLog.e(TAG, "onMessageInProgress")
            }

            override fun onRecallClick(message: EMMessage) {
                EMLog.e(TAG, "onRecallClick")
            }

            override fun onMuteClick(message: EMMessage) {
                EMLog.e(TAG, "onMuteClick")
            }
        })

        LiveDataBus.get().with(EaseConstant.CHAT_MESSAGE).observe(viewLifecycleOwner, {
            when (it.toString()) {
                EaseConstant.NORMAL_MESSAGE -> {
                    chatViewmodel.loadMessages(EaseConstant.CHATROOM_ID)
                }
            }
        })

        softInputUtil.attachSoftInput(
            inputMsgView
        ) { isSoftInputShow, softInputHeight, viewOffset ->
            isShowSoft = isSoftInputShow
            if (isSoftInputShow)
                inputMsgView.translationY = inputMsgView.translationY - viewOffset
            else
                inputMsgView.translationY = 0F
        }

        chatViewmodel.chatObservable.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                adapter.setData(it)
                recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
        })

        announcementView.setOnClickListener {
            announceDetailsView.visibility = View.VISIBLE
        }

        btnView.setOnClickListener{
            announceDetailsView.visibility = View.INVISIBLE
        }

        chatViewmodel.announcementObservable.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                announcementView.text = it
                announceContent.text = it
            }else {
                announcementView.text = getString(R.string.default_announcement)
                announceContent.text = getString(R.string.default_announcement)
            }
        })

//        searchBar.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
//                actionId == EditorInfo.IME_ACTION_DONE ||
//                event != null && KeyEvent.KEYCODE_ENTER ==
//                event.keyCode && KeyEvent.ACTION_DOWN == event.action
//            ) {
//                val searchContent = searchBar.text
//                CommonUtil.hideSoftKeyboard(context, searchBar)
//                true
//            } else
//                false
//        }
//
//        searchBar.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
//            EMLog.e(TAG,hasFocus.toString())
//        }
    }

    override fun initData() {
        chatViewmodel.loadMessages(EaseConstant.CHATROOM_ID)
        chatViewmodel.fetchAnnouncement(EaseConstant.CHATROOM_ID)
    }

//    override fun onEditTextClick() {
//        if(searchBar.isVisible) {
//            searchBar.text.clear()
//            searchBar.visibility = View.GONE
//            announcementView.visibility = View.VISIBLE
//        }
//    }

    override fun onFaceClick(isVisible: Boolean) {
        Log.e(TAG, "onFaceClick:$isVisible")
    }

//    override fun onSearchClick() {
//        if (searchBar.isVisible) {
//            searchBar.text.clear()
//            searchBar.visibility = View.GONE
//            announcementView.visibility = View.VISIBLE
//        } else {
//            inputMsgView.translationY = 0F
//            announcementView.visibility = View.GONE
//            searchBar.visibility = View.VISIBLE
//            inputMsgView.msgContent.clearFocus()
//            if(isShowSoft)
//                searchBar.requestFocus()
//            else
//                CommonUtil.showSoftKeyboard(context, searchBar)
//        }
//    }

    override fun onFocusChange(hasFocus: Boolean) {
    }


    override fun setExtBeforeSend(message: EMMessage) {
        message.setAttribute(EaseConstant.ROLE, EaseConstant.ROLE_STUDENT)
        message.setAttribute(EaseConstant.MSG_TYPE, EaseConstant.NORMAL_MESSAGE)
    }

    /***
     * 发送消息
     */
    override fun sendMessage(message: EMMessage) {
        message.chatType = EMMessage.ChatType.ChatRoom
        EMClient.getInstance().chatManager().sendMessage(message)
        chatViewmodel.loadMessages(EaseConstant.CHATROOM_ID)
    }

    override fun isVisibleToUser(isVisibleToUser: Boolean) {
        if(isShowSoft){
            inputMsgView.translationY = 0F
            CommonUtil.hideSoftKeyboard(context, inputMsgView.msgContent)
        }
    }
}