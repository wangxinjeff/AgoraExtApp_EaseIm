package com.hyphenate.kotlineaseim.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.model.User


class MembersAdapter(val context: Context) : RecyclerView.Adapter<MembersAdapter.ViewHolder>(),
    Filterable {
    private var sourceData = mutableListOf<User>()
    private var filterData = mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.member_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text =
            if (getItem(position).name.isNotEmpty()) getItem(position).name else getItem(position).id
            if (getItem(position).role.isNotEmpty()) {
                when (getItem(position).role) {
                    "3" -> holder.role.text = context.getString(R.string.assistant)
                    "2" -> holder.role.text = context.getString(R.string.student)
                    "1" -> holder.role.text = context.getString(R.string.teacher)
                    else -> holder.role.text = "无"
                }
            }
        Glide.with(context).load(
            getItem(position).avatar
        ).apply(RequestOptions.bitmapTransform(CircleCrop())).error(R.mipmap.ic_launcher_round)
            .into(
                holder.avatar
            )
    }

    override fun getItemCount(): Int = filterData.size


    fun setData(data: List<User>) {
        sourceData = data.toMutableList()
        filterData = sourceData
        notifyDataSetChanged()
    }

    fun addData(u: User) {
        sourceData.add(u)
        filterData = sourceData
        notifyItemChanged(filterData.size)
    }

    fun removeData(id: String) {
        sourceData.forEach { user ->
            if (user.id == id) {
                sourceData.remove(user)
                filterData = sourceData
                notifyDataSetChanged()
                return
            }
        }
    }

    fun getItem(position: Int): User {
        return filterData[position]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.iv_avatar)
        val name: TextView = view.findViewById(R.id.tv_name)
        val role: TextView = view.findViewById(R.id.tv_role)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            //执行过滤操作
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                filterData = if (charString.isEmpty()) {
                    //没有过滤的内容，则使用源数据
                    sourceData
                } else {
                    val filteredList = mutableListOf<User>()
                    for (user in sourceData) {
                        //这里根据需求，添加匹配规则
                        if (user.name.contains(charString)) {
                            filteredList.add(user)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filterData
                return filterResults
            }

            //把过滤后的值返回出来
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filterData = filterResults.values as MutableList<User>
                notifyDataSetChanged()
            }
        }
    }

}