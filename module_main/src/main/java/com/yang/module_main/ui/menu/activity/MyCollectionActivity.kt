package com.yang.module_main.ui.menu.activity

import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.yang.lib_common.adapter.TabAndViewPagerAdapter
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.util.buildARouter
import com.yang.module_main.R
import kotlinx.android.synthetic.main.act_my_collection.*
import kotlinx.android.synthetic.main.activity_main.tabLayout
import kotlinx.android.synthetic.main.activity_main.viewPager

/**
 * @Author Administrator
 * @ClassName MyCollectionActivity
 * @Description
 * @Date 2021/7/30 14:35
 */
@Route(path = AppConstant.RoutePath.MY_COLLECTION_ACTIVITY)
class MyCollectionActivity : BaseActivity() {

    private lateinit var fragments: MutableList<Fragment>

    private lateinit var titles: MutableList<String>

    override fun getLayout(): Int {
        return R.layout.act_my_collection
    }

    override fun initData() {


        titles = mutableListOf<String>().apply {
            add("图片")
            add("视频")
        }
    }

    override fun initView() {

        val title = intent.getStringExtra(AppConstant.Constant.NAME)
        val type = intent.getStringExtra(AppConstant.Constant.TYPE)
        commonToolBar.centerContent = title
        if (TextUtils.equals(type,AppConstant.Constant.COLLECT)){
            fragments = mutableListOf<Fragment>().apply {
                add(
                    buildARouter(AppConstant.RoutePath.MY_COLLECTION_FRAGMENT)
                        .withString(AppConstant.Constant.TYPE,AppConstant.Constant.PICTURE)
                        .navigation() as Fragment
                )
                add(
                    buildARouter(AppConstant.RoutePath.MY_COLLECTION_FRAGMENT)
                        .withString(AppConstant.Constant.TYPE,AppConstant.Constant.VIDEO)
                        .navigation() as Fragment
                )
            }
        }else{
            fragments = mutableListOf<Fragment>().apply {
                add(
                    buildARouter(AppConstant.RoutePath.MY_DOWNLOAD_FRAGMENT)
                        .withString(AppConstant.Constant.TYPE,AppConstant.Constant.PICTURE)
                        .navigation() as Fragment
                )
                add(
                    buildARouter(AppConstant.RoutePath.MY_DOWNLOAD_FRAGMENT)
                        .withString(AppConstant.Constant.TYPE,AppConstant.Constant.VIDEO)
                        .navigation() as Fragment
                )
            }
        }

        initViewPager()
        initTabLayout()
    }

    override fun initViewModel() {
    }



    private fun initViewPager() {

        viewPager.adapter = TabAndViewPagerAdapter(this,fragments,titles)
        viewPager.offscreenPageLimit = fragments.size

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