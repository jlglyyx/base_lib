package com.yang.module_main.ui.menu.activity

import android.os.Environment
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.util.*
import com.yang.module_main.R
import kotlinx.android.synthetic.main.act_setting.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * @Author Administrator
 * @ClassName SettingActivity
 * @Description 设置
 * @Date 2021/9/28 10:47
 */
@Route(path = AppConstant.RoutePath.SETTING_ACTIVITY)
class SettingActivity : BaseActivity() {
    override fun getLayout(): Int {
        return R.layout.act_setting
    }

    override fun initData() {
    }

    override fun initView() {
        icv_cache.rightContent =
            formatSize(getAllFileSize(File("${Environment.getExternalStorageDirectory()}/MFiles")))
        icv_cache.clicks().subscribe {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    deleteDirectory(File("${Environment.getExternalStorageDirectory()}/MFiles/picture"))
                }
                icv_cache.rightContent =
                    formatSize(getAllFileSize(File("${Environment.getExternalStorageDirectory()}/MFiles")))
            }
        }
        icv_account_setting.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.ACCOUNT_SETTING_ACTIVITY).navigation()
        }
        icv_download_setting.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.DOWNLOAD_SETTING_ACTIVITY).navigation()
        }
//        icv_font_setting.clicks().subscribe {
//            val list = assets.list("")?.filter {
//                it.endsWith(".ttf", true)
//            }?.toTypedArray()
//            val asBottomList = XPopup.Builder(this).asBottomList(
//                "", list
//            ) { position, text ->
//                getDefaultMMKV().encode(AppConstant.Constant.FONT_TYPE,text)
//                showShort("重启后生效")
//            }
//            asBottomList.findViewById<RecyclerView>(com.lxj.xpopup.R.id.recyclerView).let {
//                it.post {
//                    if (it.measuredHeight > 300f.dip2px(this)) {
//                        it.layoutParams.height = 300f.dip2px(this)
//                    }
//
////                    val itemCount = it.adapter?.itemCount
////                    for (i in 0 until 16){
////                        Log.i(TAG, "initView: $i")
////                        val createFromAsset = Typeface.createFromAsset(assets, list?.get(i))
////                        it[i].findViewById<TextView>(com.lxj.xpopup.R.id.tv_text).typeface = createFromAsset
////                    }
//                }
//
//            }
//            asBottomList.show()
//        }
    }

    override fun initViewModel() {
    }
}