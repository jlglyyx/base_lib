package com.yang.module_main.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yang.module_main.R

/**
 *des:
 *
 *@author My Live
 *@date 2021/9/4
 */
class DynamicAdapter(layoutResId: Int, data: MutableList<String>?) : BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: String) {
        helper.setVisible(R.id.iv_play,item.endsWith(".mp4"))
        val imageView = helper.getView<ImageView>(R.id.iv_nine_image)
        if (item.endsWith(".mp4")){
            Glide.with(mContext)
                .setDefaultRequestOptions(RequestOptions().frame(1000))
                .load(item)
                .centerCrop()
                .error(R.drawable.iv_image_error)
                .placeholder(R.drawable.iv_image_placeholder)
                .into(imageView)
        }else{
            Glide.with(mContext)
                .load(item)
                .centerCrop()
                .error(R.drawable.iv_image_error)
                .placeholder(R.drawable.iv_image_placeholder)
                .into(imageView)
        }
    }
}