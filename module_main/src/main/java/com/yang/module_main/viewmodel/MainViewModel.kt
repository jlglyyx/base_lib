package com.yang.module_main.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdLoadType
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.yang.lib_common.base.viewmodel.BaseViewModel
import com.yang.lib_common.bus.event.LiveDataBus
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.data.MediaInfoBean
import com.yang.lib_common.util.toCloseAd
import com.yang.lib_common.util.toJson
import com.yang.module_main.R
import com.yang.module_main.data.model.DynamicData
import com.yang.module_main.repository.MainRepository
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URLEncoder
import javax.inject.Inject


/**
 * ClassName: MainViewModel.
 * Created by Administrator on 2021/4/1_15:15.
 * Describe:
 */
class MainViewModel @Inject constructor(
    application: Application,
    private val mainRepository: MainRepository
) : BaseViewModel(application) {

    var dynamicListLiveData = MutableLiveData<MutableList<DynamicData>>()

    var pictureListLiveData = MutableLiveData<MutableList<String>>()

    var pictureCollectListLiveData = MutableLiveData<MutableList<String>>()

    var mTTNativeExpressAdList = mutableListOf<DynamicData>()

    fun addDynamic(dynamicData: DynamicData) {
        launch({
            mainRepository.addDynamic(dynamicData)
        }, {
            LiveDataBus.instance.with("refresh_dynamic").value = "refresh_dynamic"
            finishActivity()
        }, messages = *arrayOf(getString(R.string.string_insert_dynamic_ing), getString(R.string.string_insert_dynamic_success), getString(R.string.string_insert_dynamic_fail)))
    }

    fun getDynamicList(params: Map<String, String>) {
        launch({
            mainRepository.getDynamicList(params)
        }, {
            dynamicListLiveData.postValue(it.data)
        }, {
//            showRecyclerViewErrorEvent()
//            cancelRefreshLoadMore()
            val mutableListOf = mutableListOf<DynamicData>()

            mutableListOf.add(DynamicData().apply {
                userName = "张三"
                imageUrls =
                    "https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img2.baidu.com/it/u=3583098839,704145971&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=889" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg" +
                            "#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg"
                content = "首先来梳理下我们的需求，一般我们设计图都是以固定的尺寸来设计的。比如以分辨率1920px * 1080px来设计，以density为3来标注，也就是屏幕其实是640dp * 360dp。如果我们想在所有设备上显示完全一致，其实是不现实的，因为屏幕高宽比不是固定的，16:9、4:3甚至其他宽高比层出不穷，宽高比不同，显示完全一致就不可能了。但是通常下，我们只需要以宽或高一个维度去适配，比如我们Feed是上下滑动的，只需要保证在所有设备中宽的维度上显示一致即可，再比如一个不支持上下滑动的页面，那么需要保证在高这个维度上都显示一致，尤其不能存在某些设备上显示不全的情况。同时考虑到现在基本都是以dp为单位去做的适配，如果新的方案不支持dp，那么迁移成本也非常高。"
            })
            mutableListOf.add(DynamicData())
            mutableListOf.add(DynamicData().apply {
                userName = "李四"
                content = "首先来梳理下我们的需求，一般我们设计图都是以固定的尺寸来设计的。比如以分辨率1920px * 1080px来设计，以density为3来标注，也就是屏幕其实是640dp * 360dp。如果我们想在所有设备上显示完全一致，其实是不现实的，因为屏幕高宽比不是固定的，16:9、4:3甚至其他宽高比层出不穷，宽高比不同，显示完全一致就不可能了。但是通常下，我们只需要以宽或高一个维度去适配，比如我们Feed是上下滑动的，只需要保证在所有设备中宽的维度上显示一致即可，再比如一个不支持上下滑动的页面，那么需要保证在高这个维度上都显示一致，尤其不能存在某些设备上显示不全的情况。同时考虑到现在基本都是以dp为单位去做的适配，如果新的方案不支持dp，那么迁移成本也非常高。"
                imageUrls =
                    "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
            })
            mutableListOf.add(DynamicData().apply {
                userName = "是法律"
                content = "首先来梳理下我们的需求，一般我们设计图都是以固定的尺寸来设计的。比如以分辨率1920px * 1080px来设计，以density为3来标注，也就是屏幕其实是640dp * 360dp。如果我们想在所有设备上显示完全一致，其实是不现实的，因为屏幕高宽比不是固定的，16:9、4:3甚至其他宽高比层出不穷，宽高比不同，显示完全一致就不可能了。但是通常下，我们只需要以宽或高一个维度去适配，比如我们Feed是上下滑动的，只需要保证在所有设备中宽的维度上显示一致即可，再比如一个不支持上下滑动的页面，那么需要保证在高这个维度上都显示一致，尤其不能存在某些设备上显示不全的情况。同时考虑到现在基本都是以dp为单位去做的适配，如果新的方案不支持dp，那么迁移成本也非常高。"
                imageUrls =
                    "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4#http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4#http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
            })
            mutableListOf.add(DynamicData().apply {
                userName = "斯科拉里"
                content = "首先来梳理下我们的需求，一般我们设计图都是以固定的尺寸来设计的。比如以分辨率1920px * 1080px来设计，以density为3来标注，也就是屏幕其实是640dp * 360dp。如果我们想在所有设备上显示完全一致，其实是不现实的，因为屏幕高宽比不是固定的，16:9、4:3甚至其他宽高比层出不穷，宽高比不同，显示完全一致就不可能了。但是通常下，我们只需要以宽或高一个维度去适配，比如我们Feed是上下滑动的，只需要保证在所有设备中宽的维度上显示一致即可，再比如一个不支持上下滑动的页面，那么需要保证在高这个维度上都显示一致，尤其不能存在某些设备上显示不全的情况。同时考虑到现在基本都是以dp为单位去做的适配，如果新的方案不支持dp，那么迁移成本也非常高。"
                imageUrls =
                    "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg"
            })
            mutableListOf.add(DynamicData().apply {
                userName = "哇哈哈"
                content = "首先来梳理下我们的需求，一般我们设计图都是以固定的尺寸来设计的。比如以分辨率1920px * 1080px来设计，以density为3来标注，也就是屏幕其实是640dp * 360dp。如果我们想在所有设备上显示完全一致，其实是不现实的，因为屏幕高宽比不是固定的，16:9、4:3甚至其他宽高比层出不穷，宽高比不同，显示完全一致就不可能了。但是通常下，我们只需要以宽或高一个维度去适配，比如我们Feed是上下滑动的，只需要保证在所有设备中宽的维度上显示一致即可，再比如一个不支持上下滑动的页面，那么需要保证在高这个维度上都显示一致，尤其不能存在某些设备上显示不全的情况。同时考虑到现在基本都是以dp为单位去做的适配，如果新的方案不支持dp，那么迁移成本也非常高。"
                imageUrls =
                    "https://img1.baidu.com/it/u=3222474767,386356710&fm=26&fmt=auto#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg#https://img2.baidu.com/it/u=3583098839,704145971&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=889"
            })
            dynamicListLiveData.postValue(mutableListOf)
        }, errorDialog = false)
    }

    fun getDynamicDetail(params: Map<String, String>) {
        launch({
            mainRepository.getDynamicList(params)
        }, {
            dynamicListLiveData.postValue(it.data)
        }, {
            val mutableListOf = mutableListOf<DynamicData>()
            mutableListOf.add(DynamicData().apply {
                imageUrls =
                    "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4#https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg"
            })
            dynamicListLiveData.postValue(mutableListOf)
        }, messages = *arrayOf(getString(R.string.string_requesting)))
    }

    fun uploadFile(filePaths: MutableList<MediaInfoBean>) {
        launch({
            val mutableMapOf = mutableMapOf<String, RequestBody>()
            filePaths.forEach {
                val file = File(it.filePath.toString())
                val requestBody = RequestBody.create(MediaType.parse(AppConstant.ClientInfo.CONTENT_TYPE), file)
                val encode =
                    URLEncoder.encode("${System.currentTimeMillis()}_${file.name}", AppConstant.ClientInfo.UTF_8)
                mutableMapOf["file\";filename=\"$encode"] = requestBody
            }
            mainRepository.uploadFile(mutableMapOf)
        }, {
            pictureListLiveData.postValue(it.data)
        }, messages = *arrayOf(getString(R.string.string_uploading), getString(R.string.string_insert_success)))
    }

    fun uploadFileAndParam(filePaths: MutableList<MediaInfoBean>) {
        launch({
            val mutableListOf = mutableListOf<RequestBody>()
            filePaths.forEach {
                val file = File(it.filePath.toString())
                val requestBody = RequestBody.create(MediaType.parse(AppConstant.ClientInfo.CONTENT_TYPE), file)
                val encode =
                    URLEncoder.encode("${System.currentTimeMillis()}_${file.name}", AppConstant.ClientInfo.UTF_8)
                val build = MultipartBody.Builder()
                    .addFormDataPart("param", "".toJson())
                    .addFormDataPart("file", encode, requestBody).build()
                mutableListOf.add(build)
            }

            mainRepository.uploadFileAndParam(mutableListOf)
        }, {
            pictureListLiveData.postValue(it.data)
        }, messages = *arrayOf(getString(R.string.string_uploading), getString(R.string.string_insert_success)))
    }

    fun insertComment(params: Map<String, String>) {
        launch({
            mainRepository.insertComment(params)
        }, {

        })
    }

    fun queryCollect(type: String,pageSize: Int) {
        launch({
            mainRepository.queryCollect(type,pageSize,AppConstant.Constant.PAGE_SIZE_COUNT)
        }, {
            pictureCollectListLiveData.postValue(it.data)
        },{
            cancelRefreshLoadMore()
            showRecyclerViewErrorEvent()
        },errorDialog = false)
    }

    fun loginOut(){
        launch({
            mainRepository.loginOut()
        },{
            requestSuccess()
        },{
            requestSuccess()
        },messages = *arrayOf(getString(R.string.string_login_out_ing), getString(R.string.string_login_out_success)))
    }


    fun loadMainAd(){
        /*vip等级大于等于2不展示广告*/
        if (toCloseAd(2)) {
            return
        }
        val adSlot = AdSlot.Builder()
            .setCodeId("947667043") //广告位id
            .setSupportDeepLink(true)
            .setAdCount(1) //请求广告数量为1到3条
            .setExpressViewAcceptedSize(0f, 0f) //期望模板广告view的size,单位dp
            .setAdLoadType(TTAdLoadType.PRELOAD) //推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
            .build()
        mTTAdNative?.loadNativeExpressAd(adSlot, object : TTAdNative.NativeExpressAdListener {
            override fun onError(p0: Int, p1: String?) {
                Log.i("TAG", "onError: $p0  $p1")
            }
            override fun onNativeExpressAdLoad(p0: MutableList<TTNativeExpressAd>?) {
                Log.i("TAG", "onNativeExpressAdLoad: ${p0?.size}")
                if (p0.isNullOrEmpty()) {
                    return
                }
                mTTNativeExpressAdList.clear()
                p0.forEach {
                    mTTNativeExpressAdList.add(DynamicData(AppConstant.Constant.ITEM_AD).apply {
                        mTTNativeExpressAd = it
                        it.render()
                    })
                }
            }
        })
    }

}

