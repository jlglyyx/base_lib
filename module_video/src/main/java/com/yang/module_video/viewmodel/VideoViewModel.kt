package com.yang.module_video.viewmodel

import android.app.Application
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bytedance.sdk.openadsdk.*
import com.yang.lib_common.base.viewmodel.BaseViewModel
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.room.BaseAppDatabase
import com.yang.lib_common.room.entity.VideoData
import com.yang.lib_common.room.entity.VideoDataItem
import com.yang.lib_common.room.entity.VideoTypeData
import com.yang.lib_common.util.toCloseAd
import com.yang.module_video.R
import com.yang.module_video.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ClassName: MainViewModel.
 * Created by Administrator on 2021/4/1_15:15.
 * Describe:
 */
class VideoViewModel @Inject constructor(
    application: Application,
    private val videoRepository: VideoRepository
) : BaseViewModel(application) {


    var mVideoData = MutableLiveData<VideoData>()

    var mVideoItemData = MutableLiveData<MutableList<VideoDataItem>>()

    var mVideoTypeData = MutableLiveData<MutableList<VideoTypeData>>()

    var mBigTTNativeExpressAdList = mutableListOf<VideoDataItem>()

    var mTTRewardVideoDownAd = MutableLiveData<TTRewardVideoAd>()


    fun getVideoInfo(
        type: String = "",
        pageNum: Int,
        keyword: String = "",
        showDialog: Boolean = false
    ) {
        if (showDialog) {
            launch({
                val mutableMapOf = mutableMapOf<String, Any>()
                mutableMapOf[AppConstant.Constant.TYPE] = type
                mutableMapOf[AppConstant.Constant.KEYWORD] = keyword
                mutableMapOf[AppConstant.Constant.PAGE_NUMBER] = pageNum
                mutableMapOf[AppConstant.Constant.PAGE_SIZE] = AppConstant.Constant.PAGE_SIZE_COUNT
                videoRepository.getVideoInfo(mutableMapOf)
            }, {
                mVideoData.postValue(it.data)
            },{
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
                videoRepository.getVideoInfo(mutableMapOf)
            }, {
                mVideoData.postValue(it.data)
            },{

                mVideoData.postValue(VideoData(
                    mutableListOf<VideoDataItem>().apply {
                    for (count in 0..3){
                    add(VideoDataItem().apply {
                        id = "$count"
                        videoTitle = "$count"
                        videoUrl = "https://img2.baidu.com/it/u=3583098839,704145971&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=889"
                        videoType = "$count"
                        smartVideoUrls = mutableListOf<VideoDataItem>().apply {
                            add(VideoDataItem().apply {
                                id = "$count"
                                videoTitle = "$count"
                                videoUrl = "https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg"
                                videoType = "$count"
                            })
                            add(VideoDataItem().apply {
                                id = "$count"
                                videoTitle = "$count"
                                videoUrl = "https://img1.baidu.com/it/u=1834859148,419625166&fm=26&fmt=auto&gp=0.jpg"
                                videoType = "$count"
                            })
                            add(VideoDataItem().apply {
                                id = "$count"
                                videoTitle = "$count"
                                videoUrl = "https://img2.baidu.com/it/u=3583098839,704145971&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=889"
                                videoType = "$count"
                            })
                        }
                    })
                    }
                },null,null,null,null))

//                cancelRefreshLoadMore()
//                showRecyclerViewErrorEvent()
            }, errorDialog = false)
        }

    }

    fun getVideoItemData(sid: String) {
        launch({
            videoRepository.getVideoItemData(sid)
        }, {
            mVideoItemData.postValue(it.data)
        }, {
            withContext(Dispatchers.IO) {
                if (BaseAppDatabase.instance.videoDataDao().queryData().size != 0) {
                    mVideoItemData.postValue(
                        BaseAppDatabase.instance.videoDataDao().queryDataBySid(sid)
                    )
                }
            }
        }, errorDialog = false)
    }


    fun getVideoTypeData() {
        launch({
            videoRepository.getVideoTypeData()
        }, {
            mVideoTypeData.postValue(it.data)
        }, {
            mVideoTypeData.postValue(mutableListOf<VideoTypeData>().apply {
                for (i in 0..5){
                    add(VideoTypeData(i,"测$i","$i",null))
                }
            })
            //requestFail()
        }, messages = *arrayOf(getString(R.string.string_loading)))
    }


    fun insertViewHistory(id: String, type: String) {
        launch({
            videoRepository.insertViewHistory(id, type)
        }, {

        })
    }

    fun insertComment(params: Map<String, String>) {
        launch({
            videoRepository.insertComment(params)
        }, {

        })
    }


    fun loadVideoAd(){
        if (toCloseAd(2)) {
            return
        }
        val bigAdSlot = AdSlot.Builder()
            .setCodeId("947667043") //广告位id
            .setSupportDeepLink(true)
            .setAdCount(3) //请求广告数量为1到3条
            .setExpressViewAcceptedSize(0f, 0f) //期望模板广告view的size,单位dp
            .setAdLoadType(TTAdLoadType.PRELOAD) //推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
            .build()
        mTTAdNative?.loadNativeExpressAd(bigAdSlot, object : TTAdNative.NativeExpressAdListener {
            override fun onError(p0: Int, p1: String?) {
                Log.i("TAG", "onError: $p0  $p1")
            }

            override fun onNativeExpressAdLoad(p0: MutableList<TTNativeExpressAd>?) {
                Log.i("TAG", "onNativeExpressAdLoad: ${p0?.size}")
                if (p0.isNullOrEmpty()) {
                    return
                }
                mBigTTNativeExpressAdList.clear()
                p0.forEach {
                    mBigTTNativeExpressAdList.add(VideoDataItem().apply {
                        mItemType = AppConstant.Constant.ITEM_AD
                        mTTNativeExpressAd = it
                        it.render()
                    })
                }
            }
        })
    }
    fun loadVideoDownAd(){
        val bigAdSlot = AdSlot.Builder()
            .setCodeId("947676680") //广告位id
            .setUserID("tag123")//tag_id
            .setMediaExtra("media_extra") //附加参数
            .setOrientation(TTAdConstant.VERTICAL)
            .setExpressViewAcceptedSize(0f, 0f) //期望模板广告view的size,单位dp
            .setAdLoadType(TTAdLoadType.PRELOAD) //推荐使用，用于标注此次的广告请求用途为预加载（当做缓存）还是实时加载，方便后续为开发者优化相关策略
            .build()
        mTTAdNative?.loadRewardVideoAd(bigAdSlot, object : TTAdNative.RewardVideoAdListener {
            override fun onError(code: Int, message: String?) {
                Log.i("TAG", "onError: $code  $message")
            }


            override fun onRewardVideoAdLoad(p0: TTRewardVideoAd?) {
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            override fun onRewardVideoCached() {

            }

            override fun onRewardVideoCached(ad: TTRewardVideoAd?) {
                mTTRewardVideoDownAd.postValue(ad)
            }

        })
    }
}

