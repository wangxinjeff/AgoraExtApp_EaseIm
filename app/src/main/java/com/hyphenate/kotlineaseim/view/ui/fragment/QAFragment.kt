package com.hyphenate.kotlineaseim.view.ui.fragment

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.utils.SoftInputUtil
import com.hyphenate.kotlineaseim.view.`interface`.InputMsgListener
import com.hyphenate.kotlineaseim.view.ui.widget.InputMsgView

class QAFragment : Fragment(), InputMsgListener {

    lateinit var searchBar: EditText
    lateinit var inputMsgView: InputMsgView
    private val softInputUtil = SoftInputUtil()
    var isShowSoft :Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        inputMsgView = view.findViewById(R.id.input_view)
        searchBar = view.findViewById(R.id.search_bar)

        initListener()

    }
    private fun initListener(){
        inputMsgView.addInputMsgListener(this)
        softInputUtil.attachSoftInput(
            inputMsgView
        ) { isSoftInputShow, softInputHeight, viewOffset ->
            isShowSoft = isSoftInputShow
            if (isSoftInputShow && !searchBar.isVisible)
                inputMsgView.translationY = inputMsgView.translationY - viewOffset
            else
                inputMsgView.translationY = 0F
        }

        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event != null && KeyEvent.KEYCODE_ENTER ==
                event.keyCode && KeyEvent.ACTION_DOWN == event.action
            ) {
                val searchContent = searchBar.text
                inputMsgView.hideSoftKeyboard(searchBar)
                true
            } else
                false
        }
    }

    //伴生对象
    companion object{
        fun newInstance(){

        }
    }

    override fun onEditTextClick() {
        if(searchBar.isVisible) {
            searchBar.visibility = View.GONE
        }
    }

    override fun onFaceClick(isVisible: Boolean) {

    }

    override fun onPictureClick() {

    }

    override fun onSendClick(msgContent: String) {

    }

    override fun onSearchClick() {
        if (searchBar.isVisible) {
            if(searchBar.isFocused && isShowSoft)
                inputMsgView.hideSoftKeyboard(searchBar)
            searchBar.text.clear()
            searchBar.visibility = View.GONE
        } else {
            inputMsgView.translationY = 0F
            searchBar.visibility = View.VISIBLE
            if(isShowSoft)
                searchBar.requestFocus()
            else
                inputMsgView.showSoftKeyboard(searchBar)
        }
    }

    override fun onFocusChange(hasFocus: Boolean) {
        if(hasFocus){
            if(searchBar.isVisible) {
                searchBar.visibility = View.GONE
            }
        }
    }
}