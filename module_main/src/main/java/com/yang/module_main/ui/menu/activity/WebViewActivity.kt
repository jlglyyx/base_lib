package com.yang.module_main.ui.menu.activity

import android.graphics.Bitmap
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.*
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.module_main.R
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.act_web_view.*


/**
 * @Author Administrator
 * @ClassName WebViewActivity
 * @Description
 * @Date 2021/11/16 14:19
 */
@Route(path = AppConstant.RoutePath.WEB_VIEW_ACTIVITY)
class WebViewActivity : BaseActivity() {

    @InjectViewModel
    lateinit var mainViewModel: MainViewModel

    private var url = "https://www.baidu.com/"

    override fun getLayout(): Int {
        return R.layout.act_web_view
    }

    override fun initData() {


    }

    override fun initViewModel() {

        InjectViewModelProxy.inject(this)
    }

    override fun initView() {
        val map = mutableMapOf<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        val intent = intent
        val title = intent.getStringExtra(AppConstant.Constant.TITLE)
        val url = intent.getStringExtra(AppConstant.Constant.URL)
        title?.let {
            commonToolBar.centerContent = it
        }
        url?.let {
            this.url = it
        }
        initWebView()
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mainViewModel.uC
    }

    private fun initWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
                return super.shouldOverrideUrlLoading(p0, p1)
            }
            override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
                super.onPageStarted(p0, p1, p2)
                mLoadingProgressView.loadStart = true
                Log.i(TAG, "onPageStarted: 加载开始 $p1  $p2")
            }

            override fun onPageFinished(p0: WebView?, p1: String?) {
                super.onPageFinished(p0, p1)
                mLoadingProgressView.loadStart = false
                Log.i(TAG, "onPageFinished: 加载完成 $p1")

            }

            override fun onReceivedError(p0: WebView?, p1: Int, p2: String?, p3: String?) {
                super.onReceivedError(p0, p1, p2, p3)
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(p0: WebView?, p1: Int) {
                super.onProgressChanged(p0, p1)
                mLoadingProgressView.mProgress = p1
                Log.i(TAG, "onProgressChanged: $p1")
            }
        }



        webView.loadUrl(url)
        webView.settings.apply {
            javaScriptEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            pluginsEnabled = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK//关闭webview中缓存
            allowFileAccess = true //设置可以访问文件
            javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
            loadsImagesAutomatically = true //支持自动加载图片
            defaultTextEncodingName = "utf-8"//设置编码格式
        }


        Log.i(TAG, "initWebView: ${webView.settingsExtension == null}   ${webView.x5WebViewExtension == null}")
        QbSdk.clearAllWebViewCache(this@WebViewActivity, true)

    }





    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.destroy()
    }
}