package com.hyphenate.kotlineaseim.view.viewholder

import android.view.View
import android.widget.ImageView
import com.hyphenate.kotlineaseim.R
import com.hyphenate.kotlineaseim.utils.CommonUtil
import com.hyphenate.kotlineaseim.view.`interface`.MessageListItemClickListener

class ImageViewHolder(val view: View,
                      itemClickListener: MessageListItemClickListener, fragmentNum: Int
) : ChatRowViewHolder(view, itemClickListener, fragmentNum) {
    private val img: ImageView = itemView.findViewById(R.id.iv_img)
    override fun onSetUpView() {
        CommonUtil.showImage(view.context.applicationContext, img, message)
    }

    override fun onMessageSuccess() {
        super.onMessageSuccess()
        CommonUtil.showImage(view.context.applicationContext, img, message)
    }

    override fun onMessageInProgress() {
        super.onMessageInProgress()
    }
}