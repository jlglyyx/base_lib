package com.yang.module_mine.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yang.lib_common.util.simpleDateFormat
import com.yang.module_mine.R
import com.yang.module_mine.data.MineObtainTurnoverData

/**
 * @Author Administrator
 * @ClassName MineObtainAdapter
 * @Description
 * @Date 2021/9/10 11:13
 */
class MineObtainTurnoverAdapter(data: MutableList<MineObtainTurnoverData>?) :
    BaseQuickAdapter<MineObtainTurnoverData, BaseViewHolder>(data) {
    init {
        mLayoutResId = R.layout.item_mine_obtain
    }

    override fun convert(helper: BaseViewHolder, item: MineObtainTurnoverData) {
        helper.setText(R.id.tv_title, item.title)
            .setText(R.id.tv_content, item.content)
            .setText(R.id.tv_count, "共计："+item.count)
            .setText(R.id.tv_time, simpleDateFormat.format(System.currentTimeMillis()))
    }
}