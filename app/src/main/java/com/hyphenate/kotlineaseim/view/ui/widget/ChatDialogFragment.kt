package com.hyphenate.kotlineaseim.view.ui.widget

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.view.`interface`.OnDialogItemClickListener
import com.hyphenate.kotlineaseim.view.`interface`.OnItemClickListener
import com.hyphenate.kotlineaseim.view.adapter.ChatDialogRecycleAdapter

class ChatDialogFragment : DialogFragment() {

    lateinit var tvTitle: TextView
    lateinit var rvDialogList: RecyclerView
    lateinit var btnCancle: Button
    var listData: MutableList<String> = mutableListOf("相册", "相机")

    lateinit var onDialogItemClickListener: OnDialogItemClickListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onStart() {
        super.onStart()
        setDialogParams()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chat_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun initView(){
        dialog!!.window!!.setWindowAnimations(R.style.animate_dialog)
        tvTitle = view!!.findViewById(R.id.tv_title)
        tvTitle.text = "选择"
        rvDialogList = view!!.findViewById(R.id.rv_dialog_list)
        btnCancle = view!!.findViewById(R.id.btn_cancel)
        btnCancle.text = "取消"

    }

    private fun initData(){
        rvDialogList.layoutManager = LinearLayoutManager(context)
        var adapter = ChatDialogRecycleAdapter(listData)
        rvDialogList.adapter = adapter
        rvDialogList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        adapter.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                onDialogItemClickListener.onItemClick(view, position)
                dismiss()
            }
        }
        btnCancle.setOnClickListener{
            dismiss()
        }

    }

    private fun setDialogParams() {
        try {
            val dialogWindow = dialog!!.window
            val lp = dialogWindow!!.attributes
            lp.dimAmount = 0.6f
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.BOTTOM
            setDialogParams(lp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setDialogParams(layoutParams: WindowManager.LayoutParams?) {
        try {
            val dialogWindow = dialog!!.window
            dialogWindow!!.attributes = layoutParams
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    //伴生对象
    companion object{

    }

}