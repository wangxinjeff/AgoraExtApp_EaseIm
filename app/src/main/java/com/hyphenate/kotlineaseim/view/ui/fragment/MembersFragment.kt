package com.hyphenate.kotlineaseim.view.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.constant.EaseConstant
import com.hyphenate.kotlineaseim.utils.CommonUtil
import com.hyphenate.kotlineaseim.view.adapter.MembersAdapter
import com.hyphenate.kotlineaseim.viewmodel.ChatViewModel

class MembersFragment : BaseFragment() {

    //伴生对象
    companion object{
        const val TAG = "MembersFragment"
    }

    private val adapter = MembersAdapter()
    override fun getLayoutId(): Int {
        return R.layout.fragment_members
    }

    override fun initView(view: View) {
        recyclerView = view.findViewById(R.id.rv_members)
        searchBar = view.findViewById(R.id.search_bar)
        val layoutManager = LinearLayoutManager(context.applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun initListener() {
        chatViewmodel.membersObservable.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()) {
                adapter.setData(it)
                recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
        })
        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event != null && KeyEvent.KEYCODE_ENTER ==
                event.keyCode && KeyEvent.ACTION_DOWN == event.action
            ) {
                val searchContent = searchBar.text
                CommonUtil.hideSoftKeyboard(context, searchBar)
                true
            } else
                false
        }
    }

    override fun initData() {
        chatViewmodel.loadMembers(EaseConstant.CHATROOM_ID)
    }

}