package com.yang.module_main.ui.main.fragment

import android.util.Log
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bytedance.sdk.openadsdk.TTAdDislike
import com.bytedance.sdk.openadsdk.TTAdNative
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.fragment.BaseLazyFragment
import com.yang.lib_common.bus.event.LiveDataBus
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.dialog.ImageViewPagerDialog
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.symbolToList
import com.yang.lib_common.widget.GridNinePictureView
import com.yang.module_main.R
import com.yang.module_main.data.model.DynamicData
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.view_normal_recyclerview.*
import javax.inject.Inject
import kotlin.random.Random


@Route(path = AppConstant.RoutePath.MAIN_RIGHT_FRAGMENT)
class MainRightFragment : BaseLazyFragment(), OnRefreshLoadMoreListener {

    @Inject
    lateinit var gson: Gson

    @InjectViewModel
    lateinit var mainViewModel: MainViewModel

    private lateinit var mAdapter: MAdapter

    private var mutableListOf: MutableList<DynamicData> = mutableListOf()

    private var pageNum = 1

    private var mTTAdNative: TTAdNative? = null


    override fun getLayout(): Int {
        return R.layout.fra_main_right
    }

    override fun initData() {
        smartRefreshLayout.autoRefresh()
        initSmartRefreshLayout()

        LiveDataBus.instance.with("refresh_dynamic").observe(this, Observer {

            onRefresh(smartRefreshLayout)
        })

        mainViewModel.dynamicListLiveData.observe(this, Observer {
            if (it.isNotEmpty() && mainViewModel.mTTNativeExpressAdList.isNotEmpty()){
                mainViewModel.mTTNativeExpressAdList.forEach { adItem ->
                    it.add(Random.nextInt(0,it.size),adItem)
                }
            }
            when {
                smartRefreshLayout.isRefreshing -> {
                    smartRefreshLayout.finishRefresh()
                    if (it.isNullOrEmpty()) {
                        mainViewModel.showRecyclerViewEmptyEvent()
                    } else {
                        mAdapter.replaceData(it)

                    }
                }
                smartRefreshLayout.isLoading -> {
                    smartRefreshLayout.finishLoadMore()
                    if (it.isNullOrEmpty()) {
                        smartRefreshLayout.setNoMoreData(true)
                    } else {
                        smartRefreshLayout.setNoMoreData(false)
                        mAdapter.addData(it)
                    }
                }
                else -> {
                    if (it.isNullOrEmpty()) {
                        mainViewModel.showRecyclerViewEmptyEvent()
                    } else {
                        mAdapter.replaceData(it)
                    }
                }
            }
        })
    }

    override fun initView() {
        initRecyclerView()
    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
    }

    override fun initUIChangeLiveData(): UIChangeLiveData? {
        return mainViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }



    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = MAdapter(mutableListOf).apply {
            setOnItemChildClickListener { _, view, _ ->
                when (view.id) {
                    R.id.siv_img -> {
                        buildARouter(AppConstant.RoutePath.MINE_OTHER_PERSON_INFO_ACTIVITY).withString(
                            AppConstant.Constant.ID,
                            ""
                        ).navigation()
                    }
                }
            }

            setOnItemClickListener { _, _, position ->
                val item = mAdapter.getItem(position)
                buildARouter(AppConstant.RoutePath.DYNAMIC_DETAIL_ACTIVITY)
                    .withString(AppConstant.Constant.ID, item?.id)
                    .navigation()
            }
        }
        recyclerView.setOnClickListener {
            Log.i(TAG, "initRecyclerView: ")
        }
        recyclerView.adapter = mAdapter
        registerRefreshAndRecyclerView(smartRefreshLayout, mAdapter)
    }

    inner class MAdapter(list: MutableList<DynamicData>) :
        BaseMultiItemQuickAdapter<DynamicData, BaseViewHolder>(list) {

        init {
            addItemType(AppConstant.Constant.ITEM_CONTENT, R.layout.item_main_right)
            addItemType(AppConstant.Constant.ITEM_AD, R.layout.item_ad)
        }

        override fun convert(helper: BaseViewHolder, item: DynamicData) {
            when (item.mItemType) {
                AppConstant.Constant.ITEM_CONTENT -> {
                    if (item.imageUrls.isNullOrEmpty()) {
                        helper.setGone(R.id.gridNinePictureView, false)
                    } else {
                        helper.setGone(R.id.gridNinePictureView, true)
                        initItemMainContentImage(helper, item)
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
                            this@MAdapter.remove(indexOf)
                        }

                        override fun onCancel() {

                        }

                    })
                }
            }

        }


        private fun initItemMainContentImage(helper: BaseViewHolder, item: DynamicData) {

            val gridNinePictureView = helper.getView<GridNinePictureView>(R.id.gridNinePictureView)
            gridNinePictureView.data = item.imageUrls?.symbolToList("#")!!
            gridNinePictureView.imageCallback = object : GridNinePictureView.ImageCallback {
                override fun imageClickListener(position: Int) {
                    val imageViewPagerDialog =
                        ImageViewPagerDialog(
                            requireContext(),
                            item.imageUrls?.symbolToList("#")!!,
                            position,
                            true
                        )
                    XPopup.Builder(requireContext()).asCustom(imageViewPagerDialog).show()
                }

            }
        }

    }

    private fun getDynamicList() {
        val mutableMapOf = mutableMapOf<String, String>()
        mutableMapOf[AppConstant.Constant.PAGE_NUMBER] = pageNum.toString()
        mutableMapOf[AppConstant.Constant.PAGE_SIZE] =
            AppConstant.Constant.PAGE_SIZE_COUNT.toString()
        mainViewModel.getDynamicList(mutableMapOf)
    }


    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        getDynamicList()
        mainViewModel.loadMainAd()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNum = 1
        getDynamicList()
        mainViewModel.loadMainAd()
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