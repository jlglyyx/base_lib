package com.yang.module_main.ui.menu.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.module_main.R

/**
 * @Author Administrator
 * @ClassName AccessibilityActivity
 * @Description 辅助功能
 * @Date 2021/9/28 10:47
 */
@Route(path = AppConstant.RoutePath.ACCESSIBILITY_ACTIVITY)
class AccessibilityActivity:BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.act_accessibility
    }

    override fun initData() {
    }

    override fun initView() {
    }

    override fun initViewModel() {
    }
}