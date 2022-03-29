package com.yang.module_picture.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdLoadType
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.yang.lib_common.base.viewmodel.BaseViewModel
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.room.entity.ImageData
import com.yang.lib_common.room.entity.ImageDataItem
import com.yang.lib_common.room.entity.ImageTypeData
import com.yang.lib_common.util.getScreenDpi
import com.yang.lib_common.util.toCloseAd
import com.yang.module_picture.R
import com.yang.module_picture.repository.PictureRepository
import javax.inject.Inject

/**
 * ClassName: MainViewModel.
 * Created by Administrator on 2021/4/1_15:15.
 * Describe:
 */
class PictureViewModel @Inject constructor(
    application: Application,
    private val pictureRepository: PictureRepository
) : BaseViewModel(application) {

    var mImageData = MutableLiveData<ImageData>()

    var mImageItemData = MutableLiveData<MutableList<ImageDataItem>>()

    var mImageTypeData = MutableLiveData<MutableList<ImageTypeData>>()

    var mTTNativeExpressAdList = mutableListOf<ImageDataItem>()

    fun getImageInfo(
        type: String = "",
        pageNum: Int,
        keyword: String = "",
        showDialog: Boolean = false
    ) {
        if (showDialog) {
            launch({
                val mutableMapOf = mutableMapOf<String, Any>()
                if (!TextUtils.isEmpty(type)) {
                    mutableMapOf[AppConstant.Constant.TYPE] = type
                }
                if (!TextUtils.isEmpty(keyword)) {
                    mutableMapOf[AppConstant.Constant.KEYWORD] = keyword
                }
                mutableMapOf[AppConstant.Constant.PAGE_NUMBER] = pageNum
                mutableMapOf[AppConstant.Constant.PAGE_SIZE] = AppConstant.Constant.PAGE_SIZE_COUNT
                pictureRepository.getImageInfo(mutableMapOf)
            }, {
                mImageData.postValue(it.data)
            }, {
                cancelRefreshLoadMore()
                showRecyclerViewErrorEvent()
            }, messages = *arrayOf(getString(R.string.string_loading)))
        } else {
            launch({
                val mutableMapOf = mutableMapOf<String, Any>()
                if (!TextUtils.isEmpty(type)) {
                    mutableMapOf[AppConstant.Constant.TYPE] = type
                }
                if (!TextUtils.isEmpty(keyword)) {
                    mutableMapOf[AppConstant.Constant.KEYWORD] = keyword
                }
                mutableMapOf[AppConstant.Constant.PAGE_NUMBER] = pageNum
                mutableMapOf[AppConstant.Constant.PAGE_SIZE] = AppConstant.Constant.PAGE_SIZE_COUNT
                pictureRepository.getImageInfo(mutableMapOf)
            }, {
                mImageData.postValue(it.data)
            }, {

                mImageData.postValue(ImageData(mutableListOf<ImageDataItem>().apply {
                    for (i in 0..10){
                        add(ImageDataItem("$i","测$i","$i","https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F26%2F20200326212002_rxlyj.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1641715864&t=fa48084bc521db8a18fe5f61842cab7e","$i","$i","","",""))
                    }
                },0,0,0,"0"))
//                cancelRefreshLoadMore()
//                showRecyclerViewErrorEvent()
            }, errorDialog = false)
        }

    }

    fun getImageItemData(sid: String) {
        launch({
            pictureRepository.getImageItemData(sid)
        }, {
            mImageItemData.postValue(it.data)
        }, errorDialog = false)
    }

    fun getImageTypeData() {
        launch({
            pictureRepository.getImageTypeData()
        }, {
            mImageTypeData.postValue(it.data)
        }, {
            mImageTypeData.postValue(mutableListOf<ImageTypeData>().apply {
                for (i in 0..5){
                    add(ImageTypeData(i,"测$i","$i",null))
                }
            })

            //requestFail()
        }, messages = *arrayOf(getString(R.string.string_loading)))
    }


    fun addViewHistory(id: String, type: String) {
        launch({
            pictureRepository.addViewHistory(id, type)
        }, {

        })
    }

    fun insertComment(params: Map<String, String>) {
        launch({
            pictureRepository.insertComment(params)
        }, {

        })
    }

    fun loadPictureAd(){
        if (toCloseAd(2)) {
            return
        }
        val adSlot = AdSlot.Builder()
            .setCodeId("947672204") //广告位id
            .setSupportDeepLink(true)
            .setAdCount(3) //请求广告数量为1到3条
            .setExpressViewAcceptedSize(getScreenDpi(getApplication())[0] / 2 - 4f, 0f) //期望模板广告view的size,单位dp
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
                    mTTNativeExpressAdList.add(ImageDataItem("10", null, null,
                        null, null, null, null, null, null).apply {
                        mItemType = AppConstant.Constant.ITEM_AD
                        mTTNativeExpressAd = it
                        it.render()
                    })
                }
            }
        })
    }

}

