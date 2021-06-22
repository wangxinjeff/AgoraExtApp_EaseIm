package com.hyphenate.kotlineaseim.view.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.livedatas.LiveDataBus
import com.hyphenate.kotlineaseim.utils.CommonUtil
import com.hyphenate.kotlineaseim.utils.SoftInputUtil
import com.hyphenate.kotlineaseim.view.`interface`.InputMsgListener
import com.hyphenate.kotlineaseim.view.`interface`.MessageListItemClickListener
import com.hyphenate.kotlineaseim.view.`interface`.OnDialogItemClickListener
import com.hyphenate.kotlineaseim.view.adapter.MessageAdapter
import com.hyphenate.kotlineaseim.view.ui.widget.ChatDialogFragment
import com.hyphenate.kotlineaseim.view.ui.widget.ChatViewPager
import com.hyphenate.kotlineaseim.view.ui.widget.InputMsgView
import com.hyphenate.kotlineaseim.viewmodel.ChatViewModel
import com.hyphenate.util.EMLog
import com.hyphenate.util.PathUtil
import com.hyphenate.util.UriUtils
import com.hyphenate.util.VersionUtils
import java.io.File

class ChatFragment : BaseFragment() {
    //伴生对象
    companion object {
        const val TAG = "ChatFragment"
    }

    private lateinit var inputMsgView: InputMsgView
    private val adapter = MessageAdapter(EaseConstant.FRAGMENT_CHAT)
    private lateinit var announcementView: LinearLayout

    override fun getLayoutId(): Int {
        return R.layout.fragment_chat
    }

    override fun initView(view: View) {
        inputMsgView = view.findViewById(R.id.input_view)
        recyclerView = view.findViewById(R.id.rv_list)
        searchBar = view.findViewById(R.id.search_bar)
        announcementView = view.findViewById(R.id.announcement_view)
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

        LiveDataBus.get().with("key").observe(viewLifecycleOwner, {
            when (it.toString()) {
                "receiveMsg" -> {
                    chatViewmodel.loadMessages(EaseConstant.CHATROOM_ID)
                }
            }
        })

        softInputUtil.attachSoftInput(
            inputMsgView
        ) { isSoftInputShow, softInputHeight, viewOffset ->
            isShowSoft = isSoftInputShow
            if (isSoftInputShow && !searchBar.isVisible)
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

        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event != null && KeyEvent.KEYCODE_ENTER ==
                event.keyCode && KeyEvent.ACTION_DOWN == event.action
            ) {
                val searchContent = searchBar.text
                CommonUtil.hideSoftKeyboard(context, searchBar)
                true
            } else
                false
        }

        searchBar.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            EMLog.e(TAG,hasFocus.toString())
        }
    }

    override fun initData() {
        chatViewmodel.loadMessages(EaseConstant.CHATROOM_ID)
    }

    override fun onEditTextClick() {
        if(searchBar.isVisible) {
            searchBar.text.clear()
            searchBar.visibility = View.GONE
            announcementView.visibility = View.VISIBLE
        }

    }

    override fun onFaceClick(isVisible: Boolean) {
        Log.e(TAG, "onFaceClick:$isVisible")
    }

    override fun onSearchClick() {
        if (searchBar.isVisible) {
            searchBar.text.clear()
            searchBar.visibility = View.GONE
            announcementView.visibility = View.VISIBLE
        } else {
            inputMsgView.translationY = 0F
            announcementView.visibility = View.GONE
            searchBar.visibility = View.VISIBLE
            inputMsgView.msgContent.clearFocus()
            if(isShowSoft)
                searchBar.requestFocus()
            else
                CommonUtil.showSoftKeyboard(context, searchBar)
        }
    }

    override fun onFocusChange(hasFocus: Boolean) {
        if(hasFocus){
            if(searchBar.isVisible) {
                searchBar.visibility = View.GONE
                announcementView.visibility = View.VISIBLE
            }
        }
    }

    override fun addExt(message: EMMessage) {
    }

    /***
     * 发送消息
     */
    override fun sendMessage(message: EMMessage) {
        message.chatType = EMMessage.ChatType.ChatRoom
        EMClient.getInstance().chatManager().sendMessage(message)
        chatViewmodel.loadMessages(EaseConstant.CHATROOM_ID)
    }

    override fun onPause() {
        super.onPause()
        if (searchBar.isVisible) {
            searchBar.text.clear()
            searchBar.visibility = View.GONE
            announcementView.visibility = View.VISIBLE
        }
    }
}