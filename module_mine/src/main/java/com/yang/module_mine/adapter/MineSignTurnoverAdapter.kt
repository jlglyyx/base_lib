package com.yang.module_mine.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yang.lib_common.util.simpleDateFormat
import com.yang.module_mine.R
import com.yang.module_mine.data.MineSignTurnoverData

/**
 * @Author Administrator
 * @ClassName MineSignTurnoverAdapter
 * @Description
 * @Date 2021/9/10 11:13
 */
class MineSignTurnoverAdapter(data: MutableList<MineSignTurnoverData>?) :
    BaseQuickAdapter<MineSignTurnoverData, BaseViewHolder>(data) {
    init {
        mLayoutResId = R.layout.item_mine_obtain
    }

    override fun convert(helper: BaseViewHolder, item: MineSignTurnoverData) {
        helper.setText(R.id.tv_title, item.title)
            .setText(R.id.tv_content, item.content)
            .setText(R.id.tv_count, "共计："+item.count)
            .setText(R.id.tv_time, simpleDateFormat.format(System.currentTimeMillis()))
    }
}