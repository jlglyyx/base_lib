package com.yang.module_mine.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.bytedance.sdk.openadsdk.*
import com.yang.lib_common.base.viewmodel.BaseViewModel
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.room.BaseAppDatabase
import com.yang.lib_common.room.entity.MineGoodsDetailData
import com.yang.lib_common.room.entity.UserInfoData
import com.yang.lib_common.util.buildARouter
import com.yang.module_mine.R
import com.yang.module_mine.data.MineExtensionTurnoverData
import com.yang.module_mine.data.MineObtainTurnoverData
import com.yang.module_mine.data.MineSignTurnoverData
import com.yang.module_mine.data.MineViewHistoryData
import com.yang.module_mine.repository.MineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.net.URLEncoder
import javax.inject.Inject

/**
 * ClassName: LoginViewModel.
 * Created by Administrator on 2021/4/1_15:15.
 * Describe:
 */
class MineViewModel @Inject constructor(
    application: Application,
    private val mineRepository: MineRepository
) : BaseViewModel(application) {

    var mUserInfoData = MutableLiveData<UserInfoData>()

    var pictureListLiveData = MutableLiveData<MutableList<String>>()

    var mViewHistoryListLiveData = MutableLiveData<MutableList<MineViewHistoryData>>()

    var mMineExtensionTurnoverListLiveData = MutableLiveData<MutableList<MineExtensionTurnoverData>>()

    var mMineObtainTurnoverListLiveData = MutableLiveData<MutableList<MineObtainTurnoverData>>()

    var mMineSignTurnoverListLiveData = MutableLiveData<MutableList<MineSignTurnoverData>>()

    var mMineGoodsDetailListLiveData = MutableLiveData<MutableList<MineGoodsDetailData>>()

    var mTTRewardMineTaskAd = MutableLiveData<TTRewardVideoAd>()


    fun login(userAccount: String, password: String) {
        launch({
            mineRepository.login(userAccount, password)
        }, {
            mUserInfoData.postValue(it.data)
        }, {
            buildARouter(AppConstant.RoutePath.LOGIN_ACTIVITY).navigation()
        }, messages = *arrayOf(getString(R.string.string_requesting)))
    }

    fun register(userInfoData: UserInfoData) {
        launch({
            mineRepository.register(userInfoData)
        }, {
            mUserInfoData.postValue(it.data)
        }, messages = *arrayOf(getString(R.string.string_requesting)))
    }

    fun uploadFile(filePaths: MutableList<String>) {
        launch({
            val mutableMapOf = mutableMapOf<String, RequestBody>()
            filePaths.forEach {
                val file = File(it)
                val requestBody = RequestBody.create(MediaType.parse(AppConstant.ClientInfo.CONTENT_TYPE), file)
                val encode = URLEncoder.encode("${System.currentTimeMillis()}_${file.name}", AppConstant.ClientInfo.UTF_8)
                mutableMapOf["file\";filename=\"$encode"] = requestBody
            }
            mineRepository.uploadFile(mutableMapOf)
        }, {
            pictureListLiveData.postValue(it.data)
        }, messages = *arrayOf(getString(R.string.string_uploading), getString(R.string.string_insert_success)))
    }

    fun changePassword(password: String) {
        launch({
            mineRepository.changePassword(password)
        }, {
            requestSuccess()
        }, messages = *arrayOf(getString(R.string.string_change_password_ing), getString(R.string.string_change_password_success), getString(R.string.string_change_password_fail)))
    }

    fun changeUserInfo(userInfoData: UserInfoData) {
        launch({
            mineRepository.changeUserInfo(userInfoData)
        }, {
            mUserInfoData.postValue(it.data)
        }, messages = *arrayOf(getString(R.string.string_requesting)))
    }

    fun queryViewHistory() {
        launch({
            mineRepository.queryViewHistory()
        }, {
            mViewHistoryListLiveData.postValue(it.data)
        }, {
            cancelRefreshLoadMore()
            showRecyclerViewErrorEvent()
        }, errorDialog = false)
    }

    fun queryObtainTurnover(pageNum: Int) {
        launch({
            mineRepository.queryObtainTurnover(pageNum)
        }, {
            mMineObtainTurnoverListLiveData.postValue(it.data)
        }, {
            cancelRefreshLoadMore()
            showRecyclerViewErrorEvent()
        }, errorDialog = false)
    }

    fun querySignTurnover() {
        launch({
            mineRepository.querySignTurnover()
        }, {
            mMineSignTurnoverListLiveData.postValue(it.data)
        }, {
            cancelRefreshLoadMore()
            showRecyclerViewErrorEvent()
        }, errorDialog = false)
    }

    fun queryExtensionTurnover() {
        launch({
            mineRepository.queryExtensionTurnover()
        }, {
            mMineExtensionTurnoverListLiveData.postValue(it.data)
        }, {
            cancelRefreshLoadMore()
            showRecyclerViewErrorEvent()
        }, errorDialog = false)
    }


    fun queryGoodsList(type: Int, pageNum: Int) {
        launch({
            mineRepository.queryGoodsList()
        }, {
            mMineGoodsDetailListLiveData.postValue(it.data)
        }, {
            withContext(Dispatchers.IO) {
                if (type == 0) {
                    mMineGoodsDetailListLiveData.postValue(
                        BaseAppDatabase.instance.mineGoodsDetailDao().queryData()
                    )
                } else {
                    mMineGoodsDetailListLiveData.postValue(
                        BaseAppDatabase.instance.mineGoodsDetailDao()
                            .queryDataByType(type, pageNum, AppConstant.Constant.PAGE_SIZE_COUNT)
                    )
                }

            }
        }, errorDialog = false)
    }

    fun createGoods() {
        launch({
            mineRepository.createGoods()
        }, {

        }, {

        }, messages = *arrayOf("开始创建"))
    }

    fun exchangeGoods() {
        launch({
            mineRepository.exchangeGoods()
        }, {

        }, {

        }, messages = *arrayOf("开始兑换"))
    }



    fun loadMineTaskAd(){
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
                mTTRewardMineTaskAd.postValue(ad)
            }

        })
    }
}

