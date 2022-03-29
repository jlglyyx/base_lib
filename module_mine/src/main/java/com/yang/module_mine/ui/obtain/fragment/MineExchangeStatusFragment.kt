package com.yang.module_mine.ui.obtain.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.fragment.BaseLazyFragment
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.module_mine.R
import com.yang.module_mine.ui.obtain.adapter.MineExchangeStatusAdapter
import com.yang.module_mine.viewmodel.MineViewModel
import kotlinx.android.synthetic.main.view_normal_recyclerview.*

/**
 * @Author Administrator
 * @ClassName MineExchangeStatusFragment
 * @Description
 * @Date 2021/9/14 10:39
 */
@Route(path = AppConstant.RoutePath.MINE_EXCHANGE_STATUS_FRAGMENT)
class MineExchangeStatusFragment :BaseLazyFragment(), OnRefreshLoadMoreListener {

    @InjectViewModel
    lateinit var mineViewModel: MineViewModel

    private var pageNum = 1

    private lateinit var mAdapter: MineExchangeStatusAdapter

    var type:Int = -1

    override fun getLayout(): Int {
        return R.layout.act_mine_exchange_status
    }

    override fun initData() {

        arguments?.apply {
            /*0 全部 1 待付款 2 待发货 3 待收货*/
            type = getInt(AppConstant.Constant.TYPE,-1)
        }

        when(type){
            AppConstant.Constant.NUM_ZERO ->{

            }
            AppConstant.Constant.NUM_ONE ->{

            }
            AppConstant.Constant.NUM_TWO ->{

            }
            AppConstant.Constant.NUM_THREE ->{

            }
        }

//        CoroutineScope(Dispatchers.IO).launch {
//            BaseAppDatabase.instance.mineGoodsDetailDao().insertData(mutableListOf<MineGoodsDetailData>().apply {
//                add(MineGoodsDetailData("1", 1, "sasdghc"))
//                add(MineGoodsDetailData("2", 2, "111"))
//                add(MineGoodsDetailData("3", 3, "222"))
//                add(MineGoodsDetailData("4", 3, "444"))
//                add(MineGoodsDetailData("5", 1, "555"))
//                add(MineGoodsDetailData("6", 1, "666"))
//                add(MineGoodsDetailData("7", 2, "666"))
//                add(MineGoodsDetailData("8", 1, "6666"))
//            })
//        }
        smartRefreshLayout.autoRefresh()
    }

    override fun initView() {
        initSmartRefreshLayout()
        initRecyclerView()
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mineViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = MineExchangeStatusAdapter(R.layout.item_mine_exchange_status,null)

        mAdapter.setOnItemClickListener { adapter, view, position ->
            val item = mAdapter.getItem(position)
            buildARouter(AppConstant.RoutePath.MINE_ORDER_DETAIL_ACTIVITY).withString(AppConstant.Constant.ID,item?.id).navigation()
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
        mineViewModel.queryGoodsList(type,pageNum)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        mineViewModel.queryGoodsList(type,pageNum)
    }
}