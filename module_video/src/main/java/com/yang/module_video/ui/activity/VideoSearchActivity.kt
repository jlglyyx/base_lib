package com.yang.module_video.ui.activity

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.gson.Gson
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.BaseAppDatabase
import com.yang.lib_common.room.entity.SearchData
import com.yang.lib_common.room.entity.VideoDataItem
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.simpleDateFormat
import com.yang.module_video.R
import com.yang.module_video.adapter.VideoAdapter
import com.yang.module_video.viewmodel.VideoViewModel
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.act_video_search.*
import kotlinx.android.synthetic.main.view_normal_recyclerview.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * @Author Administrator
 * @ClassName SearchActivity
 * @Description
 * @Date 2021/9/8 11:37
 */
@Route(path = AppConstant.RoutePath.VIDEO_SEARCH_ACTIVITY)
class VideoSearchActivity : BaseActivity(), OnRefreshLoadMoreListener {

    @InjectViewModel
    lateinit var videoViewModel: VideoViewModel
    @Inject
    lateinit var gson: Gson
    private var pageNum = 1
    private var list = mutableListOf<SearchData>()
    private lateinit var flowLayoutAdapter: FlowLayoutAdapter
    private lateinit var mAdapter: VideoAdapter

    private var type:Int = -1
    private var keyword = ""

    override fun getLayout(): Int {
        return R.layout.act_video_search
    }

    override fun initData() {
        type = intent.getIntExtra(AppConstant.Constant.TYPE, -1)
        queryData()
    }

    override fun initView() {
        initRecyclerView()
        initSmartRefreshLayout()
        initTextListener()
        et_search.requestFocus()
        iv_back.clicks().subscribe {
            finish()
        }
        iv_delete.clicks().subscribe {
            deleteAllData()
        }
        tv_search.clicks().subscribe {
            if (TextUtils.isEmpty(et_search.text.toString().trim())) {
                return@subscribe
            }
            et_search.clearFocus()
            keyword = et_search.text.toString()
            onRefresh(smartRefreshLayout)
            if (list.findLast { TextUtils.equals(it.content, et_search.text.toString()) } != null){
                return@subscribe
            }
            insertData()
        }



    }

    override fun initUIChangeLiveData(): UIChangeLiveData? {
        return videoViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
    }

    private fun initTextListener(){

        et_search.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                flowLayout.visibility = View.VISIBLE
                iv_delete.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }else{
                flowLayout.visibility = View.GONE
                iv_delete.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                mAdapter.setEmptyView(R.layout.view_black_data,recyclerView)
            }
        }
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = VideoAdapter(R.layout.item_video_image, mutableListOf()).also {
            it.setOnItemClickListener { adapter, view, position ->
                val imageData = adapter.data[position] as VideoDataItem
                buildARouter(AppConstant.RoutePath.VIDEO_ITEM_ACTIVITY)
                    .withString(AppConstant.Constant.ID, imageData.id)
                    .navigation()
            }
        }
        recyclerView.adapter = mAdapter
        videoViewModel.mVideoData.observe(this, Observer {
            when {
                smartRefreshLayout.isRefreshing -> {
                    smartRefreshLayout.finishRefresh()
                    if (it.list.isEmpty()) {
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
                    if (it.list.isEmpty()) {
                        videoViewModel.showRecyclerViewEmptyEvent()
                    } else {
                        mAdapter.replaceData(it.list)
                    }
                }
            }
        })

        registerRefreshAndRecyclerView(smartRefreshLayout,mAdapter)
    }

    private fun initFlowLayout() {
        flowLayoutAdapter = FlowLayoutAdapter(list)
        flowLayout.adapter = flowLayoutAdapter
        if (list.size == 0){
            iv_delete.visibility = View.GONE
        }else{
            iv_delete.visibility = View.VISIBLE
        }

        flowLayout.setOnTagClickListener { view, position, parent ->
            et_search.setText(list[position].content)
            et_search.setSelection(list[position].content.length)
            et_search.clearFocus()
            keyword = list[position].content
            onRefresh(smartRefreshLayout)
            return@setOnTagClickListener true
        }
    }

    private fun insertData() {
        val searchData = SearchData(
            UUID.randomUUID().toString().replace("-", ""),
            type,
            et_search.text.toString(),
            simpleDateFormat.format(System.currentTimeMillis()),
            simpleDateFormat.format(System.currentTimeMillis())
        )
        lifecycleScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                BaseAppDatabase.instance.searchHistoryDao().insertData(searchData)
            }
            val await = async.await()
            if (await != 0L) {
                list.add(searchData)
                flowLayoutAdapter.notifyDataChanged()
            }
        }
    }

    private fun queryData() {
        lifecycleScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                BaseAppDatabase.instance.searchHistoryDao().queryAllByType(type)
            }
            list.addAll(async.await())
            initFlowLayout()
        }
    }

    private fun deleteData(position: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                BaseAppDatabase.instance.searchHistoryDao().deleteData(list[position])
            }
            val await = async.await()
            if (await != 0) {
                list.remove(list[position])
                if (list.size == 0){
                    iv_delete.visibility = View.GONE
                }
                flowLayoutAdapter.notifyDataChanged()
            }
        }
    }
    private fun deleteAllData() {
        lifecycleScope.launch(Dispatchers.Main) {
            val async = async(Dispatchers.IO) {
                BaseAppDatabase.instance.searchHistoryDao().deleteAllData(list)
            }
            val await = async.await()
            if (await != 0) {
                list.clear()
                iv_delete.visibility = View.GONE
                flowLayoutAdapter.notifyDataChanged()
            }
        }
    }

    inner class FlowLayoutAdapter(mData: MutableList<SearchData>) : TagAdapter<SearchData>(mData) {
        override fun getView(parent: FlowLayout, position: Int, t: SearchData): View {
            val view = LayoutInflater.from(this@VideoSearchActivity)
                .inflate(R.layout.item_flow_layout, null, false)
            val tvContent = view.findViewById<TextView>(R.id.tv_content)
            val tvDelete = view.findViewById<TextView>(R.id.tv_delete)
            tvContent.text = t.content
            tvDelete.clicks().subscribe {
                deleteData(position)
            }
            return view
        }

    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNum = 1
        videoViewModel.getVideoInfo("",pageNum,keyword,true)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        videoViewModel.getVideoInfo("",pageNum,keyword,true)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }
}