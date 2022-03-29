package com.yang.module_login.ui.activity

import android.text.TextUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.entity.UserInfoData
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.formatDate_YYYY_MMM_DD_HHMMSS
import com.yang.lib_common.util.isPhone
import com.yang.lib_common.util.showShort
import com.yang.module_login.R
import com.yang.module_login.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.act_register.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

@Route(path = AppConstant.RoutePath.REGISTER_ACTIVITY)
class RegisterActivity : BaseActivity() {

    @InjectViewModel
    lateinit var loginViewModel: LoginViewModel

    override fun getLayout(): Int {
        return R.layout.act_register
    }

    override fun initData() {
    }

    override fun initView() {
        lifecycle.addObserver(isv_image)
        bt_register.clicks().subscribe {
            checkForm()
        }
        tv_verification_code.clicks().subscribe {
            val phone = et_user.text.toString().isPhone()
            if (phone) {
                initTimer()
            } else {
                showShort("请输入正确的手机号")
            }

        }
        tv_to_login.clicks().subscribe {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return loginViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)

        loginViewModel.mUserInfoData.observe(this, Observer {
            finish()
        })
    }

    private fun checkForm() {
        if (TextUtils.isEmpty(et_user.text.toString())) {
            showShort(getString(R.string.string_input_account))
            return
        }
        if (TextUtils.isEmpty(et_password.text.toString())) {
            showShort(getString(R.string.string_input_password))
            return
        }

        if (et_password.text.toString().length < 6) {
            showShort(getString(R.string.string_password_must_six))
            return
        }

        if (TextUtils.isEmpty(et_confirm_password.text.toString())) {
            showShort(getString(R.string.string_confirm_password))
            return
        }
        if (!TextUtils.equals(et_password.text.toString(), et_confirm_password.text.toString())) {
            showShort(getString(R.string.string_password_no_match))
            return
        }
        val userInfoData = UserInfoData(
            UUID.randomUUID().toString().replace("-", ""),
            null,
            null,
            null,
            null,
            null,
            null,
            et_user.text.toString(),
            et_password.text.toString(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            et_user.text.toString().toInt(),
            false,
            0,
            null,
            null,
            null,
            formatDate_YYYY_MMM_DD_HHMMSS.format(Date()),
            formatDate_YYYY_MMM_DD_HHMMSS.format(Date()),
            ""
        )
        loginViewModel.register(userInfoData)

    }

    private fun initTimer() {
        lifecycleScope.launch {
            tv_verification_code.isClickable = false
            for (i in 60 downTo 0) {
                tv_verification_code.text = "${i}秒后获取验证码"
                delay(1000)
            }
            tv_verification_code.isClickable = true
            tv_verification_code.text = "重新获取验证码"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }
}