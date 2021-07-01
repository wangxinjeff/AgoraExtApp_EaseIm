package com.hyphenate.kotlineaseim.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.model.User
import com.hyphenate.kotlineaseim.repositories.EaseRepository


class ChatViewModel(application: Application) : AndroidViewModel(application) {
    companion object{
        const val TAG = "ChatViewModel"
    }
    val chatObservable = MutableLiveData<List<EMMessage>>()
    val chatQAObservable = MutableLiveData<List<EMMessage>>()
    val membersObservable = MutableLiveData<List<User>>()
    val announcementObservable = MutableLiveData<String>()
    val singleObservable = MutableLiveData<User>()

    private val easeRepository = EaseRepository()

    /**
     * 加载普通消息
     */
    fun loadMessages(conversationId: String){
        easeRepository.loadMessages(conversationId, chatObservable)

    }

    /**
     * 加载问答消息
     */
    fun loadQAMessages(conversationId: String){
        easeRepository.loadQAMessages(conversationId, chatQAObservable)
    }

    /**
     * 加载所有成员
     */
    fun loadMembers(chatRoomId: String){
        easeRepository.loadMembers(chatRoomId, membersObservable)
    }

    /**
     * 获取公告
     */
    fun fetchAnnouncement(chatRoomId: String){
        easeRepository.fetchAnnouncement(chatRoomId, announcementObservable)
    }

    /**
     * 加载单个成员
     */
    fun loadSingleUser(userId: String){
        easeRepository.loadSingleUser(userId, singleObservable)
    }
}