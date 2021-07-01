package com.hyphenate.kotlineaseim.view.ui.fragment

import android.util.Log
import android.view.View
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

class QAFragment : BaseFragment() {

    //伴生对象
    companion object {
        const val TAG = "QAFragment"
    }

    lateinit var inputMsgView: InputMsgView
    private val adapter = MessageAdapter(EaseConstant.FRAGMENT_QA)

    override fun getLayoutId(): Int {
        return R.layout.fragment_qa
    }

    override fun initView(view: View) {
        inputMsgView = view.findViewById(R.id.input_view)
        inputMsgView.hideFaceAndPic()
        recyclerView = view.findViewById(R.id.rv_list)
//        searchBar = view.findViewById(R.id.search_bar)
        val layoutManager = LinearLayoutManager(context.applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun initListener() {
        inputMsgView.addInputMsgListener(this)
        softInputUtil.attachSoftInput(
            inputMsgView
        ) { isSoftInputShow, softInputHeight, viewOffset ->
            isShowSoft = isSoftInputShow
            if (isSoftInputShow)
                inputMsgView.translationY = inputMsgView.translationY - viewOffset
            else
                inputMsgView.translationY = 0F
        }

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
                EaseConstant.QA_MESSAGE -> {
                    chatViewmodel.loadQAMessages(EaseConstant.CHATROOM_ID)
                }
            }
        })

        chatViewmodel.chatQAObservable.observe(viewLifecycleOwner, { messages ->
            if (messages.isNotEmpty()) {
                adapter.setData(messages)
                recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
        })
    }

    override fun initData() {
//        chatViewmodel.loadQAMessages(EaseConstant.CHATROOM_ID)
    }

//    override fun onEditTextClick() {
//        if (searchBar.isVisible) {
//            searchBar.text.clear()
//            searchBar.visibility = View.GONE
//        }
//    }

    override fun onResume() {
        super.onResume()
        chatViewmodel.loadQAMessages(EaseConstant.CHATROOM_ID)
    }

    override fun onFaceClick(isVisible: Boolean) {
        Log.e(TAG, "onFaceClick:$isVisible")
    }

//    override fun onSearchClick() {
//        if (searchBar.isVisible) {
//            searchBar.text.clear()
//            searchBar.visibility = View.GONE
//        } else {
//            inputMsgView.translationY = 0F
//            searchBar.visibility = View.VISIBLE
//            inputMsgView.msgContent.clearFocus()
//            if (isShowSoft)
//                searchBar.requestFocus()
//            else
//                CommonUtil.showSoftKeyboard(context, searchBar)
//        }
//    }

    override fun onFocusChange(hasFocus: Boolean) {
    }

    override fun setExtBeforeSend(message: EMMessage) {
        message.setAttribute(EaseConstant.ROLE, EaseConstant.ROLE_STUDENT)
        message.setAttribute(EaseConstant.MSG_TYPE, EaseConstant.QUES_MSG)
    }

    override fun sendMessage(message: EMMessage) {
        message.chatType = EMMessage.ChatType.ChatRoom
        EMClient.getInstance().chatManager().sendMessage(message)
        chatViewmodel.loadQAMessages(EaseConstant.CHATROOM_ID)
    }

    override fun onPause() {
        super.onPause()
        if(isShowSoft){
            inputMsgView.translationY = 0F
            CommonUtil.hideSoftKeyboard(context, inputMsgView.msgContent)
        }
    }

    override fun isVisibleToUser(isVisibleToUser: Boolean) {
        if(isShowSoft && !isVisibleToUser){
            inputMsgView.translationY = 0F
            CommonUtil.hideSoftKeyboard(context, inputMsgView.msgContent)
        }
    }
}