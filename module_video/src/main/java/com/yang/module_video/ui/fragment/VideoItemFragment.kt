package com.yang.module_video.ui.fragment

import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.MBannerAdapter
import com.yang.lib_common.base.ui.fragment.BaseLazyFragment
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.data.BannerBean
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.entity.VideoDataItem
import com.yang.lib_common.util.buildARouter
import com.yang.module_video.R
import com.yang.module_video.viewmodel.VideoViewModel
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.fra_item_video.*
import javax.inject.Inject
import kotlin.random.Random


@Route(path = AppConstant.RoutePath.VIDEO_ITEM_FRAGMENT)
class VideoItemFragment : BaseLazyFragment(), OnRefreshLoadMoreListener {

    @InjectViewModel
    lateinit var videoViewModel: VideoViewModel

    @Inject
    lateinit var gson: Gson

    lateinit var mAdapter: MAdapter

    private var queryType: String? = null

    private var pageNum = 1


    override fun getLayout(): Int {
        return R.layout.fra_item_video
    }

    override fun initData() {
        queryType = arguments?.getString(AppConstant.Constant.TYPE)
        videoViewModel.getVideoInfo(queryType ?: "", pageNum)
        initSmartRefreshLayout()
    }

    override fun initView() {
        initBanner()
        initRecyclerView()
    }

    override fun initUIChangeLiveData(): UIChangeLiveData? {
        return videoViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)

        videoViewModel.mVideoData.observe(this, Observer {
            if (it.list.isNotEmpty() && videoViewModel.mBigTTNativeExpressAdList.isNotEmpty()) {
                videoViewModel.mBigTTNativeExpressAdList.forEach { adItem ->
                    it.list.add(Random.nextInt(0, it.list.size), adItem)
                }
            }
            when {
                smartRefreshLayout.isRefreshing -> {
                    smartRefreshLayout.finishRefresh()
                    if (it.list.isNullOrEmpty()) {
                        videoViewModel.showRecyclerViewEmptyEvent()
                    } else {
                        mAdapter.replaceData(it.list)

                    }
                }
                smartRefreshLayout.isLoading -> {
                    smartRefreshLayout.finishLoadMore()
                    if (it.list.isNullOrEmpty()) {
                        smartRefreshLayout.setNoMoreData(true)
                    } else {
                        smartRefreshLayout.setNoMoreData(false)
                        mAdapter.addData(it.list)
                    }
                }
                else -> {
                    if (it.list.isNullOrEmpty()) {
                        videoViewModel.showRecyclerViewEmptyEvent()
                    } else {
                        mAdapter.replaceData(it.list)
                    }
                }
            }
        })
    }


    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
    }


    private fun initRecyclerView() {
        val gridLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = gridLayoutManager
        mAdapter = MAdapter(mutableListOf()).also {
            it.setOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.item_video_big_image -> {
                        val videoDataItem = adapter.data[position] as VideoDataItem
                        buildARouter(AppConstant.RoutePath.VIDEO_ITEM_ACTIVITY).withString(
                            AppConstant.Constant.ID,
                            videoDataItem.id
                        ).navigation()
                    }
                    R.id.item_video_recommend_type -> {
                        val videoDataItem = adapter.data[position] as VideoDataItem
                        buildARouter(AppConstant.RoutePath.VIDEO_SCREEN_ACTIVITY).withString(
                            AppConstant.Constant.ID,
                            videoDataItem.id
                        ).navigation()
                    }
                }

            }
        }
        recyclerView.adapter = mAdapter

        registerRefreshAndRecyclerView(smartRefreshLayout, mAdapter)
    }


    private fun initBanner() {
        banner.addBannerLifecycleObserver(this)//添加生命周期观察者
            .setAdapter(MBannerAdapter(mutableListOf<BannerBean>().apply {
                add(BannerBean("http://10.16.242.28:20000/files/1.jpg"))
                add(BannerBean("http://10.16.242.28:20000/files/2.jpg"))
                add(BannerBean("http://10.16.242.28:20000/files/3.jpg"))
                add(BannerBean("http://10.16.242.28:20000/files/4.jpg"))
                add(BannerBean("http://10.16.242.28:20000/files/1.jpg"))
                add(BannerBean("http://10.16.242.28:20000/files/2.jpg"))
                add(BannerBean("http://10.16.242.28:20000/files/3.jpg"))
                add(BannerBean("http://10.16.242.28:20000/files/4.jpg"))
            }))
            .indicator = CircleIndicator(requireContext())
    }

    inner class MAdapter(list: MutableList<VideoDataItem>) :
        BaseMultiItemQuickAdapter<VideoDataItem, BaseViewHolder>(list) {
        init {
            addItemType(AppConstant.Constant.ITEM_CONTENT, R.layout.item_video)
            addItemType(AppConstant.Constant.ITEM_AD, R.layout.item_ad)
        }

        override fun convert(helper: BaseViewHolder, item: VideoDataItem) {
            when (item.mItemType) {
                AppConstant.Constant.ITEM_CONTENT -> {
                    val sivImg = helper.getView<ShapeableImageView>(R.id.siv_img)
                    val recyclerView = helper.getView<RecyclerView>(R.id.recyclerView)
                    helper.setText(R.id.tv_title, item.videoTitle)
                        .setText(R.id.tv_type, item.videoType)
                        .addOnClickListener(R.id.item_video_big_image)
                        .addOnClickListener(R.id.item_video_recommend_type)


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

                    initRecyclerView(recyclerView, item.smartVideoUrls ?: mutableListOf())
                }
                AppConstant.Constant.ITEM_AD -> {
                    /*广告view*/
                    val adContainer = helper.getView<FrameLayout>(R.id.adContainer)
                    adContainer.removeAllViews()
                    item.mTTNativeExpressAd?.expressAdView?.let {
                        if (it.parent == null) {
                            adContainer.addView(it)
                        }
                    }
                    item.mTTNativeExpressAd?.setDislikeCallback(requireActivity(), object :
                        TTAdDislike.DislikeInteractionCallback {
                        override fun onShow() {

                        }

                        override fun onSelected(p0: Int, p1: String?, p2: Boolean) {
                            val indexOf = mData.indexOf(item)
                            this@MAdapter.remove(indexOf)
                        }

                        override fun onCancel() {

                        }

                    })
                }
            }

        }


        private fun initRecyclerView(recyclerView: RecyclerView, data: MutableList<VideoDataItem>) {
            recyclerView.adapter = MImageAdapter(data).also {
                it.setOnItemClickListener { adapter, view, position ->
                    val videoDataItem = adapter.data[position] as VideoDataItem
                    buildARouter(AppConstant.RoutePath.VIDEO_ITEM_ACTIVITY).withString(
                        AppConstant.Constant.ID,
                        videoDataItem.id
                    ).navigation()
                }
            }
            recyclerView.layoutManager = GridLayoutManager(mContext, 2)
        }
    }

    inner class MImageAdapter(list: MutableList<VideoDataItem>) :
        BaseMultiItemQuickAdapter<VideoDataItem, BaseViewHolder>(list) {
        init {
            addItemType(AppConstant.Constant.ITEM_CONTENT, R.layout.item_video_smart_image)
            addItemType(AppConstant.Constant.ITEM_AD, R.layout.item_ad)
        }

        override fun convert(helper: BaseViewHolder, item: VideoDataItem) {
            when (item.mItemType) {
                AppConstant.Constant.ITEM_CONTENT -> {
                    helper.setText(R.id.tv_title, item.videoTitle)
                    val sivImg = helper.getView<ShapeableImageView>(R.id.siv_img)
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
                AppConstant.Constant.ITEM_AD -> {
                    /*广告view*/
                    val adContainer = helper.getView<FrameLayout>(R.id.adContainer)
                    adContainer.removeAllViews()
                    item.mTTNativeExpressAd?.expressAdView?.let {
                        if (it.parent == null) {
                            adContainer.addView(it)
                        }
                    }
                    item.mTTNativeExpressAd?.setDislikeCallback(requireActivity(), object :
                        TTAdDislike.DislikeInteractionCallback {
                        override fun onShow() {

                        }

                        override fun onSelected(p0: Int, p1: String?, p2: Boolean) {
                            val indexOf = mData.indexOf(item)
                            mData.remove(item)
                            notifyItemChanged(indexOf)
                        }

                        override fun onCancel() {

                        }

                    })
                }
            }

        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        videoViewModel.getVideoInfo(queryType ?: "", pageNum)
        videoViewModel.loadVideoAd()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNum = 1
        videoViewModel.getVideoInfo(queryType ?: "", pageNum)
        videoViewModel.loadVideoAd()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAdapter.data.filter {
            it.mItemType != AppConstant.Constant.ITEM_AD
        }.forEach {
            it.mTTNativeExpressAd?.destroy()
        }
    }
}