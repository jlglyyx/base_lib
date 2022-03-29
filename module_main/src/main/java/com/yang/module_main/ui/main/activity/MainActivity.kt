package com.yang.module_main.ui.main.activity

import android.Manifest
import android.content.res.ColorStateList
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tbruyelle.rxpermissions3.RxPermissions
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.TabAndViewPagerAdapter
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.LiveDataBus
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.getScreenPx
import com.yang.lib_common.util.px2dip
import com.yang.lib_common.util.showShort
import com.yang.module_main.R
import com.yang.module_main.ui.menu.fragment.LeftFragment
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


@Route(path = AppConstant.RoutePath.MAIN_ACTIVITY)
class MainActivity : BaseActivity() {

    @InjectViewModel
    lateinit var mainViewModel: MainViewModel

    private lateinit var fragments: MutableList<Fragment>

    private lateinit var titles: MutableList<String>

    private var firstClickTime = 0L

    private var icon =
        arrayOf(R.drawable.iv_home, R.drawable.iv_video, R.drawable.iv_picture, R.drawable.iv_mine)
    private var selectIcon = arrayOf(
        R.drawable.iv_home_select,
        R.drawable.iv_video_select,
        R.drawable.iv_picture_select,
        R.drawable.iv_mine_select
    )


    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun initData() {

        lifecycleScope.launch {
            val async = async(Dispatchers.IO) {
                fragments = mutableListOf<Fragment>().apply {
                    add(
                        buildARouter(AppConstant.RoutePath.MAIN_FRAGMENT)
                            .navigation() as Fragment
                    )
                    add(
                        buildARouter(AppConstant.RoutePath.VIDEO_FRAGMENT)
                            .navigation() as Fragment
                    )
                    add(
                        buildARouter(AppConstant.RoutePath.PICTURE_FRAGMENT)
                            .navigation() as Fragment
                    )
                    add(
                        buildARouter(AppConstant.RoutePath.MINE_FRAGMENT)
                            .navigation() as Fragment
                    )
                }
                titles = mutableListOf<String>().apply {
                    add("首页")
                    add("视频")
                    add("图片")
                    add("我的")
                }
                true
            }
            val await = async.await()
            withContext(Dispatchers.Main) {
                if (await) {
                    initDrawerLayout()
                    initViewPager()
                    initTabLayout()
                    initPermission()
                    initLeftFragment()
                }
            }

        }
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)

        LiveDataBus.instance.with("openDrawerLayout").observe(this, Observer {
            drawerLayout.openDrawer(GravityCompat.START)
        })

    }

    override fun initView() {

    }

    private fun initDrawerLayout() {
        val screenSize = getScreenPx(this)
        fragmentContainerView.post {
            val layoutParams = fragmentContainerView.layoutParams as DrawerLayout.LayoutParams
            fragmentContainerView.layoutParams = layoutParams
            layoutParams.rightMargin = fragmentContainerView.width - screenSize[0]
            fragmentContainerView.layoutParams = layoutParams
        }
    }


    private fun initViewPager() {

        viewPager.adapter = TabAndViewPagerAdapter(this, fragments, titles)
        viewPager.isUserInputEnabled = false
        viewPager.offscreenPageLimit = fragments.size

    }

    private fun initTabLayout() {

        TabLayoutMediator(
            tabLayout,
            viewPager,
            true,
            false
        ) { tab, position ->
            tab.text = titles[position]
            tab.setIcon(icon[position])
            if (position == 0) {
                tab.setIcon(selectIcon[position])
                (tab.view.getChildAt(0) as ImageView).imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.colorBar))
                //tab.customView = ATabLayout(this)
            } else {
                tab.setIcon(icon[position])
                (tab.view.getChildAt(0) as ImageView).imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.grey))
            }
            tab.view.setOnLongClickListener { true }
        }.attach()

        tabLayout.post {
            viewPager.setPadding(0, 0, 0, tabLayout.height + 10f.px2dip(this))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.setIcon(icon[tab.position])
                (tab.view.getChildAt(0) as ImageView).imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.grey))
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.setIcon(selectIcon[tab.position])
                (tab.view.getChildAt(0) as ImageView).imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(this@MainActivity, R.color.colorBar))

            }

        })

    }

    private fun initPermission() {
        RxPermissions(this).requestEachCombined(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        )
            .subscribe {
                when {
                    it.granted -> {
                        //showShort("呦西，小伙子很不错")
                    }

                    it.shouldShowRequestPermissionRationale -> {

                        showShort("逼崽子把权限关了还怎么玩，赶紧打开")
                    }
                    else -> {
                        showShort("逼崽子把权限关了还怎么玩，赶紧打开")
                    }
                }
            }
    }

    private fun initLeftFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragmentContainerView,
            LeftFragment()
        ).commit()
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - firstClickTime < 1500) {
                firstClickTime = 0L
                /*进入后台不退出*/
                moveTaskToBack(true)
                //super.onBackPressed()
            } else {
                firstClickTime = currentTimeMillis
                showShort("再按一次退出")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }
}