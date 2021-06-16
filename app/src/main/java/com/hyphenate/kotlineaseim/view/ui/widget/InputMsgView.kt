package com.hyphenate.kotlineaseim.view.ui.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.view.`interface`.InputMsgListener

class InputMsgView(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
    LinearLayout(context, attributeSet, defStyleAttr), View.OnClickListener {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    lateinit var msgContent: EditText
    lateinit var faceIcon: ImageView
    lateinit var keyboardIcon: ImageView
    lateinit var faceView: FrameLayout
    lateinit var pictureIcon: ImageView
    lateinit var sendBtn: TextView
    lateinit var listener: InputMsgListener
    lateinit var searchIcon: ImageView

    init {
        LayoutInflater.from(getContext()).inflate(R.layout.input_message_view, this)
        initViews()
    }

    private fun initViews() {
        msgContent = findViewById(R.id.et_msg_content)
        faceView = findViewById(R.id.face_view)
        faceIcon = findViewById(R.id.iv_face)
        keyboardIcon = findViewById(R.id.iv_keyboard)
        pictureIcon = findViewById(R.id.iv_picture)
        sendBtn = findViewById(R.id.btn_send)
        searchIcon = findViewById(R.id.iv_search)


        initListener()
    }

    private fun initListener() {
        msgContent.setOnClickListener(this)
        faceView.setOnClickListener(this)
        pictureIcon.setOnClickListener(this)
        sendBtn.setOnClickListener(this)
        searchIcon.setOnClickListener(this)
        msgContent.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (s != null && s.isNotEmpty()) {
                        sendBtn.background =
                            ContextCompat.getDrawable(context, R.drawable.input_send_btn_enable)
                        return
                    }
                    sendBtn.background =
                        ContextCompat.getDrawable(context, R.drawable.input_send_btn_unenable)
                }
            }
        )
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.et_msg_content -> listener.onEditTextClick()
                R.id.face_view -> {
                    faceIcon.visibility = if (faceIcon.visibility == VISIBLE) GONE else VISIBLE
                    keyboardIcon.visibility = if (faceIcon.visibility == VISIBLE) GONE else VISIBLE
                    listener.onFaceClick(faceIcon.visibility == VISIBLE)
                }
                R.id.iv_picture -> listener.onPictureClick()
                R.id.btn_send -> {
                    listener.onSendClick(msgContent.text.toString())
                    msgContent.text.clear()
                }
                R.id.iv_search -> listener.onSearchClick()
            }
        }
    }

    fun addInputMsgListener(listener: InputMsgListener) {
        this.listener = listener
    }


}