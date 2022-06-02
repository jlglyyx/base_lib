package com.yang.module_login.ui.activity

import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.room.BaseAppDatabase
import com.yang.lib_common.scope.ActivityScope
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.getCurrentUserId
import com.yang.lib_common.util.updateUserInfo
import com.yang.module_login.R
import kotlinx.android.synthetic.main.act_user_type_select.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec

/**
 * @ClassName: UserTypeSelectActivity
 * @Description:
 * @Author: yxy
 * @Date: 2022/6/2 14:30
 */
@Route(path = AppConstant.RoutePath.USER_TYPE_SELECT_ACTIVITY)
class UserTypeSelectActivity : BaseActivity() {


    override fun getLayout(): Int {
        return R.layout.act_user_type_select
    }

    override fun initData() {
    }

    override fun initView() {

        ll_merchant.clicks().subscribe {
            login(1)
        }
        ll_buyer.clicks().subscribe {
            login(2)
        }

    }

    override fun initViewModel() {
    }

    private fun login(type: Int) {

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val userInfo = BaseAppDatabase.instance.userInfoDao().queryData(getCurrentUserId())
                userInfo.userType = type
                BaseAppDatabase.instance.userInfoDao().updateData(userInfo)
                updateUserInfo(userInfo)
                withContext(Dispatchers.Main) {
                    buildARouter(AppConstant.RoutePath.MAIN_ACTIVITY).withOptionsCompat(
                        ActivityOptionsCompat.makeCustomAnimation(
                            this@UserTypeSelectActivity,
                            R.anim.bottom_in,
                            R.anim.bottom_out
                        )
                    ).navigation()
                }
            }
        }


    }
}