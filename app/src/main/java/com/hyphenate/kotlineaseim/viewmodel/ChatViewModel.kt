package com.hyphenate.kotlineaseim.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.repositories.EaseRepository


class ChatViewModel(application: Application) : AndroidViewModel(application) {
    companion object{
        const val TAG = "ChatViewModel"
    }
    val chatObservable = MutableLiveData<List<EMMessage>>()
    val chatQAObservable = MutableLiveData<List<EMMessage>>()

    private val easeRepository = EaseRepository()

    init {
        Log.e(TAG, "ViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "ViewModel instance about to be destroyed")
    }

    fun loadMessages(conversationId: String){
        easeRepository.easeLoadMessages(conversationId, chatObservable)

    }

    fun loadQAMessages(conversationId: String){
        easeRepository.easeLoadMessages(conversationId, chatQAObservable)

    }


}