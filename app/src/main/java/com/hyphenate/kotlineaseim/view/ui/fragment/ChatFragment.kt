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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.livedatas.LiveDataBus
import com.hyphenate.kotlineaseim.utils.CommonUtil
import com.hyphenate.kotlineaseim.view.`interface`.InputMsgListener
import com.hyphenate.kotlineaseim.view.`interface`.MessageListItemClickListener
import com.hyphenate.kotlineaseim.view.`interface`.OnDialogItemClickListener
import com.hyphenate.kotlineaseim.view.adapter.MessageAdapter
import com.hyphenate.kotlineaseim.view.ui.widget.ChatDialogFragment
import com.hyphenate.kotlineaseim.view.ui.widget.ChatViewPager
import com.hyphenate.kotlineaseim.view.ui.widget.InputMsgView
import com.hyphenate.util.EMLog
import com.hyphenate.util.PathUtil
import com.hyphenate.util.UriUtils
import com.hyphenate.util.VersionUtils
import java.io.File

class ChatFragment : Fragment(), EMMessageListener, InputMsgListener {
    //伴生对象
    companion object{
        const val TAG = "ChatFragment"
        const val REQUEST_CODE_CAMERA = 1
        const val REQUEST_CODE_LOCAL = 2
    }

    lateinit var searchBar: EditText
    lateinit var recyclerView: RecyclerView
    lateinit var cameraFile: File
    lateinit var inputMsgView: InputMsgView
    private lateinit var context: Activity
    private val adapter = MessageAdapter()
    lateinit var announcementView: LinearLayout

    private val con: EMConversation = EMClient.getInstance().chatManager().getConversation(
        EaseConstant.CHATROOM_ID, EMConversation.EMConversationType.ChatRoom, true
    )
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context as Activity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inputMsgView = view.findViewById(R.id.input_view)
        inputMsgView.addInputMsgListener(this)
        recyclerView = view.findViewById(R.id.rv_list)
        searchBar = view.findViewById(R.id.search_bar)
        announcementView = view.findViewById(R.id.announcement_view)
        val layoutManager = LinearLayoutManager(context.applicationContext)
        recyclerView.layoutManager = layoutManager
//        val adapter = ChatRecycleViewAdapter(
//            context!!, EMClient.getInstance().chatManager().getConversation(
//                EaseConstant.CHATROOM_ID, EMConversation.EMConversationType.ChatRoom, true
//            )
//        )
        adapter.setMessageListItemClickListener(object : MessageListItemClickListener{
            override fun onResendClick(message: EMMessage): Boolean {
                return true
            }

            override fun onMessageCreate(message: EMMessage) {
                EMLog.e(TAG,"onMessageCreate")
            }

            override fun onMessageSuccess(message: EMMessage) {
                EMLog.e(TAG,"onMessageSuccess")
            }

            override fun onMessageError(message: EMMessage, code: Int, error: String?) {
                EMLog.e(TAG,"onMessageError")
            }

            override fun onMessageInProgress(message: EMMessage, progress: Int) {
                EMLog.e(TAG,"onMessageInProgress")
            }

            override fun onRecallClick(message: EMMessage) {
                EMLog.e(TAG,"onRecallClick")
            }

            override fun onMuteClick(message: EMMessage) {
                EMLog.e(TAG,"onMuteClick")
            }
        })
        recyclerView.adapter = adapter
        adapter.setData(con.allMessages)
//        LiveDataBus.get().with("key").observe(viewLifecycleOwner, {
//            when (it.toString()) {
//                "send" -> {
//                    adapter.setData(con.allMessages)
//                    recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
//                }
//            }
//        })

    }

    override fun onResume() {
        super.onResume()
        EMClient.getInstance().chatManager().addMessageListener(this)
    }

    override fun onPause() {
        super.onPause()
        EMClient.getInstance().chatManager().removeMessageListener(this)
    }

    override fun onMessageReceived(messages: MutableList<EMMessage>?) {
        var adapter = recyclerView.adapter as MessageAdapter
        activity?.runOnUiThread {
            adapter.setData(con.allMessages)
            recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }

    override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageRead(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageRecalled(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageChanged(message: EMMessage?, change: Any?) {

    }

    override fun onEditTextClick() {
        Log.e(ChatViewPager.TAG, "onEditTextClick")
    }

    override fun onFaceClick(isVisible: Boolean) {
        Log.e(ChatViewPager.TAG, "onFaceClick:$isVisible")
    }

    override fun onPictureClick() {
        Log.e(ChatViewPager.TAG, "onPictureClick")
        var dialog = ChatDialogFragment()
        dialog.onDialogItemClickListener = object : OnDialogItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                when(position){
                    0 -> selectPicFromLocal()
                    1 -> selectPicFromCamera()
                }
            }
        }
        var transaction: FragmentTransaction = parentFragmentManager.beginTransaction().setTransition(
            FragmentTransaction.TRANSIT_FRAGMENT_FADE
        )
        dialog.show(transaction, null)
    }

    override fun onSendClick(msgContent: String) {
        Log.e(ChatViewPager.TAG, "onSendClick:$msgContent")
        if(msgContent.isNotEmpty()){
            var message = EMMessage.createTxtSendMessage(msgContent, EaseConstant.CHATROOM_ID);
            sendMessage(message)
        }
    }

    override fun onSearchClick() {
        announcementView.visibility = View.GONE
        searchBar.visibility = View.VISIBLE
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
            CommonUtil.getUriForFile(context, cameraFile)), REQUEST_CODE_CAMERA
        )
    }

    fun selectPicFromLocal() {
        var intent: Intent?
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

    private fun onActivityResultForCamera() {
        if (cameraFile.exists()) {
            EMLog.e(ChatViewPager.TAG, Uri.parse(cameraFile.absolutePath).toString())
            var message = EMMessage.createImageSendMessage(Uri.parse(cameraFile.absolutePath), false, EaseConstant.CHATROOM_ID)
            sendMessage(message)
        }
    }

    private fun onActivityResultForLocalPhotos(data: Intent) {
        data.data?.let {
            var filePath = UriUtils.getFilePath(context, it)
            if (!TextUtils.isEmpty(filePath) && File(filePath).exists()) {
                EMLog.e(ChatViewPager.TAG, Uri.parse(filePath).toString())
                var message = EMMessage.createImageSendMessage(
                    Uri.parse(filePath),
                    false,
                    EaseConstant.CHATROOM_ID
                )
                sendMessage(message)
            }else {
                EMLog.e(ChatViewPager.TAG, it.toString())
                var message =
                    EMMessage.createImageSendMessage(data.data, false, EaseConstant.CHATROOM_ID)
                sendMessage(message)
            }
        }
    }

    private fun sendMessage(message: EMMessage){
        message.chatType = EMMessage.ChatType.ChatRoom
        EMClient.getInstance().chatManager().sendMessage(message)
//        LiveDataBus.get().with("key")
//            .postValue("send")
        adapter.setData(con.allMessages)
        recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
    }
}