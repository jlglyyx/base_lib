package com.yang.lib_common.dialog

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.RadioGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocation
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lxj.xpopup.core.BottomPopupView
import com.yang.lib_common.R
import com.yang.lib_common.util.LocationUtil
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.getScreenPx
import kotlinx.android.synthetic.main.dialog_filter_task.view.*
import kotlinx.android.synthetic.main.dialog_search_recycler_view.view.*

/**
 * @Author Administrator
 * @ClassName FilterTaskDialog
 * @Description
 * @Date 2021/11/23 11:59
 */
class FilterTaskDialog(context: Context) : BottomPopupView(context) {



    override fun getImplLayoutId(): Int {
        return R.layout.dialog_filter_task
    }

    override fun onCreate() {
        super.onCreate()

        radioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

            }

        })

        tv_cancel.clicks().subscribe {
            clear()
        }

        tv_confirm.clicks().subscribe {
            dismiss()
        }
    }

    override fun getMaxHeight(): Int {
        return getScreenPx(context)[1]/5*3
    }


    private fun clear(){
        radioGroup.clearCheck()
        et_price_start.setText("")
        et_price_end.setText("")
    }


    override fun onDismiss() {
        super.onDismiss()
    }


}