package com.yang.module_video.ui.fragment

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.fragment.BaseLazyFragment
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.helper.ItemTouchHelperCallback
import com.yang.lib_common.helper.MoveAndSwipedListener
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.widget.CommonToolBar
import com.yang.module_video.R
import com.yang.module_video.data.FriendInfoData
import com.yang.module_video.viewmodel.VideoViewModel
import kotlinx.android.synthetic.main.fra_video.*

@Route(path = AppConstant.RoutePath.VIDEO_FRAGMENT)
class VideoFragment : BaseLazyFragment(), OnRefreshListener {
    @InjectViewModel
    lateinit var videoViewModel: VideoViewModel


    private lateinit var mAdapter: MAdapter

    override fun getLayout(): Int {
        return R.layout.fra_video
    }

    override fun initData() {

        initSmartRefreshLayout()
    }

    override fun initView() {

        commonToolBar.imageAddCallBack = object : CommonToolBar.ImageAddCallBack {
            override fun imageAddClickListener() {
                buildARouter(AppConstant.RoutePath.VIDEO_UPLOAD_ACTIVITY).withInt(
                    AppConstant.Constant.TYPE,
                    AppConstant.Constant.NUM_ONE
                ).navigation()
            }
        }
        commonToolBar.imageSearchCallBack = object : CommonToolBar.ImageSearchCallBack {
            override fun imageSearchClickListener() {
                buildARouter(AppConstant.RoutePath.VIDEO_SEARCH_ACTIVITY).withInt(
                    AppConstant.Constant.TYPE,
                    AppConstant.Constant.NUM_ONE
                ).navigation()

            }

        }
        commonToolBar.ivSearch.visibility = View.VISIBLE


        initRecyclerView()



    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return videoViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshListener(this)
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = linearLayoutManager
        mAdapter = MAdapter(mutableListOf<FriendInfoData>().apply {
            for (i in 1..30){
                add(FriendInfoData("$i"))
            }
        })
        recyclerView.adapter = mAdapter
        ItemTouchHelper(ItemTouchHelperCallback(object : MoveAndSwipedListener {
            override fun onDelete(index: Int) {
                mAdapter.remove(index)
                mAdapter.notifyItemRemoved(index)
            }

            override fun onMoved(startIndex: Int, endIndex: Int) {
//                val item = mAdapter.getItem(startIndex)
//                mAdapter.addData(endIndex,item!!)
                mAdapter.notifyItemMoved(startIndex,endIndex)
            }

        })).attachToRecyclerView(recyclerView)

        registerRefreshAndRecyclerView(smartRefreshLayout, mAdapter)
    }


    inner class MAdapter(list: MutableList<FriendInfoData>) :
        BaseQuickAdapter<FriendInfoData, BaseViewHolder>(list) {
        init {
            mLayoutResId = R.layout.item_friend
        }

        override fun convert(helper: BaseViewHolder, item: FriendInfoData) {
            val view = helper.getView<ShapeableImageView>(R.id.siv_image)
            Glide.with(mContext).load("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F39%2Fb7%2F53%2F39b75357f98675e2d6d5dcde1fb805a3.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642840086&t=2a7574a5d8ecc96669ac3e050fe4fd8e"
            ).error(R.drawable.iv_image_error)
                .placeholder(R.drawable.iv_image_placeholder).into(view)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

}