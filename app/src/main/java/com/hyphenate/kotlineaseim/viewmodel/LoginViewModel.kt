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

    init {
        Log.e(TAG, "ViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "ViewModel instance about to be destroyed")
    }

    fun login(userName: String, pwd: String){
        easeRepository.login(userName, pwd,loginObservable)

    }

    fun joinChatRoom(chatRoomId: String){
        easeRepository.joinRoom(chatRoomId,joinObservable)

    }

    fun createUser(userName: String, pwd: String){
        easeRepository.createUser(userName, pwd, registerObservable)
    }

}