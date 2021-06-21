package com.hyphenate.kotlineaseim.view.viewholder

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMMessage
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.view.`interface`.MessageListItemClickListener
import com.hyphenate.util.EMLog

abstract class ChatRowViewHolder(
    private val view: View,
    private val itemClickListener: MessageListItemClickListener,
    private val fragmentNum: Int
) : RecyclerView.ViewHolder(view) {
    companion object {
        const val TAG = "ChatRowViewHolder"
    }

    val avatar: ImageView? = itemView.findViewById(R.id.iv_avatar)
    val name: TextView? = itemView.findViewById(R.id.tv_name)
    val role: TextView? = itemView.findViewById(R.id.tv_role)
    private val proBar: ProgressBar? = itemView.findViewById(R.id.progress_bar)
    private val reSend: ImageView? = itemView.findViewById(R.id.resend)
    private val recall: TextView? = itemView.findViewById(R.id.tv_recall)
    private val mute: TextView? = itemView.findViewById(R.id.tv_mute)
    lateinit var message: EMMessage
    val mainThreadHandler = Handler(Looper.getMainLooper())
    private val callback = ChatCallback()


    open fun setUpView(message: EMMessage) {
        this.message = message
        when(fragmentNum) {
            0 -> {
                avatar?.visibility = View.VISIBLE
                mute?.visibility = View.VISIBLE
            }
            1 -> {
                avatar?.visibility = View.GONE
                mute?.visibility = View.GONE
            }
        }
        onSetUpView()
        setListener()
        handleMessage()
    }

    open fun setUpView() {
        onSetUpView()
    }

    abstract fun onSetUpView()

    private fun setListener() {
        reSend?.setOnClickListener {
            itemClickListener.onResendClick(message)
        }
        recall?.setOnClickListener {
            itemClickListener.onRecallClick(message)
        }
        mute?.setOnClickListener {
            itemClickListener.onMuteClick(message)
        }

    }

    private fun handleMessage() {
        message.setMessageStatusCallback(callback)
        mainThreadHandler.post {
            when (message.status()) {
                EMMessage.Status.CREATE -> onMessageCreate()
                EMMessage.Status.SUCCESS -> onMessageSuccess()
                EMMessage.Status.INPROGRESS -> onMessageInProgress()
                EMMessage.Status.FAIL -> onMessageError()
                else -> EMLog.e(TAG, "default")
            }
        }
    }

    inner class ChatCallback : EMCallBack {
        override fun onSuccess() {
            mainThreadHandler.post {
                onMessageSuccess()
                itemClickListener.onMessageSuccess(message)
            }
        }

        override fun onError(code: Int, error: String?) {
            mainThreadHandler.post {
                onMessageError()
                itemClickListener.onMessageError(message, code, error)
            }
        }

        override fun onProgress(progress: Int, status: String?) {
            mainThreadHandler.post {
                onMessageInProgress()
                itemClickListener.onMessageInProgress(message, progress)
            }
        }

    }

    private fun onMessageCreate() {
        setStatus(View.VISIBLE, View.GONE)
    }

    open fun onMessageSuccess() {
        setStatus(View.GONE, View.GONE)
    }

    fun onMessageError() {
        setStatus(View.GONE, View.VISIBLE)
    }

    open fun onMessageInProgress() {
        setStatus(View.VISIBLE, View.GONE)
    }

    private fun setStatus(progressVisible: Int, reSendVisible: Int) {
        proBar?.visibility = progressVisible
        reSend?.visibility = reSendVisible
    }
}