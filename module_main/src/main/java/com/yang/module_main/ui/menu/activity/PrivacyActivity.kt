package com.yang.module_main.ui.menu.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.module_main.R

/**
 * @Author Administrator
 * @ClassName PrivacyActivity
 * @Description
 * @Date 2021/9/28 10:57
 */
@Route(path = AppConstant.RoutePath.PRIVACY_ACTIVITY)
class PrivacyActivity: BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.act_privacy
    }

    override fun initData() {
    }

    override fun initView() {
    }

    override fun initViewModel() {
    }
}