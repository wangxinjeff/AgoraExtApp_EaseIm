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
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.utils.CommonUtil
import com.hyphenate.kotlineaseim.view.`interface`.InputMsgListener
import com.hyphenate.kotlineaseim.view.`interface`.OnDialogItemClickListener
import com.hyphenate.kotlineaseim.view.ui.widget.ChatDialogFragment
import com.hyphenate.kotlineaseim.view.ui.widget.InputMsgView
import com.hyphenate.util.EMLog
import com.hyphenate.util.PathUtil
import com.hyphenate.util.UriUtils
import com.hyphenate.util.VersionUtils
import java.io.File

abstract class BaseFragment: Fragment(), InputMsgListener {

    companion object{
        const val TAG = "BaseFragment"
        const val REQUEST_CODE_CAMERA = 1
        const val REQUEST_CODE_LOCAL = 2
    }

    lateinit var cameraFile: File
    lateinit var context: Activity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as Activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initListener()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData()
    }

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
        val dialog = ChatDialogFragment()
        dialog.onDialogItemClickListener = object : OnDialogItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                when (position) {
                    0 -> selectPicFromLocal()
                    1 -> selectPicFromCamera()
                }
            }
        }
        val transaction: FragmentTransaction =
            parentFragmentManager.beginTransaction().setTransition(
                FragmentTransaction.TRANSIT_FRAGMENT_FADE
            )
        dialog.show(transaction, null)
    }

    /**
     * 点击发送，子类不需要再实现
     */
    override fun onSendClick(msgContent: String) {
        Log.e(TAG, "onSendClick:$msgContent")
        if (msgContent.isNotEmpty()) {
            val message = EMMessage.createTxtSendMessage(msgContent, EaseConstant.CHATROOM_ID);
            addExt(message)
            sendMessage(message)
        }
    }

    override fun onSearchClick() {

    }

    override fun onFocusChange(hasFocus: Boolean) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> onActivityResultForCamera()
                REQUEST_CODE_LOCAL -> data?.let { onActivityResultForLocalPhotos(it) }
            }
        }
    }

    /***
     * 处理相机拍摄
     */
    private fun onActivityResultForCamera() {
        if (cameraFile.exists()) {
            EMLog.e(ChatFragment.TAG, Uri.parse(cameraFile.absolutePath).toString())
            val message = EMMessage.createImageSendMessage(
                Uri.parse(cameraFile.absolutePath),
                false,
                EaseConstant.CHATROOM_ID
            )
            addExt(message)
            sendMessage(message)
        }
    }

    /**
     * 处理相册选择
     */
    private fun onActivityResultForLocalPhotos(data: Intent) {
        data.data?.let {
            val filePath = UriUtils.getFilePath(context, it)
            if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                EMLog.e(ChatFragment.TAG, Uri.parse(filePath).toString())
                val message = EMMessage.createImageSendMessage(
                    Uri.parse(filePath),
                    false,
                    EaseConstant.CHATROOM_ID
                )
                sendMessage(message)
            } else {
                EMLog.e(ChatFragment.TAG, it.toString())
                val message =
                    EMMessage.createImageSendMessage(data.data, false, EaseConstant.CHATROOM_ID)
                addExt(message)
                sendMessage(message)
            }
        }
    }

    /**
     * 选择本地相册
     */
    fun selectPicFromLocal() {
        val intent: Intent?
        if (VersionUtils.isTargetQ(context)) {
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
        } else {
            if (Build.VERSION.SDK_INT < 19) {
                intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
            } else {
                intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            }
        }
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_LOCAL)
    }

    /**
     * 选择相机拍摄
     */
    fun selectPicFromCamera() {
        if (!CommonUtil.isSdcardExist())
            return
        cameraFile = File(
            PathUtil.getInstance().imagePath,
            EMClient.getInstance().currentUser + System.currentTimeMillis() + ".jpg"
        )
        cameraFile.parentFile?.mkdirs()
        startActivityForResult(
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                MediaStore.EXTRA_OUTPUT,
                CommonUtil.getUriForFile(context, cameraFile)
            ), REQUEST_CODE_CAMERA
        )
    }

    /**
     * 设置消息扩展
     */
    abstract fun addExt(message: EMMessage)

    /**
     * 发送消息
     */
    abstract fun sendMessage(message: EMMessage)
}