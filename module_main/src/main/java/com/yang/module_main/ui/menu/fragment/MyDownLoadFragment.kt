package com.yang.module_main.ui.menu.fragment

import android.os.Environment
import android.text.TextUtils
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.fragment.BaseLazyFragment
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.filterEmptyFile
import com.yang.lib_common.util.getFilePath
import com.yang.module_main.R
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.view_normal_recyclerview.*

/**
 * @Author Administrator
 * @ClassName MyCollectionVideoFragment
 * @Description
 * @Date 2021/7/30 14:36
 */
@Route(path = AppConstant.RoutePath.MY_DOWNLOAD_FRAGMENT)
class MyDownLoadFragment : BaseLazyFragment(), OnRefreshLoadMoreListener {


    @InjectViewModel
    lateinit var mainViewModel: MainViewModel

    private lateinit var mAdapter: MAdapter

    private var type: String = ""

    override fun getLayout(): Int {
        return R.layout.fra_my_collection_video
    }

    override fun initData() {
        smartRefreshLayout.autoRefresh()
        initSmartRefreshLayout()
        type = arguments?.getString(AppConstant.Constant.TYPE) ?: ""

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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = MAdapter(R.layout.item_menu_my_collection_picture, mutableListOf()).apply {
            setOnItemClickListener { adapter, view, position ->
                val item = mAdapter.getItem(position)
                if (TextUtils.equals(type, AppConstant.Constant.VIDEO)) {
                    buildARouter(AppConstant.RoutePath.VIDEO_ITEM_ACTIVITY)
                        .withString(AppConstant.Constant.URL, item)
                        .navigation()
                } else {
                    buildARouter(AppConstant.RoutePath.PICTURE_ITEM_ACTIVITY)
                        .withString(AppConstant.Constant.ID, "0").navigation()
                }
            }
        }
        recyclerView.adapter = mAdapter
        registerRefreshAndRecyclerView(smartRefreshLayout,mAdapter)
    }

    inner class MAdapter(layoutResId: Int, list: MutableList<String>) :
        BaseQuickAdapter<String, BaseViewHolder>(layoutResId, list) {
        override fun convert(helper: BaseViewHolder, item: String) {
            if (TextUtils.equals(type, AppConstant.Constant.VIDEO)) {
                val ivImage = helper.getView<ImageView>(R.id.iv_image)
                Glide.with(ivImage)
                    .setDefaultRequestOptions(RequestOptions().frame(1000).fitCenter()).load(item)
                    .error(R.drawable.iv_image_error)
                    .placeholder(R.drawable.iv_image_placeholder).into(ivImage)
            } else {
                val ivImage = helper.getView<ImageView>(R.id.iv_image)
                Glide.with(ivImage).load(item).error(R.drawable.iv_image_error)
                    .placeholder(R.drawable.iv_image_placeholder).into(ivImage)
            }
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        val filePath = getFilePath("${Environment.getExternalStorageDirectory()}/MFiles/$type").filterEmptyFile()
        mAdapter.replaceData(filePath)
        smartRefreshLayout.finishRefresh()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        smartRefreshLayout.setNoMoreData(true)
    }
}