package com.yang.module_mine.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yang.module_mine.R
import com.yang.module_mine.data.MineShippingAddressData

/**
 * @Author Administrator
 * @ClassName MineShippingAddressData
 * @Description
 * @Date 2021/9/10 11:13
 */
class MineShippingAddressAdapter(layoutResId: Int, data: MutableList<MineShippingAddressData>) :
    BaseQuickAdapter<MineShippingAddressData, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: MineShippingAddressData) {
        helper.setText(R.id.tv_address, item.address)
            .setText(R.id.tv_name, item.name)
            .setText(R.id.tv_phone, item.phone)
            .setGone(R.id.tv_status, item.default)
            .setText(R.id.tv_status, if (item.default) "默认" else "")
    }
}