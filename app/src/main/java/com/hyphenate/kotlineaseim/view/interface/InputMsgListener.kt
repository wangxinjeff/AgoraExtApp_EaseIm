package com.hyphenate.kotlineaseim.view.`interface`

interface InputMsgListener {

    fun onEditTextClick()

    fun onFaceClick(isVisible: Boolean)

    fun onPictureClick()

    fun onSendClick(msgContent: String)

    fun onSearchClick()

}