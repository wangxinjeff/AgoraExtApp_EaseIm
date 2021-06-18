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

    val testObservable = MutableLiveData<Map<String, String>>()
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
        easeRepository.easeLogin(userName, pwd,testObservable)

    }

    fun joinChatRoom(chatRoomId: String){
        easeRepository.easeJoinRoom(chatRoomId,joinObservable)

    }

}