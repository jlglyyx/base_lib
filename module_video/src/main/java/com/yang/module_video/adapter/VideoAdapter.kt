package com.yang.module_video.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.yang.module_video.R
import com.yang.lib_common.room.entity.VideoDataItem

/**
 * @Author Administrator
 * @ClassName PictureAdapter
 * @Description
 * @Date 2021/9/9 11:02
 */
class VideoAdapter(layoutResId: Int, list: MutableList<VideoDataItem>): BaseQuickAdapter<VideoDataItem, BaseViewHolder>(layoutResId, list) {
    override fun convert(helper: BaseViewHolder, item: VideoDataItem) {
        val sivImg = helper.getView<ShapeableImageView>(R.id.siv_img)
        helper.setTag(R.id.siv_img, item.id)
            .setText(R.id.tv_title,item.videoTitle)
            .setText(R.id.tv_desc,item.videoName)
//                .setGone(R.id.tv_title,!TextUtils.isEmpty(item.imageTitle))
//                .setGone(R.id.tv_desc,!TextUtils.isEmpty(item.imageDesc))
        Glide.with(sivImg)
            .load(item.videoUrl)
            .error(R.drawable.iv_image_error)
            .placeholder(R.drawable.iv_image_placeholder)
            .into(sivImg)
    }
}