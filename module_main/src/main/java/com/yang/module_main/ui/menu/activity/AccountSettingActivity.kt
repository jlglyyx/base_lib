package com.yang.module_main.ui.menu.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.module_main.R
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.act_account_setting.*

/**
 * @Author Administrator
 * @ClassName AccountSettingActivity
 * @Description 设置
 * @Date 2021/9/28 10:47
 */
@Route(path = AppConstant.RoutePath.ACCOUNT_SETTING_ACTIVITY)
class AccountSettingActivity:BaseActivity() {

    @InjectViewModel
    lateinit var mainViewModel: MainViewModel


    override fun getLayout(): Int {
        return R.layout.act_account_setting
    }

    override fun initData() {
    }

    override fun initView() {
        icv_change_password.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.MINE_CHANGE_PASSWORD_ACTIVITY).navigation()
        }
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }
}