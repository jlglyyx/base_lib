package com.yang.module_mine.ui.obtain.activity

import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.module_mine.R
import com.yang.module_mine.adapter.MineObtainTaskAdapter
import com.yang.module_mine.data.MineEarnObtainData
import com.yang.module_mine.viewmodel.MineViewModel
import kotlinx.android.synthetic.main.act_mine_earn_obtain.*

/**
 * @Author Administrator
 * @ClassName MineEarnObtainActivity
 * @Description
 * @Date 2021/9/29 16:13
 */
@Route(path = AppConstant.RoutePath.MINE_OBTAIN_TASK_ACTIVITY)
class MineObtainTaskActivity : BaseActivity() {

    @InjectViewModel
    lateinit var mineViewModel: MineViewModel

    lateinit var mTaskAdapter: MineObtainTaskAdapter

    private var currentPosition = -1

    override fun getLayout(): Int {
        return R.layout.act_mine_earn_obtain
    }

    override fun initData() {
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)

        mineViewModel.mTTRewardMineTaskAd.observe(this, Observer {

            //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
            //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

            it?.setRewardAdInteractionListener(object :
                TTRewardVideoAd.RewardAdInteractionListener {
                override fun onAdShow() {

                    Log.i(TAG, "onAdShow: ")
                }

                override fun onAdVideoBarClick() {

                    Log.i(TAG, "onAdVideoBarClick: ")
                }

                override fun onAdClose() {
                    Log.i(TAG, "onAdClose: ")
                    mTaskAdapter.getItem(currentPosition)?.apply {
                        ++currentTask
                        mTaskAdapter.setData(currentPosition, this)
                    }
                }

                override fun onVideoComplete() {
                    Log.i(TAG, "onVideoComplete: ")
                }

                override fun onVideoError() {

                    Log.i(TAG, "onVideoError: ")
                }

                override fun onRewardVerify(
                    p0: Boolean,
                    p1: Int,
                    p2: String?,
                    p3: Int,
                    p4: String?
                ) {

                    Log.i(TAG, "onRewardVerify: ")
                }

                override fun onSkippedVideo() {
                    Log.i(TAG, "onSkippedVideo: ")

                }

            })

            //展示广告，并传入广告展示的场景
            it.showRewardVideoAd(this@MineObtainTaskActivity)
        })
    }

    private fun initRecyclerView() {
        val mutableListOf = mutableListOf<MineEarnObtainData>()
        mutableListOf.add(MineEarnObtainData("每日签到+30", 1, 1))
        mutableListOf.add(MineEarnObtainData("限时推广+100", 100, 1))
        mutableListOf.add(MineEarnObtainData("观看广告+60", 3, 0))
        mTaskAdapter = MineObtainTaskAdapter(R.layout.item_mine_earn_obtain, mutableListOf)
        recyclerView.adapter = mTaskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        mTaskAdapter.setOnItemChildClickListener { adapter, view, position ->
            currentPosition = position
            val item = mTaskAdapter.getItem(position)
            item?.let {
                when (view.id) {
                    R.id.tv_finish -> {
                        if (it.currentTask < it.countTask) {
                            mineViewModel.loadMineTaskAd()
                        }
                    }
                }
            }
        }
    }
}