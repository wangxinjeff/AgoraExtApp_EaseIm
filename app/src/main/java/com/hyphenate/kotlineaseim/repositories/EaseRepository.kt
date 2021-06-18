package com.hyphenate.kotlineaseim.repositories

import androidx.lifecycle.MutableLiveData
import com.hyphenate.EMCallBack
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMChatRoom
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage

class EaseRepository {

    fun easeLogin(userName: String, pwd: String, data: MutableLiveData<Map<String, String>>){
        EMClient.getInstance().login(userName, pwd, object : EMCallBack {
            override fun onSuccess() {
                val result: MutableMap<String, String> = mutableMapOf()
                result["errorCode"] = "0"
                data.postValue(result)
            }

            override fun onError(code: Int, errorMsg: String?) {
                val result: MutableMap<String, String> = mutableMapOf()
                result["errorCode"] = code.toString()
                result["errorMsg"] = errorMsg!!
                data.postValue(result)
            }

            override fun onProgress(progress: Int, status: String?) {
                TODO("Not yet implemented")
            }

        })
    }

    fun easeJoinRoom(chatRoomId: String, data: MutableLiveData<Map<String, String>>){
        EMClient.getInstance().chatroomManager().joinChatRoom(chatRoomId, object : EMValueCallBack<EMChatRoom> {
            override fun onSuccess(value: EMChatRoom?) {
                val result: MutableMap<String, String> = mutableMapOf()
                result["errorCode"] = "0"
                data.postValue(result)
            }

            override fun onError(error: Int, errorMsg: String?) {
                val result: MutableMap<String, String> = mutableMapOf()
                result["errorCode"] = error.toString()
                result["errorMsg"] = errorMsg!!
                data.postValue(result)
            }

        })
    }

    fun easeLoadMessages(conversationId: String, data: MutableLiveData<List<EMMessage>>){
        val conversation = EMClient.getInstance().chatManager().getConversation(conversationId, EMConversation.EMConversationType.ChatRoom, true)
        val msgList = conversation.allMessages
        data.postValue(msgList)
    }
}