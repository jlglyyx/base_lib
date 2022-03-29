package com.yang.module_login.ui.activity

import android.text.TextUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.interceptor.UrlInterceptor
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.getDefaultMMKV
import com.yang.module_login.R
import kotlinx.android.synthetic.main.act_connect_address.*

/**
 * @Author Administrator
 * @ClassName ConnectAddressActivity
 * @Description
 * @Date 2021/10/22 15:35
 */
@Route(path = AppConstant.RoutePath.CONNECT_ADDRESS_ACTIVITY)
class ConnectAddressActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.act_connect_address
    }

    override fun initData() {
    }

    override fun initView() {
        val defaultMMKV = getDefaultMMKV()

        et_ip.setText(
            defaultMMKV.decodeString(
                AppConstant.Constant.IP,
                AppConstant.ClientInfo.BASE_IP
            )
        )
        et_port.setText(
            defaultMMKV.decodeString(
                AppConstant.Constant.PORT,
                AppConstant.ClientInfo.BASE_PORT
            )
        )
        bt_confirm.clicks().subscribe {
            if (TextUtils.isEmpty(et_ip.text.toString())) {
                return@subscribe
            }
            val ip = et_ip.text.toString()
            val port = et_port.text.toString()
            defaultMMKV.encode(AppConstant.Constant.IP, ip)
            defaultMMKV.encode(AppConstant.Constant.PORT, port)
            UrlInterceptor.url = "$ip:$port/"
            finish()
        }
    }

    override fun initViewModel() {
    }
}