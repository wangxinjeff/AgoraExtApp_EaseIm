package com.hyphenate.kotlineaseim.view.`interface`

import com.hyphenate.chat.EMMessage

interface MessageListItemClickListener {

    fun onResendClick(message: EMMessage): Boolean
    fun onMessageCreate(message: EMMessage)
    fun onMessageSuccess(message: EMMessage)
    fun onMessageError(message: EMMessage, code: Int, error: String?)
    fun onMessageInProgress(message: EMMessage, progress: Int)
    fun onRecallClick(message: EMMessage)
    fun onMuteClick(message: EMMessage)
}