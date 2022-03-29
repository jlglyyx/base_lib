package com.yang.module_main.ui.menu.activity

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.lxj.xpopup.XPopup
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.dialog.ImageViewPagerDialog
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.getUserInfo
import com.yang.lib_common.util.symbolToList
import com.yang.module_main.R
import com.yang.module_main.adapter.DynamicAdapter
import com.yang.module_main.data.model.DynamicData
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.view_normal_recyclerview.*

@Route(path = AppConstant.RoutePath.MY_PUSH_ACTIVITY)
class MyPushActivity : BaseActivity(), OnRefreshLoadMoreListener {

    @InjectViewModel
    lateinit var mainViewModel: MainViewModel

    private lateinit var mAdapter: MAdapter

    private var pageNum = 1

    override fun getLayout(): Int {
        return R.layout.act_my_push
    }

    override fun initData() {
        smartRefreshLayout.autoRefresh()
        initSmartRefreshLayout()
        mainViewModel.dynamicListLiveData.observe(this, Observer {
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

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mainViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)


    }
    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = MAdapter(mutableListOf()).apply {
            setOnItemChildClickListener { adapter, view, position ->
                when (view.id) {
                    R.id.siv_img -> {
                        buildARouter(AppConstant.RoutePath.MINE_OTHER_PERSON_INFO_ACTIVITY).withString(AppConstant.Constant.ID,"").navigation()
                    }

                }
            }
        }
        recyclerView.adapter = mAdapter
        registerRefreshAndRecyclerView(smartRefreshLayout,mAdapter)
    }

    inner class MAdapter(list: MutableList<DynamicData>) :
        BaseQuickAdapter<DynamicData, BaseViewHolder>(list) {

        init {
            mLayoutResId = R.layout.view_dynamic_item
        }

        override fun convert(helper: BaseViewHolder, item: DynamicData) {

            initItemMainTitle(helper,item)

            if (item.content.isNullOrEmpty()){
                helper.setGone(R.id.item_main_content_text,false)
            }else{
                helper.setGone(R.id.item_main_content_text,true)
                initItemMainContentText(helper,item)
            }

            if (item.imageUrls.isNullOrEmpty()){
                helper.setGone(R.id.mRecyclerView,false)
            }else{
                helper.setGone(R.id.mRecyclerView,true)
                initItemMainContentImage(helper,item)
            }
            initItemMainIdentification(helper,item)
        }

        private fun initItemMainTitle(helper: BaseViewHolder,item: DynamicData){
            val sivImg = helper.getView<ShapeableImageView>(R.id.siv_img)
            helper.addOnClickListener(R.id.siv_img)
            helper.setText(R.id.tv_time, item.createTime)
            Glide.with(sivImg).load(item.userImage).error(R.drawable.iv_image_error)
                .placeholder(R.drawable.iv_image_placeholder).into(sivImg)
        }
        private fun initItemMainContentText(helper: BaseViewHolder,item: DynamicData){
            helper.setText(R.id.tv_text, item.content)
        }
        private fun initItemMainContentImage(helper: BaseViewHolder,item: DynamicData){
            val mRecyclerView = helper.getView<RecyclerView>(R.id.mRecyclerView)
            mRecyclerView.layoutManager = GridLayoutManager(mContext,3)
            val dynamicAdapter = DynamicAdapter(
                R.layout.view_item_grid_nine_picture,
                item.imageUrls?.symbolToList("#")!!
            )
            mRecyclerView.adapter = dynamicAdapter
            dynamicAdapter.setOnItemClickListener { adapter, view, position ->
                val imageViewPagerDialog =
                    ImageViewPagerDialog(this@MyPushActivity, item.imageUrls?.symbolToList("#")!!, position,true)
                XPopup.Builder(this@MyPushActivity).asCustom(imageViewPagerDialog).show()
            }
        }
        private fun initItemMainIdentification(helper: BaseViewHolder,item: DynamicData){

        }
    }

    private fun getDynamicList(){
        val mutableMapOf = mutableMapOf<String, String>()
        mutableMapOf[AppConstant.Constant.USER_ID] = getUserInfo()?.id?:""
        mutableMapOf[AppConstant.Constant.PAGE_NUMBER] = pageNum.toString()
        mutableMapOf[AppConstant.Constant.PAGE_SIZE] = AppConstant.Constant.PAGE_SIZE_COUNT.toString()
        mainViewModel.getDynamicList(mutableMapOf)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        getDynamicList()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNum = 1
        getDynamicList()
    }

}