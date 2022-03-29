package com.yang.module_mine.ui.activity

import android.text.TextUtils
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.getDefaultMMKV
import com.yang.lib_common.util.removeAllActivity
import com.yang.lib_common.util.showShort
import com.yang.lib_common.widget.CommonToolBar
import com.yang.module_mine.R
import com.yang.module_mine.viewmodel.MineViewModel
import kotlinx.android.synthetic.main.act_change_password.*

/**
 * @Author Administrator
 * @ClassName ChangePasswordActivity
 * @Description 修改密码
 * @Date 2021/9/28 10:47
 */
@Route(path = AppConstant.RoutePath.MINE_CHANGE_PASSWORD_ACTIVITY)
class MineChangePasswordActivity : BaseActivity() {

    @InjectViewModel
    lateinit var mineViewModel: MineViewModel

    override fun getLayout(): Int {
        return R.layout.act_change_password
    }

    override fun initData() {

        mineViewModel.uC.requestSuccessEvent.observe(this, Observer {
            getDefaultMMKV().encode(AppConstant.Constant.LOGIN_STATUS, -1)
            getDefaultMMKV().clearAll()
            removeAllActivity()
            buildARouter(AppConstant.RoutePath.LOGIN_ACTIVITY).withOptionsCompat(
                ActivityOptionsCompat.makeCustomAnimation(
                    this, R.anim.fade_in,
                    R.anim.fade_out
                )
            ).navigation(this)
        })

    }

    override fun initView() {
        commonToolBar.tVRightCallBack = object : CommonToolBar.TVRightCallBack {
            override fun tvRightClickListener() {
                checkForm()
            }
        }
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mineViewModel.uC
    }

    private fun checkForm() {
        val password = et_password.text.toString()
        val confirmPassword = et_confirm_password.text.toString()
        if (TextUtils.isEmpty(password)) {
            showShort("请输入密码")
            return
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            showShort("请确认密码")
            return
        }
        if (!TextUtils.equals(password,confirmPassword)) {
            showShort("两次密码不一致")
            return
        }
        mineViewModel.changePassword(password)
    }
}