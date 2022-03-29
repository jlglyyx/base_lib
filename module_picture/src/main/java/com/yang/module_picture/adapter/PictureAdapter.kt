package com.yang.module_picture.adapter

import android.app.Activity
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.room.entity.ImageDataItem
import com.yang.module_picture.R

/**
 * @Author Administrator
 * @ClassName PictureAdapter
 * @Description
 * @Date 2021/9/9 11:02
 */
class PictureAdapter(private var mActivity:Activity, list: MutableList<ImageDataItem>): BaseMultiItemQuickAdapter<ImageDataItem, BaseViewHolder>(list) {

    init {
        addItemType(AppConstant.Constant.ITEM_CONTENT, R.layout.item_picture_image)
        addItemType(AppConstant.Constant.ITEM_AD, R.layout.item_ad)
    }

    override fun convert(helper: BaseViewHolder, item: ImageDataItem) {
        when (item.mItemType) {
            AppConstant.Constant.ITEM_CONTENT -> {
                val sivImg = helper.getView<ShapeableImageView>(R.id.siv_img)
                helper
                    .setText(R.id.tv_title,item.imageTitle)
                    .setText(R.id.tv_desc,item.imageDesc)
//                .setGone(R.id.tv_title,!TextUtils.isEmpty(item.imageTitle))
//                .setGone(R.id.tv_desc,!TextUtils.isEmpty(item.imageDesc))
                Glide.with(sivImg)
                    .load(item.imageUrl)
                    .error(R.drawable.iv_image_error)
                    .placeholder(R.drawable.iv_image_placeholder)
                    .into(sivImg)
            }
            AppConstant.Constant.ITEM_AD -> {
                /*广告view*/
                val adContainer = helper.getView<FrameLayout>(R.id.adContainer)
                adContainer.removeAllViews()
                item.mTTNativeExpressAd?.expressAdView?.let {
                    if (it.parent == null) {
                        adContainer.addView(it)
                    }
                }
                item.mTTNativeExpressAd?.setDislikeCallback(mActivity, object :
                    TTAdDislike.DislikeInteractionCallback {
                    override fun onShow() {

                    }

                    override fun onSelected(p0: Int, p1: String?, p2: Boolean) {
                        val indexOf = mData.indexOf(item)
                        this@PictureAdapter.remove(indexOf)
                    }

                    override fun onCancel() {

                    }

                })
            }
        }

    }
}