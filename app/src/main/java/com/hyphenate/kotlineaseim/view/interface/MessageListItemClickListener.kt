package com.hyphenate.kotlineaseim.view.`interface`

import com.hyphenate.chat.EMMessage

interface MessageListItemClickListener {

    /**
     * 重发点击
     */
    fun onResendClick(message: EMMessage): Boolean

    /**
     * 消息创建
     */
    fun onMessageCreate(message: EMMessage)

    /**
     * 消息成功
     */
    fun onMessageSuccess(message: EMMessage)

    /**
     * 消息失败
     */
    fun onMessageError(message: EMMessage, code: Int, error: String?)

    /**
     * 消息进行中
     */
    fun onMessageInProgress(message: EMMessage, progress: Int)

    /**
     * 撤回点击
     */
    fun onRecallClick(message: EMMessage)

    /**
     * 禁言点击
     */
    fun onMuteClick(message: EMMessage)
}