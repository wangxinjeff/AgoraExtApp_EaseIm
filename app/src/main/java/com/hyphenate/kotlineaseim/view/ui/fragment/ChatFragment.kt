package com.hyphenate.kotlineaseim.view.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.livedatas.LiveDataBus
import com.hyphenate.kotlineaseim.utils.CommonUtil
import com.hyphenate.kotlineaseim.view.`interface`.MessageListItemClickListener
import com.hyphenate.kotlineaseim.view.`interface`.OnDialogItemClickListener
import com.hyphenate.kotlineaseim.view.adapter.MessageAdapter
import com.hyphenate.kotlineaseim.view.ui.widget.ChatDialogFragment
import com.hyphenate.kotlineaseim.view.ui.widget.InputMsgView
import com.hyphenate.util.EMLog
import com.hyphenate.util.PathUtil
import com.hyphenate.util.UriUtils
import com.hyphenate.util.VersionUtils
import java.io.File

class ChatFragment : BaseFragment() {
    //伴生对象
    companion object {
        const val TAG = "ChatFragment"
        const val REQUEST_CODE_CAMERA = 1
        const val REQUEST_CODE_LOCAL = 2
    }

    private lateinit var inputMsgView: InputMsgView
    private val adapter = MessageAdapter(EaseConstant.FRAGMENT_CHAT)
    private lateinit var announcementView: TextView
    private lateinit var fragmentView : RelativeLayout
    private lateinit var announceDetailsView: ScrollView
    private lateinit var announceContent: TextView
    private lateinit var btnView: TextView
    lateinit var cameraFile: File

    override fun getLayoutId(): Int {
        return R.layout.fragment_chat
    }

    override fun initView(view: View) {
        fragmentView = view.findViewById(R.id.fragment_view)
        inputMsgView = view.findViewById(R.id.input_view)
        recyclerView = view.findViewById(R.id.rv_list)
//        searchBar = view.findViewById(R.id.search_bar)
        announcementView = view.findViewById(R.id.tv_announcement)
        announceDetailsView = view.findViewById(R.id.sv_announcement)
        announceContent = view.findViewById(R.id.announcement_content)
        btnView = view.findViewById(R.id.btn_announcement)
        val layoutManager = LinearLayoutManager(context.applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun initListener() {
        inputMsgView.addInputMsgListener(this)
        adapter.setMessageListItemClickListener(object : MessageListItemClickListener {
            override fun onResendClick(message: EMMessage): Boolean {
                return true
            }

            override fun onMessageCreate(message: EMMessage) {
                EMLog.e(TAG, "onMessageCreate")
            }

            override fun onMessageSuccess(message: EMMessage) {
                EMLog.e(TAG, "onMessageSuccess")
            }

            override fun onMessageError(message: EMMessage, code: Int, error: String?) {
                EMLog.e(TAG, "onMessageError")
            }

            override fun onMessageInProgress(message: EMMessage, progress: Int) {
                EMLog.e(TAG, "onMessageInProgress")
            }

            override fun onRecallClick(message: EMMessage) {
                EMLog.e(TAG, "onRecallClick")
            }

            override fun onMuteClick(message: EMMessage) {
                EMLog.e(TAG, "onMuteClick")
            }
        })

        LiveDataBus.get().with(EaseConstant.CHAT_MESSAGE).observe(viewLifecycleOwner, {
            when (it.toString()) {
                EaseConstant.NORMAL_MESSAGE -> {
                    chatViewmodel.loadMessages(EaseConstant.CHATROOM_ID)
                }
            }
        })

        LiveDataBus.get().with(EaseConstant.ANNOUNCEMENT_CHANGE).observe(viewLifecycleOwner, { announcement ->
            if (announcement.toString().isNotEmpty()) {
                announcementView.text = announcement.toString()
                announceContent.text = announcement.toString()
            }else {
                announcementView.text = getString(R.string.default_announcement)
                announceContent.text = getString(R.string.default_announcement)
            }
        })

        softInputUtil.attachSoftInput(
            inputMsgView
        ) { isSoftInputShow, softInputHeight, viewOffset ->
            isShowSoft = isSoftInputShow
            if (isSoftInputShow)
                inputMsgView.translationY = inputMsgView.translationY - viewOffset
            else
                inputMsgView.translationY = 0F
        }

        chatViewmodel.chatObservable.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                adapter.setData(it)
                recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
        })

        announcementView.setOnClickListener {
            announceDetailsView.visibility = View.VISIBLE
        }

        btnView.setOnClickListener{
            announceDetailsView.visibility = View.INVISIBLE
        }

        chatViewmodel.announcementObservable.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                announcementView.text = it
                announceContent.text = it
            }else {
                announcementView.text = getString(R.string.default_announcement)
                announceContent.text = getString(R.string.default_announcement)
            }
        })

//        searchBar.setOnEditorActionListener { v, actionId, event ->
//            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
//                actionId == EditorInfo.IME_ACTION_DONE ||
//                event != null && KeyEvent.KEYCODE_ENTER ==
//                event.keyCode && KeyEvent.ACTION_DOWN == event.action
//            ) {
//                val searchContent = searchBar.text
//                CommonUtil.hideSoftKeyboard(context, searchBar)
//                true
//            } else
//                false
//        }
//
//        searchBar.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
//            EMLog.e(TAG,hasFocus.toString())
//        }
    }

    override fun initData() {
//        chatViewmodel.loadMessages(EaseConstant.CHATROOM_ID)
        chatViewmodel.fetchAnnouncement(EaseConstant.CHATROOM_ID)
    }

//    override fun onEditTextClick() {
//        if(searchBar.isVisible) {
//            searchBar.text.clear()
//            searchBar.visibility = View.GONE
//            announcementView.visibility = View.VISIBLE
//        }
//    }

    override fun onResume() {
        super.onResume()
        chatViewmodel.loadMessages(EaseConstant.CHATROOM_ID)
    }

    override fun onFaceClick(isVisible: Boolean) {
        Log.e(TAG, "onFaceClick:$isVisible")
    }

//    override fun onSearchClick() {
//        if (searchBar.isVisible) {
//            searchBar.text.clear()
//            searchBar.visibility = View.GONE
//            announcementView.visibility = View.VISIBLE
//        } else {
//            inputMsgView.translationY = 0F
//            announcementView.visibility = View.GONE
//            searchBar.visibility = View.VISIBLE
//            inputMsgView.msgContent.clearFocus()
//            if(isShowSoft)
//                searchBar.requestFocus()
//            else
//                CommonUtil.showSoftKeyboard(context, searchBar)
//        }
//    }

    override fun onFocusChange(hasFocus: Boolean) {
    }


    override fun setExtBeforeSend(message: EMMessage) {
        message.setAttribute(EaseConstant.ROLE, EaseConstant.ROLE_STUDENT)
        message.setAttribute(EaseConstant.MSG_TYPE, EaseConstant.NORMAL_MESSAGE)
    }

    /***
     * 发送消息
     */
    override fun sendMessage(message: EMMessage) {
        message.chatType = EMMessage.ChatType.ChatRoom
        EMClient.getInstance().chatManager().sendMessage(message)
        chatViewmodel.loadMessages(EaseConstant.CHATROOM_ID)
    }

    override fun isVisibleToUser(isVisibleToUser: Boolean) {
        Log.e("isVisibleToUser", isVisibleToUser.toString())
        if(isShowSoft && !isVisibleToUser){
            inputMsgView.translationY = 0F
            CommonUtil.hideSoftKeyboard(context, inputMsgView.msgContent)
        }
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
            setExtBeforeSend(message)
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
                setExtBeforeSend(message)
                sendMessage(message)
            }
        }
    }

    /**
     * 选择本地相册
     */
    private fun selectPicFromLocal() {
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
    private fun selectPicFromCamera() {
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
     * 点击图片选择
     */
    override fun onPictureClick() {
        Log.e(BaseFragment.TAG, "onPictureClick")
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
}