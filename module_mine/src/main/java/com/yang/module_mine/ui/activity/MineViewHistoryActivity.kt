package com.yang.module_mine.ui.activity

import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.module_mine.R
import com.yang.module_mine.data.MineViewHistoryData
import com.yang.module_mine.viewmodel.MineViewModel
import kotlinx.android.synthetic.main.view_normal_recyclerview.*

/**
 * @Author Administrator
 * @ClassName HistoryActivity
 * @Description 观看历史
 * @Date 2021/8/31 10:44
 */
@Route(path = AppConstant.RoutePath.MINE_VIEW_HISTORY_ACTIVITY)
class MineViewHistoryActivity : BaseActivity(), OnRefreshLoadMoreListener {

    @InjectViewModel
    lateinit var mineViewModel: MineViewModel

    private var pageNum = 1

    private lateinit var mAdapter: MAdapter
    override fun getLayout(): Int {
        return R.layout.act_view_history
    }

    override fun initData() {
        initSmartRefreshLayout()
        smartRefreshLayout.autoRefresh()
        mineViewModel.mViewHistoryListLiveData.observe(this, Observer {
            when {
                smartRefreshLayout.isRefreshing -> {
                    smartRefreshLayout.finishRefresh()
                    if (it.size == 0) {
                        mineViewModel.showRecyclerViewEmptyEvent()
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
                    if (it.size == 0) {
                        mineViewModel.showRecyclerViewEmptyEvent()
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

    override fun initUIChangeLiveData(): UIChangeLiveData? {
        return mineViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = MAdapter(R.layout.item_view_history, null).apply {
            setOnItemClickListener { adapter, view, position ->
                val item = mAdapter.getItem(position)
                item?.let {
                    if (it.type == "1") {
                        buildARouter(AppConstant.RoutePath.PICTURE_ITEM_ACTIVITY)
                            .withString(AppConstant.Constant.ID, it.id)
                            .navigation()
                    } else {
                        buildARouter(AppConstant.RoutePath.VIDEO_ITEM_ACTIVITY)
                            .withString(AppConstant.Constant.URL, it.filePath)
                            .navigation()
                    }
                }

            }
        }
        recyclerView.adapter = mAdapter
        registerRefreshAndRecyclerView(smartRefreshLayout,mAdapter)
    }

    inner class MAdapter(layoutResId: Int, list: MutableList<MineViewHistoryData>?) :
        BaseQuickAdapter<MineViewHistoryData, BaseViewHolder>(layoutResId, list) {
        override fun convert(helper: BaseViewHolder, item: MineViewHistoryData) {
            val ivImage = helper.getView<ImageView>(R.id.iv_image)
            Glide.with(ivImage).load(item.filePath)
                .error(R.drawable.iv_image_error)
                .placeholder(R.drawable.iv_image_placeholder).into(ivImage)
            if (item.type == "2") {
                helper.setText(R.id.tv_type, "#视频")
            } else {
                helper.setText(R.id.tv_type, "#图片")
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mineViewModel.queryViewHistory()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        mineViewModel.queryViewHistory()
    }
}