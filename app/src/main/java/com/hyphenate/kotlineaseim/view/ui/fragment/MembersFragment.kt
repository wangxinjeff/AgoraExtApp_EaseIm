package com.hyphenate.kotlineaseim.view.ui.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.hyphenate.kotlineaseim.livedatas.LiveDataBus
import com.hyphenate.kotlineaseim.model.User
import com.hyphenate.kotlineaseim.utils.CommonUtil
import com.hyphenate.kotlineaseim.view.adapter.MembersAdapter
import com.hyphenate.kotlineaseim.viewmodel.ChatViewModel

class MembersFragment : BaseFragment() {

    //伴生对象
    companion object{
        const val TAG = "MembersFragment"
    }
    lateinit var searchBar: EditText
    private lateinit var adapter : MembersAdapter
    override fun getLayoutId(): Int {
        return R.layout.fragment_members
    }

    override fun initView(view: View) {
        recyclerView = view.findViewById(R.id.rv_members)
        searchBar = view.findViewById(R.id.search_bar)
        val layoutManager = LinearLayoutManager(context.applicationContext)
        adapter = MembersAdapter(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun initListener() {
        chatViewmodel.membersObservable.observe(viewLifecycleOwner, { members ->
            if (members.isNotEmpty()) {
                LiveDataBus.get().with(EaseConstant.MEMBER_COUNT)
                    .postValue(members.size)
                Log.e("memberCount", members.size.toString())
                adapter.setData(members)
//                recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
            }
        })

        chatViewmodel.singleObservable.observe(viewLifecycleOwner, { user ->
            adapter.addData(user)
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

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        softInputUtil.attachSoftInput(
            searchBar
        ) { isSoftInputShow, softInputHeight, viewOffset ->
            isShowSoft = isSoftInputShow
        }

        LiveDataBus.get().with(EaseConstant.MEMBER_EXIT).observe(viewLifecycleOwner, {
            if(it.toString().isNotEmpty()){
                adapter.removeData(it.toString())
            }
        })
        LiveDataBus.get().with(EaseConstant.MEMBER_JOIN).observe(viewLifecycleOwner, {
            if(it.toString().isNotEmpty()){
                chatViewmodel.loadSingleUser(it.toString())
            }
        })
    }

    override fun initData() {
        chatViewmodel.loadMembers(EaseConstant.CHATROOM_ID)
    }

    override fun isVisibleToUser(isVisibleToUser: Boolean) {
        if(isShowSoft){
            CommonUtil.hideSoftKeyboard(context, searchBar)
        }
    }

}