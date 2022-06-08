package com.yang.lib_common.dialog

import android.content.Context
import com.loc.fa
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.CenterPopupView
import com.yang.lib_common.R
import com.yang.lib_common.util.clicks
import kotlinx.android.synthetic.main.dialog_pay_task.view.*

/**
 * @ClassName: PayTaskDialog
 * @Description:
 * @Author: yxy
 * @Date: 2022/6/8 15:38
 */
class PayTaskDialog(context: Context) : BottomPopupView(context) {

    var onItemClickListener:OnItemClickListener? = null

    interface OnItemClickListener{

        fun onCancelClickListener()

        fun onConfirmClickListener()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.dialog_pay_task
    }


    override fun onCreate() {
        super.onCreate()

        tv_cancel.clicks().subscribe {
            onItemClickListener?.onCancelClickListener()
        }
        tv_confirm.clicks().subscribe {
            onItemClickListener?.onConfirmClickListener()
        }
    }


    override fun onDismiss() {
        super.onDismiss()
    }
}