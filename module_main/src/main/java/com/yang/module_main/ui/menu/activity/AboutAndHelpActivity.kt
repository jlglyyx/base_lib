package com.yang.module_main.ui.menu.activity

import com.alibaba.android.arouter.facade.annotation.Route
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.getVersionName
import com.yang.lib_common.util.showShort
import com.yang.module_main.R
import kotlinx.android.synthetic.main.act_about_and_help.*

/**
 * @Author Administrator
 * @ClassName AboutAndHelpActivity
 * @Description 关于与帮助
 * @Date 2021/9/28 10:47
 */
@Route(path = AppConstant.RoutePath.ABOUT_AND_HELP_ACTIVITY)
class AboutAndHelpActivity:BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.act_about_and_help
    }

    override fun initData() {
    }

    override fun initView() {

        icv_ad.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.WEB_VIEW_ACTIVITY)
                .withString(AppConstant.Constant.TITLE,"关于广告")
                .withString(AppConstant.Constant.URL,"file:///android_asset/AboutAD.htm")
                .navigation()
        }

        icv_version.rightContent = getVersionName(this)
        icv_help.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.WEB_VIEW_ACTIVITY)
                .withString(AppConstant.Constant.TITLE,"使用帮助")
                .withString(AppConstant.Constant.URL,"https://www.baidu.com/")
                .navigation()
        }
        icv_feed_back.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.WEB_VIEW_ACTIVITY)
                .withString(AppConstant.Constant.TITLE,"意见反馈")
                .withString(AppConstant.Constant.URL,"https://www.baidu.com/")
                .navigation()
        }
        icv_version.clicks().subscribe {
           showShort("已是最新版本")
        }
    }

    override fun initViewModel() {

    }
}