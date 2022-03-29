package com.yang.module_login.ui.activity

import android.media.MediaPlayer
import android.os.Environment
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.app.BaseApplication
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.*
import com.yang.module_login.R
import com.yang.module_login.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.act_login.*
import kotlinx.android.synthetic.main.act_login.et_password
import kotlinx.android.synthetic.main.act_login.et_user
import kotlinx.android.synthetic.main.act_login.tv_verification_code
import kotlinx.android.synthetic.main.act_register.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@Route(path = AppConstant.RoutePath.LOGIN_ACTIVITY)
class LoginActivity : BaseActivity() {

    @Inject
    lateinit var gson: Gson

    @InjectViewModel
    lateinit var loginViewModel: LoginViewModel

    private var videoUrl = "${Environment.getExternalStorageDirectory()}/MFiles/video/aaa.mp4"

    private var mediaPlayer:MediaPlayer? = null

    private var data = -1

    private var passwordVisibility = false

    override fun getLayout(): Int {
        return R.layout.act_login
    }

    override fun initData() {
        data = intent.getIntExtra(AppConstant.Constant.DATA,-1)

        Log.i(TAG, "initData: $loginViewModel")
    }

    override fun initView() {
        initVideoView()
        iv_password_visibility.setOnClickListener {
            if (passwordVisibility){
                et_password.transformationMethod = PasswordTransformationMethod.getInstance()
                iv_password_visibility.setImageResource(R.drawable.iv_password_gone)
            }else{
                et_password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                iv_password_visibility.setImageResource(R.drawable.iv_password_visibility)
            }
            passwordVisibility = !passwordVisibility
            et_password.setSelection(et_password.text.toString().length)
        }
        bt_login.clicks().subscribe {
            checkForm()
        }
        tv_not_login.clicks().subscribe {
            getDefaultMMKV().encode(
                AppConstant.Constant.LOGIN_STATUS,
                AppConstant.Constant.LOGIN_NO_PERMISSION
            )
            buildARouter(AppConstant.RoutePath.MAIN_ACTIVITY).withOptionsCompat(
                ActivityOptionsCompat.makeCustomAnimation(
                    this@LoginActivity,
                    R.anim.bottom_in,
                    R.anim.bottom_out
                )
            ).navigation()
            finish()
        }

        tv_verification_code.clicks().subscribe {
            initTimer()
        }
        tv_to_register.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.REGISTER_ACTIVITY).withOptionsCompat(
                ActivityOptionsCompat.makeCustomAnimation(
                    this@LoginActivity, R.anim.fade_in,
                    R.anim.fade_out
                )
            ).navigation()


        }
        iv_icon.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.CONNECT_ADDRESS_ACTIVITY).navigation()
        }
    }


    /**
     * 1.去除loginViewModelFactory
     * 2.去除getLoginComponent(this).inject(this)
     * 3.去除loginViewModel = getViewModel(loginViewModelFactory,LoginViewModel::class.java)
     */

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)

        loginViewModel.mUserInfoData.observe(this, Observer {
            getDefaultMMKV().encode(
                AppConstant.Constant.LOGIN_STATUS,
                AppConstant.Constant.LOGIN_SUCCESS
            )
            updateUserInfo(it)
            buildARouter(AppConstant.RoutePath.MAIN_ACTIVITY).withOptionsCompat(
                ActivityOptionsCompat.makeCustomAnimation(
                    this@LoginActivity,
                    R.anim.bottom_in,
                    R.anim.bottom_out
                )
            ).navigation()
            finish()
        })
    }

    override fun initUIChangeLiveData(): UIChangeLiveData? {
        return loginViewModel.uC
    }

    private fun checkForm() {

        if (TextUtils.isEmpty(et_user.text.toString())) {
            showShort(getString(R.string.string_input_account))
            return
        }

        if (TextUtils.equals(et_user.text.toString(),"30")){
            deleteDirectory(File("${BaseApplication.baseApplication.cacheDir}/app_/db_"))
            Log.i(TAG, "checkForm: 数据库删除成功")
        }

        if (TextUtils.isEmpty(et_password.text.toString())) {
            showShort(getString(R.string.string_input_password))
            return
        }
        if (et_password.text.toString().length < 6){
            showShort(getString(R.string.string_password_must_six))
            return
        }
        loginViewModel.login(et_user.text.toString(), et_password.text.toString())

    }

    private fun initTimer() {
        lifecycleScope.launch {
            tv_verification_code.isClickable = false
            for (i in 60 downTo 0) {
                tv_verification_code.text = "${i}秒后获取验证码"
                delay(1000)
            }
            tv_verification_code.isClickable = true
            tv_verification_code.text = getString(R.string.string_re_get_verification_code)
        }
    }

    private fun initVideoView() {
        lifecycle.addObserver(surfaceView)
        mediaPlayer = surfaceView.initMediaPlayer(videoUrl)
    }

    override fun finish() {
        super.finish()
        if (data != -1){
            overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }
}