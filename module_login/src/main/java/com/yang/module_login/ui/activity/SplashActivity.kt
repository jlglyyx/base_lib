package com.yang.module_login.ui.activity

import android.Manifest
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.bytedance.sdk.openadsdk.*
import com.google.gson.Gson
import com.tbruyelle.rxpermissions3.RxPermissions
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.entity.UserInfoData
import com.yang.lib_common.util.*
import com.yang.module_login.R
import com.yang.module_login.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.act_splash.*
import kotlinx.coroutines.*
import javax.inject.Inject

@Route(path = AppConstant.RoutePath.SPLASH_ACTIVITY)
class SplashActivity : BaseActivity(), TTSplashAd.AdInteractionListener {

    @Inject
    lateinit var gson: Gson

    @InjectViewModel
    lateinit var loginViewModel: LoginViewModel

    private var mForceGoMain = false

    private var mTTAdNative: TTAdNative? = null

    private var userInfo: UserInfoData? = null

    override fun getLayout(): Int {
        return R.layout.act_splash
    }

    override fun onResume() {
        super.onResume()
//        if (mForceGoMain) {
//            toLogin()
//        }
    }

    override fun onStop() {
        super.onStop()
        //mForceGoMain = true
    }

    override fun initData() {

    }

    override fun initView() {
        userInfo = getUserInfo()
        initPermission()


    }

    private fun loadSplashAd() {
        mTTAdNative = TTAdSdk.getAdManager().createAdNative(this@SplashActivity)
        lifecycleScope.launch {
            delay(40)
            val build = AdSlot.Builder()
                .setCodeId("887672240")
                //不区分渲染方式，要求开发者同时设置setImageAcceptedSize（单位：px）和setExpressViewAcceptedSize（单位：dp ）接口，不同时设置可能会导致展示异常。
                .setImageAcceptedSize(
                    getScreenPx(this@SplashActivity)[0],
                    getScreenPx(this@SplashActivity)[1]
                )
                .setAdLoadType(TTAdLoadType.PRELOAD)//推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
                .build()
            mTTAdNative?.loadSplashAd(build, object : TTAdNative.SplashAdListener {
                override fun onError(p0: Int, p1: String?) {
                    Log.i(TAG, "onError: $p0  $p1")
                    toLogin()
                }

                override fun onTimeout() {
                    Log.i(TAG, "onTimeout: ")
                    toLogin()
                }

                override fun onSplashAdLoad(p0: TTSplashAd?) {
                    if (p0 == null) {
                        return
                    }

                    splashContainer.addView(p0.splashView)
                    Log.i(TAG, "onSplashAdLoad: $p0")
                    if (p0.interactionType == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                        p0.setDownloadListener(object : TTAppDownloadListener {
                            var hasShow = false
                            override fun onIdle() {}
                            override fun onDownloadActive(
                                totalBytes: Long,
                                currBytes: Long,
                                fileName: String,
                                appName: String
                            ) {
                                if (!hasShow) {
                                    showShort("下载中...")
                                    hasShow = true
                                }
                            }

                            override fun onDownloadPaused(
                                totalBytes: Long,
                                currBytes: Long,
                                fileName: String,
                                appName: String
                            ) {
                                showShort("下载暂停...")
                            }

                            override fun onDownloadFailed(
                                totalBytes: Long,
                                currBytes: Long,
                                fileName: String,
                                appName: String
                            ) {
                                showShort("下载失败...")
                            }

                            override fun onDownloadFinished(
                                totalBytes: Long,
                                fileName: String,
                                appName: String
                            ) {
                                showShort("下载完成...")
                            }

                            override fun onInstalled(fileName: String, appName: String) {
                                showShort("安装完成...")
                            }
                        })
                    }
                    p0.setSplashInteractionListener(this@SplashActivity)
                }

            }, 3000)
        }
    }

    override fun onAdClicked(p0: View?, p1: Int) {
        Log.i(TAG, "onAdClicked: 开屏广告点击")
        //showShort("开屏广告点击")
    }

    override fun onAdShow(p0: View?, p1: Int) {
        //showShort("开屏广告展示")
        Log.i(TAG, "onAdShow: 开屏广告展示")
    }

    override fun onAdSkip() {
        //showShort("开屏广告跳过")
        Log.i(TAG, "onAdSkip: 开屏广告跳过")
        toLogin()
    }

    override fun onAdTimeOver() {
        //showShort("开屏广告倒计时结束")
        Log.i(TAG, "onAdTimeOver: 开屏广告倒计时结束")
        toLogin()
    }


    private fun initPermission() {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        )
            .subscribe {
                when {
                    it.granted -> {
                        loadSplashAd()
                    }
                    it.shouldShowRequestPermissionRationale -> {

                        showShort("逼崽子把权限关了还怎么玩，赶紧打开")
                    }
                    else -> {
                        showShort("逼崽子把权限关了还怎么玩，赶紧打开")
                    }
                }
            }
    }


    private fun toLogin() {
        if (userInfo == null) {
            buildARouter(AppConstant.RoutePath.LOGIN_ACTIVITY)
                .withOptionsCompat(
                    ActivityOptionsCompat.makeCustomAnimation(
                        this@SplashActivity,
                        0,
                        0
                    )
                )
                .navigation(this@SplashActivity)
            finish()
        } else {
            loginViewModel.splashLogin(userInfo?.userAccount?:"", userInfo?.userPassword?:"")
            loginViewModel.mUserInfoData.observe(this, Observer { mUserInfo ->
                getDefaultMMKV().encode(
                    AppConstant.Constant.LOGIN_STATUS,
                    AppConstant.Constant.LOGIN_SUCCESS
                )
                updateUserInfo(mUserInfo)
                buildARouter(AppConstant.RoutePath.MAIN_ACTIVITY).withOptionsCompat(
                    ActivityOptionsCompat.makeCustomAnimation(
                        this@SplashActivity,
                        R.anim.fade_in,
                        R.anim.fade_out
                    )
                ).navigation()
                finish()
            })
        }
    }


    override fun initUIChangeLiveData(): UIChangeLiveData {
        return loginViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        splashContainer.removeAllViews()
        mTTAdNative = null
        lifecycleScope.cancel()

    }


}