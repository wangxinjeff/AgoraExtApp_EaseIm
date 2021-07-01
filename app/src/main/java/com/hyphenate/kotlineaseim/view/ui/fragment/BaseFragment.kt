package com.hyphenate.kotlineaseim.view.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.utils.CommonUtil
import com.hyphenate.kotlineaseim.utils.SoftInputUtil
import com.hyphenate.kotlineaseim.view.`interface`.InputMsgListener
import com.hyphenate.kotlineaseim.view.`interface`.OnDialogItemClickListener
import com.hyphenate.kotlineaseim.view.adapter.MessageAdapter
import com.hyphenate.kotlineaseim.view.ui.widget.ChatDialogFragment
import com.hyphenate.kotlineaseim.view.ui.widget.InputMsgView
import com.hyphenate.kotlineaseim.viewmodel.ChatViewModel
import com.hyphenate.util.EMLog
import com.hyphenate.util.PathUtil
import com.hyphenate.util.UriUtils
import com.hyphenate.util.VersionUtils
import java.io.File

abstract class BaseFragment: Fragment(), InputMsgListener {

    companion object{
        const val TAG = "BaseFragment"
    }

    lateinit var context: Activity
//    lateinit var searchBar: EditText
    lateinit var recyclerView: RecyclerView
    lateinit var chatViewmodel: ChatViewModel
    val softInputUtil = SoftInputUtil()

    var isShowSoft :Boolean = false
    var isCreated = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCreated = true
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(!isCreated)
            return
        isVisibleToUser(isVisibleToUser)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatViewmodel = ViewModelProvider(this).get(ChatViewModel::class.java)
        initView(view)
        initListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

    abstract fun getLayoutId(): Int

    abstract fun initView(view: View)
    abstract fun initListener()
    abstract fun initData()

    override fun onEditTextClick() {

    }

    override fun onFaceClick(isVisible: Boolean) {

    }

    /**
     * 点击图片选择，子类不需要再实现
     */
    override fun onPictureClick() {
        Log.e(TAG, "onPictureClick")
    }

    /**
     * 点击发送，子类不需要再实现
     */
    override fun onSendClick(msgContent: String) {
        Log.e(TAG, "onSendClick:$msgContent")
        if (msgContent.isNotEmpty()) {
            val message = EMMessage.createTxtSendMessage(msgContent, EaseConstant.CHATROOM_ID);
            setExtBeforeSend(message)
            sendMessage(message)
        }
    }

//    override fun onSearchClick() {
//
//    }

    override fun onFocusChange(hasFocus: Boolean) {

    }

    /**
     * 设置消息扩展
     */
    open fun setExtBeforeSend(message: EMMessage){}

    /**
     * 发送消息
     */
    open fun sendMessage(message: EMMessage){}

    abstract fun isVisibleToUser(isVisibleToUser: Boolean)

}