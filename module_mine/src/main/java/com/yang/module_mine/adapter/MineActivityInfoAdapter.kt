package com.yang.module_mine.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yang.module_mine.R
import com.yang.module_mine.data.MineActivityInfoData

/**
 * @Author Administrator
 * @ClassName ActivityInfoAdapter
 * @Description
 * @Date 2021/8/27 14:25
 */
class MineActivityInfoAdapter(layoutResId: Int, data: MutableList<MineActivityInfoData>) : BaseQuickAdapter<MineActivityInfoData, BaseViewHolder>(layoutResId,data) {

    override fun convert(helper: BaseViewHolder, item: MineActivityInfoData) {
        helper.setText(R.id.tv_name,item.key)
    }
}