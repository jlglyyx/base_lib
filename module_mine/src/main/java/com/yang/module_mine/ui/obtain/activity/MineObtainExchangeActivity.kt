package com.yang.module_mine.ui.obtain.activity

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.widget.CommonToolBar
import com.yang.module_mine.R
import com.yang.module_mine.adapter.MineObtainExchangeAdapter
import com.yang.module_mine.viewmodel.MineViewModel
import kotlinx.android.synthetic.main.act_mine_obtain_exchange.*
import kotlinx.android.synthetic.main.view_normal_recyclerview.recyclerView
import kotlinx.android.synthetic.main.view_normal_recyclerview.smartRefreshLayout

/**
 * @Author Administrator
 * @ClassName MineObtainExchangeActivity
 * @Description 我的积分兑换
 * @Date 2021/9/13 17:16
 */
@Route(path = AppConstant.RoutePath.MINE_OBTAIN_EXCHANGE_ACTIVITY)
class MineObtainExchangeActivity : BaseActivity(), OnRefreshLoadMoreListener {

    @InjectViewModel
    lateinit var mineViewModel: MineViewModel

    private var pageNum = 1

    private lateinit var mAdapter: MineObtainExchangeAdapter

    override fun getLayout(): Int {
        return R.layout.act_mine_obtain_exchange
    }

    override fun initData() {

    }

    override fun initView() {
        initSmartRefreshLayout()
        initRecyclerView()
        commonToolBar.tVRightCallBack = object : CommonToolBar.TVRightCallBack {
            override fun tvRightClickListener() {
                buildARouter(AppConstant.RoutePath.MINE_EXCHANGE_ACTIVITY).navigation()
            }
        }
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mineViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
        smartRefreshLayout.autoRefresh()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mAdapter = MineObtainExchangeAdapter(null)
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item = mAdapter.getItem(position)
            buildARouter(AppConstant.RoutePath.MINE_GOODS_DETAIL_ACTIVITY).withString(AppConstant.Constant.ID,item?.id).navigation()
        }
        recyclerView.adapter = mAdapter
        mineViewModel.mMineGoodsDetailListLiveData.observe(this, Observer {
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

        registerRefreshAndRecyclerView(smartRefreshLayout, mAdapter)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNum = 1
        mineViewModel.queryGoodsList(0,pageNum)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        mineViewModel.queryGoodsList(0,pageNum)
    }
}