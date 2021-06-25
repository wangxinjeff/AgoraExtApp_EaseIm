package com.hyphenate.kotlineaseim.repositories

import androidx.lifecycle.MutableLiveData
import com.hyphenate.EMCallBack
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.*
import com.hyphenate.exceptions.HyphenateException
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.manager.ThreadManager
import com.hyphenate.kotlineaseim.model.User
import com.hyphenate.util.EMLog

class EaseRepository : BaseRepository() {
    companion object {
        const val TAG = "EaseRepository"
    }

    fun createUser(userName: String, pwd: String, data: MutableLiveData<Map<String, String>>){
        runOnIOThread {
            try {
                EMClient.getInstance().createAccount(userName, pwd)
                val result: MutableMap<String, String> = mutableMapOf()
                result[EaseConstant.ERROR_CODE] = "0"
                data.postValue(result)
            }catch (e: HyphenateException){
                e.printStackTrace()
                val result: MutableMap<String, String> = mutableMapOf()
                result[EaseConstant.ERROR_CODE] = e.errorCode.toString()
                result[EaseConstant.ERROR_MSG] = e.description.toString()
                data.postValue(result)
            }

        }
    }

    fun login(userName: String, pwd: String, data: MutableLiveData<Map<String, String>>) {
        EMClient.getInstance().login(userName, pwd, object : EMCallBack {
            override fun onSuccess() {
                val result: MutableMap<String, String> = mutableMapOf()
                result[EaseConstant.ERROR_CODE] = "0"
                data.postValue(result)
            }

            override fun onError(code: Int, errorMsg: String?) {
                val result: MutableMap<String, String> = mutableMapOf()
                result[EaseConstant.ERROR_CODE] = code.toString()
                result[EaseConstant.ERROR_MSG] = errorMsg!!
                data.postValue(result)
            }

            override fun onProgress(progress: Int, status: String?) {
                TODO("Not yet implemented")
            }

        })
    }

    fun joinRoom(chatRoomId: String, data: MutableLiveData<Map<String, String>>) {
        EMClient.getInstance().chatroomManager()
            .joinChatRoom(chatRoomId, object : EMValueCallBack<EMChatRoom> {
                override fun onSuccess(value: EMChatRoom?) {
                    val result: MutableMap<String, String> = mutableMapOf()
                    result[EaseConstant.ERROR_CODE] = "0"
                    data.postValue(result)
                }

                override fun onError(error: Int, errorMsg: String?) {
                    val result: MutableMap<String, String> = mutableMapOf()
                    result[EaseConstant.ERROR_CODE] = error.toString()
                    result[EaseConstant.ERROR_MSG] = errorMsg!!
                    data.postValue(result)
                }

            })
    }

    fun loadMessages(conversationId: String, data: MutableLiveData<List<EMMessage>>) {
        val conversation = EMClient.getInstance().chatManager()
            .getConversation(conversationId, EMConversation.EMConversationType.ChatRoom, true)
        val msgList = conversation.allMessages
        val norMsgList = mutableListOf<EMMessage>()
        for(message in msgList){
            val msgType = message.getIntAttribute(EaseConstant.MSG_TYPE, EaseConstant.NORMAL_MSG)
            if(msgType == 0)
                norMsgList.add(message)
        }
        data.postValue(norMsgList)
    }

    fun loadQAMessages(conversationId: String, data: MutableLiveData<List<EMMessage>>) {
        val conversation = EMClient.getInstance().chatManager()
            .getConversation(conversationId, EMConversation.EMConversationType.ChatRoom, true)
        val msgList = conversation.allMessages
        val qaMsgList = mutableListOf<EMMessage>()
        for(message in msgList){
            val msgType = message.getIntAttribute(EaseConstant.MSG_TYPE, EaseConstant.NORMAL_MSG)
            if(msgType in 1..2)
                qaMsgList.add(message)
        }
        data.postValue(qaMsgList)
    }

    fun loadMembers(chatRoomId: String, data: MutableLiveData<List<User>>) {
        runOnIOThread {
            var users = mutableListOf<User>()
            val members = mutableListOf<String>()
            val room = EMClient.getInstance().chatroomManager().fetchChatRoomFromServer(chatRoomId)
            members.add(room.owner)
            members.addAll(room.adminList)
            var result = EMCursorResult<String>()
            do {
                result = EMClient.getInstance().chatroomManager().fetchChatRoomMembers(
                    chatRoomId,
                    if (result.cursor.isNotEmpty()) result.cursor else "",
                    20
                )
                members.addAll(result.data)
            } while (result.cursor.isNotEmpty())
            EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(
                members.toTypedArray(),
                object : EMValueCallBack<Map<String, EMUserInfo>> {
                    override fun onSuccess(value: Map<String, EMUserInfo>?) {
                        value?.forEach { item ->
                            EMLog.e(TAG, item.value.nickName)
                            users.add(
                                User(
                                    item.value.userId,
                                    item.value.avatarUrl,
                                    item.value.nickName,
                                    item.value.ext
                                )
                            )
                        }
                        data.postValue(users)
                    }

                    override fun onError(error: Int, errorMsg: String?) {

                    }

                })
        }
    }

    fun fetchAnnouncement(chatRoomId: String, data: MutableLiveData<String>) {
        EMClient.getInstance().chatroomManager()
            .asyncFetchChatRoomAnnouncement(chatRoomId, object : EMValueCallBack<String> {
                override fun onSuccess(value: String?) {
                    value?.let { data.postValue(value) }
                }

                override fun onError(error: Int, errorMsg: String?) {

                }

            })
    }
}