package com.hyphenate.kotlineaseim.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hyphenate.kotlineaseim.repositories.EaseRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        const val TAG = "TestViewModel"
    }
    val registerObservable = MutableLiveData<Map<String, String>>()
    val loginObservable = MutableLiveData<Map<String, String>>()
    val joinObservable = MutableLiveData<Map<String, String>>()

    private val easeRepository = EaseRepository()

    /**
     * 登录环信
     */
    fun login(userName: String, pwd: String){
        easeRepository.login(userName, pwd,loginObservable)

    }

    /**
     * 加入聊天室
     */
    fun joinChatRoom(chatRoomId: String){
        easeRepository.joinRoom(chatRoomId,joinObservable)

    }

    /**
     * 创建账号
     */
    fun createUser(userName: String, pwd: String){
        easeRepository.createUser(userName, pwd, registerObservable)
    }

}