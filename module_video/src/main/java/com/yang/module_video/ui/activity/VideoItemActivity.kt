package com.yang.module_video.ui.activity

import android.content.res.Configuration
import android.graphics.Rect
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bytedance.sdk.openadsdk.TTRewardVideoAd
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.tabs.TabLayout
import com.lxj.xpopup.XPopup
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.CommentAdapter
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.data.CommentData
import com.yang.lib_common.dialog.EditBottomDialog
import com.yang.lib_common.down.thread.MultiMoreThreadDownload
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.entity.VideoDataItem
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.toCloseAd
import com.yang.module_video.R
import com.yang.module_video.viewmodel.VideoViewModel
import kotlinx.android.synthetic.main.act_video_item.*

@Route(path = AppConstant.RoutePath.VIDEO_ITEM_ACTIVITY)
class VideoItemActivity : BaseActivity() {

    @InjectViewModel
    lateinit var videoViewModel: VideoViewModel

    private lateinit var orientationUtils: OrientationUtils
    private lateinit var commentAdapter: CommentAdapter

    private lateinit var collectionAdapter: CollectionAdapter

    private var url = ""

    private var isPause = false
    private var isPlay = false

    private var isScroll = false

    val paramMap = mutableMapOf<String, String>()

    override fun getLayout(): Int {
        return R.layout.act_video_item
    }

    override fun initData() {
        val intent = intent
        val id = intent.getStringExtra(AppConstant.Constant.ID)
        val url = intent.getStringExtra(AppConstant.Constant.URL)
        id?.let {
            videoViewModel.getVideoItemData(it)
        }
        url?.let {
            this.url = it
            initVideo()
        }
        insertViewHistory()
    }

    override fun initView() {

        initRecyclerView()
        initTabLayout()

        //外部辅助的旋转，帮助全屏
        orientationUtils = OrientationUtils(this, detailPlayer)
        //初始化不打开外部的旋转
        orientationUtils.isEnable = false
        detailPlayer.fullscreenButton.setOnClickListener {
            orientationUtils.resolveByClick()
            detailPlayer.startWindowFullscreen(this, true, true)
        }

        detailPlayer.backButton.setOnClickListener {
            finish()
        }

        tv_send_comment.clicks().subscribe {
            XPopup.Builder(this).autoOpenSoftInput(true).asCustom(EditBottomDialog(this).apply {
                dialogCallBack = object : EditBottomDialog.DialogCallBack {
                    override fun getComment(s: String) {
                        commentAdapter.addData(0, CommentData(0, 0).apply {
                            comment = s
                        })
                        insertComment(s)
                        commentAdapter.getViewByPosition(
                            recyclerView,
                            0,
                            com.yang.lib_common.R.id.siv_img
                        )?.let { it1 ->
                            it1.isFocusable = true
                            it1.requestFocus()
                            scrollToPosition(it1)
                        }
                        nestedScrollView.fullScroll(View.FOCUS_DOWN)
                    }

                }
            }).show()
        }
        tv_video_down.clicks().subscribe {
            if (toCloseAd(3)) {
                MultiMoreThreadDownload.Builder(this)
                    .parentFilePath("${Environment.getExternalStorageDirectory()}/MFiles/video")
                    .filePath("${System.currentTimeMillis()}.mp4")
                    .fileUrl(url)
                    .threadNum(50)
                    .build()
                    .start()
                return@subscribe
            }
            videoViewModel.loadVideoDownAd()
        }


    }

    private fun scrollToPosition(view: View) {
        val intArray = IntArray(2)
        view.getLocationOnScreen(intArray)
        nestedScrollView.scrollTo(intArray[0], intArray[1])
    }

    private fun insertComment(comment: String) {
        paramMap.clear()
        paramMap[AppConstant.Constant.COMMENT] = comment
        videoViewModel.insertComment(paramMap)
    }

    private fun insertViewHistory() {
        videoViewModel.insertViewHistory("", "")
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
        videoViewModel.mVideoItemData.observe(this, Observer {
            if (it.size == 0) {
                return@Observer
            }
            for ((index, videoDataItem) in it.withIndex()) {
                if (index == 0) {
                    videoDataItem.select = true
                }
                videoDataItem.position = index + 1
            }
            url = it[0].videoUrl.toString()
            initVideo()
            collectionAdapter.replaceData(it)
        })

        videoViewModel.mTTRewardVideoDownAd.observe(this, Observer {

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
                    MultiMoreThreadDownload.Builder(this@VideoItemActivity)
                        .parentFilePath("${Environment.getExternalStorageDirectory()}/MFiles/video")
                        .filePath("${System.currentTimeMillis()}.mp4")
                        .fileUrl(url)
                        .threadNum(50)
                        .build()
                        .start()
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
            it.showRewardVideoAd(this@VideoItemActivity)
        })
    }

    private fun initRecyclerView() {

        recyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(mutableListOf()).apply {
            setOnItemChildClickListener { _, view, position ->
                val item = commentAdapter.getItem(position)
                item?.let {
                    when (view.id) {
                        com.yang.lib_common.R.id.siv_img -> {
                            buildARouter(AppConstant.RoutePath.MINE_OTHER_PERSON_INFO_ACTIVITY).withString(
                                AppConstant.Constant.ID,
                                ""
                            ).navigation()
                        }
                        com.yang.lib_common.R.id.siv_reply_img -> {
                            buildARouter(AppConstant.RoutePath.MINE_OTHER_PERSON_INFO_ACTIVITY).withString(
                                AppConstant.Constant.ID,
                                ""
                            ).navigation()
                        }
                        com.yang.lib_common.R.id.tv_reply -> {
                            XPopup.Builder(this@VideoItemActivity)
                                .autoOpenSoftInput(true)
                                .asCustom(EditBottomDialog(this@VideoItemActivity).apply {
                                    dialogCallBack = object : EditBottomDialog.DialogCallBack {
                                        override fun getComment(s: String) {
                                            when (it.itemType) {
                                                0 -> {
                                                    it.addSubItem(CommentData(1, 1).apply {
                                                        comment = s
                                                        parentId = it.id

                                                    })
                                                    commentAdapter.collapse(position)
                                                    commentAdapter.expand(position)
                                                }
                                                1, 2 -> {
                                                    it.parentId?.let { mParentId ->
                                                        val mPosition = commentAdapter.data.indexOf(
                                                            commentAdapter.data.findLast {
                                                                TextUtils.equals(it.parentId,
                                                                    mParentId)
                                                            }?.apply {
                                                                addSubItem(CommentData(1, 2).apply {
                                                                    comment = s
                                                                    parentId = mParentId
                                                                })
                                                            })
                                                        commentAdapter.collapse(mPosition)
                                                        commentAdapter.expand(mPosition)
                                                    }
                                                }
                                            }
                                            insertComment(s)
                                        }
                                    }
                                }).show()
                        }
                        else -> {

                        }
                    }
                }

            }
        }
        recyclerView.adapter = commentAdapter

        collectionRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL,
            false)
        collectionAdapter =
            CollectionAdapter(R.layout.item_video_collection, mutableListOf()).apply {
                setOnItemClickListener { adapter, view, position ->
                    val item = adapter.getItem(position) as VideoDataItem
                    url = item.videoUrl.toString()
                    if (item.select) {
                        item.select = false
                        adapter.notifyItemChanged(position)
                    } else {
                        adapter.data.forEach {
                            if ((it as VideoDataItem).select) {
                                it.select = false
                            }
                        }
                        item.select = true
                        adapter.notifyDataSetChanged()
                    }
                    initVideo()
                    detailPlayer.startPlayLogic()
                }
            }
        collectionRecyclerView.adapter = collectionAdapter
        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            val rect = Rect()
            cl_video.getHitRect(rect)
            isScroll = true
            if (cl_video.getLocalVisibleRect(rect)) {
                tabLayout.getTabAt(0)?.select()
            } else {
                tabLayout.getTabAt(1)?.select()
            }
        }


    }

    private fun initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().apply {
            view.setOnClickListener {
                isScroll = false
            }
        }.setText("视频"))
        tabLayout.addTab(tabLayout.newTab().apply {
            view.setOnClickListener {
                isScroll = false
            }
        }.setText("评论"))


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (!isScroll) {
                    if (tab?.position == 0) {
                        nestedScrollView.fullScroll(View.FOCUS_UP)
                    } else {
                        cl_video.post {
                            nestedScrollView.scrollY = cl_video.height
                        }
                    }
                }

            }

        })
    }


    private fun initVideo() {

        val gsyVideoOption = GSYVideoOptionBuilder()
        gsyVideoOption
//        gsyVideoOption.setThumbImageView(imageView)
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setUrl(url)
            .setCacheWithPlay(false)
            .setVideoTitle("测试视频")
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    //开始播放了才能旋转和全屏
                    orientationUtils.isEnable = true
                    isPlay = true
                }

                override fun onQuitFullscreen(
                    url: String,
                    vararg objects: Any
                ) {
                    super.onQuitFullscreen(url, *objects)
                    orientationUtils.backToProtVideo()
                }
            }).setLockClickListener { view, lock ->
                orientationUtils.isEnable = !lock
            }.build(detailPlayer)


    }


    inner class CollectionAdapter(layoutResId: Int, data: MutableList<VideoDataItem>) :
        BaseQuickAdapter<VideoDataItem, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: VideoDataItem) {
            helper.setText(R.id.bt_collection, item.position.toString())
            if (item.select) {
                helper.setTextColor(R.id.bt_collection,
                    ContextCompat.getColor(mContext, R.color.mediumslateblue))
            } else {
                helper.setTextColor(R.id.bt_collection, ContextCompat.getColor(mContext,
                    R.color.black))
            }
        }

    }


    override fun onBackPressed() {
        orientationUtils.backToProtVideo()
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }


    override fun onPause() {
        detailPlayer.currentPlayer.onVideoPause()
        super.onPause()
        isPause = true
    }

    override fun onResume() {
        detailPlayer.currentPlayer.onVideoResume(false)
        super.onResume()
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            detailPlayer.currentPlayer.release()
        }
        orientationUtils.releaseListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            detailPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true)
        }
    }
}