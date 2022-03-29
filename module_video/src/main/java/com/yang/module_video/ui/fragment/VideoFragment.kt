package com.yang.module_video.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.TabAndViewPagerFragmentAdapter
import com.yang.lib_common.base.ui.fragment.BaseLazyFragment
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.BaseAppDatabase
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.lib_common.widget.CommonToolBar
import com.yang.module_video.R
import com.yang.module_video.viewmodel.VideoViewModel
import kotlinx.android.synthetic.main.fra_video.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Route(path = AppConstant.RoutePath.VIDEO_FRAGMENT)
class VideoFragment : BaseLazyFragment() {
    @InjectViewModel
    lateinit var videoViewModel: VideoViewModel

    private lateinit var fragments: MutableList<Fragment>

    private lateinit var titles: MutableList<String>

    override fun getLayout(): Int {
        return R.layout.fra_video
    }

    override fun initData() {

        videoViewModel.getVideoTypeData()

    }

    override fun initView() {
        titles = mutableListOf()
        fragments = mutableListOf()

        commonToolBar.imageAddCallBack = object : CommonToolBar.ImageAddCallBack{
            override fun imageAddClickListener() {
                buildARouter(AppConstant.RoutePath.VIDEO_UPLOAD_ACTIVITY).withInt(AppConstant.Constant.TYPE,AppConstant.Constant.NUM_ONE).navigation()
            }
        }
        commonToolBar.imageSearchCallBack = object : CommonToolBar.ImageSearchCallBack{
            override fun imageSearchClickListener() {
                buildARouter(AppConstant.RoutePath.VIDEO_SEARCH_ACTIVITY).withInt(AppConstant.Constant.TYPE,AppConstant.Constant.NUM_ONE).navigation()

            }

        }
        commonToolBar.ivSearch.visibility = View.VISIBLE

        view_error_re_load_data.clicks().subscribe {
            videoViewModel.getVideoTypeData()
        }
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return videoViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)

        videoViewModel.mVideoTypeData.observe(this, Observer {
            view_error_re_load_data.visibility = View.GONE
            tabLayout.visibility = View.VISIBLE
            it.forEach { videoTypeData ->
                titles.add(videoTypeData.name)
                fragments.add(
                    buildARouter(AppConstant.RoutePath.VIDEO_ITEM_FRAGMENT)
                        .withString(AppConstant.Constant.TYPE,videoTypeData.type)
                        .navigation() as Fragment
                )
            }
            lifecycleScope.launch(Dispatchers.IO){
                BaseAppDatabase.instance.videoTypeDao().updateData(it)
            }
            initViewPager()
            initTabLayout()

        })

        videoViewModel.uC.requestFailEvent.observe(this, Observer {
            tabLayout.visibility = View.GONE
            view_error_re_load_data.visibility = View.VISIBLE
        })

    }


    private fun initViewPager() {

        viewPager.adapter = TabAndViewPagerFragmentAdapter(this, fragments, titles)
        if(fragments.size != 0){
            viewPager.offscreenPageLimit = fragments.size
        }
    }

    private fun initTabLayout() {

        TabLayoutMediator(
            tabLayout,
            viewPager
        ) { tab, position ->
            tab.text = titles[position]
        }.attach()

    }
}