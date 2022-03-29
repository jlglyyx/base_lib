package com.yang.module_main.ui.menu.activity

import androidx.activity.result.contract.ActivityResultContracts
import com.alibaba.android.arouter.facade.annotation.Route
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.util.clicks
import com.yang.module_main.R
import kotlinx.android.synthetic.main.act_download_setting.*

/**
 * @Author Administrator
 * @ClassName DownloadSettingActivity
 * @Description 下载设置
 * @Date 2021/9/28 10:47
 */
@Route(path = AppConstant.RoutePath.DOWNLOAD_SETTING_ACTIVITY)
class DownloadSettingActivity:BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.act_download_setting
    }

    override fun initData() {
    }

    override fun initView() {
        val registerForActivityResult = registerForActivityResult(
            ActivityResultContracts.OpenDocument()) {


            //Log.i(TAG, "initView: $it    ${it.path}    ${uri2path(this, it)}")

        }

        icv_download_location.clicks().subscribe {

            registerForActivityResult.launch(arrayOf("*/*"))

        }
        icv_clear_download.clicks().subscribe {

        }
    }

    override fun initViewModel() {
    }
}