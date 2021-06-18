package com.hyphenate.kotlineaseim.view.`interface`

interface InputMsgListener {

    /**
     * 输入框点击
     */
    fun onEditTextClick()

    /**
     * 表情点击
     */
    fun onFaceClick(isVisible: Boolean)

    /**
     * 图片点击
     */
    fun onPictureClick()

    /**
     * 发送点击
     */
    fun onSendClick(msgContent: String)

    /**
     * 搜索点击
     */
    fun onSearchClick()

    /**
     * 搜索点击
     */
    fun onFocusChange(hasFocus: Boolean)

}