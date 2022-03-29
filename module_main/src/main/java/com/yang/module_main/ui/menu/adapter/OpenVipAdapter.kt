package com.yang.module_main.ui.menu.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yang.module_main.R
import com.yang.module_main.ui.menu.data.OpenVipData

/**
 * @Author Administrator
 * @ClassName OpenVipAdapter
 * @Description
 * @Date 2021/9/29 15:16
 */
class OpenVipAdapter(layoutResId: Int, data: MutableList<OpenVipData>?) :
    BaseQuickAdapter<OpenVipData, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: OpenVipData) {

        helper.setText(R.id.tv_type, item.type)
            .setText(R.id.tv_experience, "+${item.experience}")
            .setText(R.id.tv_price, "￥${item.price}")
            .setText(R.id.tv_time, "${item.time}天")
    }
}