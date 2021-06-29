package com.hyphenate.kotlineaseim.view.ui.widget

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.utils.CommonUtil
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
    private var listener: InputMsgListener? = null

    //    lateinit var searchIcon: ImageView
    private val activity: Activity = context as Activity

    init {
        LayoutInflater.from(context).inflate(R.layout.input_message_view, this)
        initViews()
    }

    private fun initViews() {
        msgContent = findViewById(R.id.et_msg_content)
        faceView = findViewById(R.id.face_view)
        faceIcon = findViewById(R.id.iv_face)
        keyboardIcon = findViewById(R.id.iv_keyboard)
        pictureIcon = findViewById(R.id.iv_picture)
        sendBtn = findViewById(R.id.btn_send)
//        searchIcon = findViewById(R.id.iv_search)

        sendBtn.isEnabled = false

        initListener()
    }

    private fun initListener() {
        msgContent.setOnClickListener(this)
        faceView.setOnClickListener(this)
        pictureIcon.setOnClickListener(this)
        sendBtn.setOnClickListener(this)
//        searchIcon.setOnClickListener(this)
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
                        sendBtn.isEnabled = true
                        sendBtn.background =
                            ContextCompat.getDrawable(context, R.drawable.input_send_btn_enable)
                        return
                    }
                    sendBtn.background =
                        ContextCompat.getDrawable(context, R.drawable.input_send_btn_unenable)
                }
            }
        )
        msgContent.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event != null && KeyEvent.KEYCODE_ENTER ==
                event.keyCode && KeyEvent.ACTION_DOWN == event.action
            ) {
                listener?.onSendClick(msgContent.text.toString())
                msgContent.text.clear()
                CommonUtil.hideSoftKeyboard(activity, msgContent)
                true
            } else
                false
        })

        msgContent.setOnFocusChangeListener { v, hasFocus ->
            listener?.onFocusChange(hasFocus)
        }
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.et_msg_content -> listener?.onEditTextClick()
                R.id.face_view -> {
                    faceIcon.visibility = if (faceIcon.visibility == VISIBLE) GONE else VISIBLE
                    keyboardIcon.visibility = if (faceIcon.visibility == VISIBLE) GONE else VISIBLE
                    listener?.onFaceClick(faceIcon.visibility == VISIBLE)
                }
                R.id.iv_picture -> listener?.onPictureClick()
                R.id.btn_send -> {
                    listener?.onSendClick(msgContent.text.toString())
                    msgContent.text.clear()
                    CommonUtil.hideSoftKeyboard(activity, msgContent)
                }
//                R.id.iv_search -> listener?.onSearchClick()
            }
        }
    }

    fun addInputMsgListener(listener: InputMsgListener) {
        this.listener = listener
    }

    fun hideFaceAndPic() {
        faceView.visibility = View.GONE
        pictureIcon.visibility = View.GONE
    }

    fun showFaceAndPic() {
        faceView.visibility = View.VISIBLE
        pictureIcon.visibility = View.VISIBLE
    }

    /**
     * 显示禁言UI
     */
    fun mutedRefreshUI() {
        hideFaceAndPic()
        msgContent.setText(context.getString(R.string.muted))
        msgContent.isEnabled = false
        sendBtn.isClickable = false
        sendBtn.background = ContextCompat.getDrawable(context, R.drawable.input_send_btn_unenable)
    }

    /**
     * 取消禁言UI
     */
    fun cancleMutedRefreshUI() {
        showFaceAndPic()
        msgContent.setText(context.getString(R.string.muted))
        msgContent.isEnabled = true
        sendBtn.isClickable = true
        sendBtn.background = ContextCompat.getDrawable(context, R.drawable.input_send_btn_unenable)
    }

}