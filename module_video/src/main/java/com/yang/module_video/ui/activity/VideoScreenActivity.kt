package com.yang.module_video.ui.activity

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.entity.VideoDataItem
import com.yang.lib_common.util.buildARouter
import com.yang.module_video.R
import com.yang.module_video.data.VideoScreenData
import com.yang.module_video.viewmodel.VideoViewModel
import kotlinx.android.synthetic.main.act_video_screen.*

/**
 * @Author Administrator
 * @ClassName UploadActivity
 * @Description
 * @Date 2021/11/19 11:08
 */
@Route(path = AppConstant.RoutePath.VIDEO_SCREEN_ACTIVITY)
class VideoScreenActivity : BaseActivity(), OnRefreshLoadMoreListener {

    @InjectViewModel
    lateinit var videoViewModel: VideoViewModel

    private lateinit var screenOneAdapter: ScreenAdapter

    private lateinit var screenTwoAdapter: ScreenAdapter

    private lateinit var screenThreeAdapter: ScreenAdapter

    private lateinit var mAdapter: MAdapter


    override fun getLayout(): Int {
        return R.layout.act_video_screen
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return videoViewModel.uC
    }

    override fun initData() {
        smartRefreshLayout.autoRefresh()
        initSmartRefreshLayout()
    }

    override fun initView() {
        initScreenOneRecyclerView(recyclerView_top_one)
        initScreenTwoRecyclerView(recyclerView_top_two)
        initScreenThreeRecyclerView(recyclerView_top_three)
        initRecyclerView()
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
    }

    private fun initScreenOneRecyclerView(recyclerView: RecyclerView) {
        screenOneAdapter =
            ScreenAdapter(R.layout.item_video_screen, mutableListOf<VideoScreenData>().apply {
                repeat(10) {
                    if (it == 0) {
                        add(VideoScreenData("全部", true))
                    } else {
                        add(VideoScreenData("测试条目$it"))
                    }

                }
            }).apply {
                setOnItemClickListener { adapter, view, position ->
                    val item = this.getItem(position)
                    item?.let {
                        this.data.forEach { videoScreenData ->
                            if (videoScreenData.select) {
                                videoScreenData.select = false
                            }
                        }
                        it.select = true
                        getScreenData()
                        notifyDataSetChanged()
                    }
                }
            }
        recyclerView.adapter = screenOneAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initScreenTwoRecyclerView(recyclerView: RecyclerView) {
        screenTwoAdapter =
            ScreenAdapter(R.layout.item_video_screen, mutableListOf<VideoScreenData>().apply {
                repeat(10) {
                    if (it == 0) {
                        add(VideoScreenData("全部", true))
                    } else {
                        add(VideoScreenData("测试条目$it"))
                    }

                }
            }).apply {
                setOnItemClickListener { adapter, view, position ->
                    val item = this.getItem(position)
                    item?.let {
                        this.data.forEach { videoScreenData ->
                            if (videoScreenData.select) {
                                videoScreenData.select = false
                            }
                        }
                        it.select = true
                        getScreenData()
                        notifyDataSetChanged()
                    }
                }
            }
        recyclerView.adapter = screenTwoAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initScreenThreeRecyclerView(recyclerView: RecyclerView) {
        screenThreeAdapter =
            ScreenAdapter(R.layout.item_video_screen, mutableListOf<VideoScreenData>().apply {
                repeat(10) {
                    if (it == 0) {
                        add(VideoScreenData("全部", true))
                    } else {
                        add(VideoScreenData("测试条目$it"))
                    }

                }
            }).apply {
                setOnItemClickListener { adapter, view, position ->
                    val item = this.getItem(position)
                    item?.let {
                        this.data.forEach { videoScreenData ->
                            if (videoScreenData.select) {
                                videoScreenData.select = false
                            }
                        }
                        it.select = true
                        getScreenData()
                        notifyDataSetChanged()
                    }
                }
            }
        recyclerView.adapter = screenThreeAdapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun initRecyclerView() {
        mAdapter = MAdapter(mutableListOf<VideoDataItem>().apply {
            repeat(50) {
                add(VideoDataItem().apply {
                    videoTitle = "测试$it"
                    videoUrl =
                        "https://img0.baidu.com/it/u=1051577226,2771334401&fm=26&fmt=auto"
                })
            }
        }).apply {
            setOnItemClickListener { adapter, view, position ->
                val videoDataItem = adapter.data[position] as VideoDataItem
                buildARouter(AppConstant.RoutePath.VIDEO_ITEM_ACTIVITY).withString(
                    AppConstant.Constant.ID,
                    videoDataItem.id
                ).navigation()
            }
        }
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        registerRefreshAndRecyclerView(smartRefreshLayout, mAdapter)
    }

    private fun getScreenData() {
        val content = screenOneAdapter.data.findLast {
            it.select
        }?.content
        val content1 = screenTwoAdapter.data.findLast {
            it.select
        }?.content
        val content2 = screenThreeAdapter.data.findLast {
            it.select
        }?.content
        Log.i(TAG, "getScreenData: $content $content1 $content2")
    }


    inner class ScreenAdapter(layoutResId: Int, data: MutableList<VideoScreenData>?) :
        BaseQuickAdapter<VideoScreenData, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: VideoScreenData) {
            helper.setText(R.id.tv_content, item.content)
            if (item.select) {
                helper.setBackgroundRes(R.id.tv_content, R.drawable.shape_screen_bg)
                helper.setTextColor(R.id.tv_content, ContextCompat.getColor(mContext, R.color.plum))
            } else {
                helper.setBackgroundRes(R.id.tv_content, 0)
                helper.setTextColor(
                    R.id.tv_content,
                    ContextCompat.getColor(mContext, R.color.black)
                )
            }

        }
    }

    inner class MAdapter(list: MutableList<VideoDataItem>) :
        BaseQuickAdapter<VideoDataItem, BaseViewHolder>(list) {
        init {
            mLayoutResId = R.layout.item_video
        }

        override fun convert(helper: BaseViewHolder, item: VideoDataItem) {
            val sivImg = helper.getView<ShapeableImageView>(R.id.siv_img)
            helper.setText(R.id.tv_title, item.videoTitle)
                .setText(R.id.tv_type, item.videoType)
                .setGone(R.id.item_video_recommend_type, false)
                .setGone(R.id.recyclerView, false)
            item.videoUrl?.let {
                if (it.endsWith(".mp4")) {
                    Glide.with(sivImg)
                        .setDefaultRequestOptions(RequestOptions().frame(5000))
                        .load(item.videoUrl)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(R.drawable.iv_image_error)
                        .placeholder(R.drawable.iv_image_placeholder)
                        .override(1080, 500)
                        .into(sivImg)
                } else {
                    Glide.with(sivImg)
                        .load(item.videoUrl)
                        .error(R.drawable.iv_image_error)
                        .placeholder(R.drawable.iv_image_placeholder)
                        .into(sivImg)
                }
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {

    }

}