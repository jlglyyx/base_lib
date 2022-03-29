package com.yang.module_picture.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.TabAndViewPagerFragmentAdapter
import com.yang.lib_common.base.ui.fragment.BaseLazyFragment
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.lib_common.widget.CommonToolBar
import com.yang.module_picture.R
import com.yang.module_picture.viewmodel.PictureViewModel
import kotlinx.android.synthetic.main.fra_picture.*

@Route(path = AppConstant.RoutePath.PICTURE_FRAGMENT)
class PictureFragment : BaseLazyFragment() {

    @InjectViewModel
    lateinit var pictureViewModule: PictureViewModel

    private lateinit var fragments: MutableList<Fragment>

    private lateinit var titles: MutableList<String>

    override fun getLayout(): Int {
        return R.layout.fra_picture
    }

    override fun initData() {
        pictureViewModule.getImageTypeData()
        commonToolBar.imageAddCallBack = object : CommonToolBar.ImageAddCallBack {
            override fun imageAddClickListener() {
                buildARouter(AppConstant.RoutePath.PICTURE_UPLOAD_ACTIVITY).navigation()
            }
        }

        commonToolBar.imageSearchCallBack = object : CommonToolBar.ImageSearchCallBack{
            override fun imageSearchClickListener() {
                buildARouter(AppConstant.RoutePath.PICTURE_SEARCH_ACTIVITY).withInt(
                    AppConstant.Constant.TYPE,
                    AppConstant.Constant.NUM_ZERO
                ).navigation()

            }
        }
        commonToolBar.ivSearch.visibility = View.VISIBLE

    }

    override fun initView() {
        titles = mutableListOf()
        fragments = mutableListOf()

        view_error_re_load_data.clicks().subscribe {
            pictureViewModule.getImageTypeData()
        }
    }

    override fun initUIChangeLiveData(): UIChangeLiveData? {
        return pictureViewModule.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)

        pictureViewModule.mImageTypeData.observe(this, Observer {
            view_error_re_load_data.visibility = View.GONE
            tabLayout.visibility = View.VISIBLE
            it.forEach { imageTypeData ->
                titles.add(imageTypeData.name)
                fragments.add(
                    buildARouter(AppConstant.RoutePath.PICTURE_ITEM_FRAGMENT)
                        .withString(AppConstant.Constant.TYPE, imageTypeData.type)
                        .navigation() as Fragment
                )
            }
            initViewPager()
            initTabLayout()
        })

        pictureViewModule.uC.requestFailEvent.observe(this, Observer {
            tabLayout.visibility = View.GONE
            view_error_re_load_data.visibility = View.VISIBLE
        })
    }


    private fun initViewPager() {

        viewPager.adapter = TabAndViewPagerFragmentAdapter(this, fragments, titles)
        if (fragments.size != 0) {
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