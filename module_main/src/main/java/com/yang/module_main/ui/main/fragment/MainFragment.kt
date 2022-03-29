package com.yang.module_main.ui.main.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.TabAndViewPagerFragmentAdapter
import com.yang.lib_common.base.ui.fragment.BaseLazyFragment
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.module_main.R
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fra_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@Route(path = AppConstant.RoutePath.MAIN_FRAGMENT)
class MainFragment : BaseLazyFragment() {

    @Inject
    lateinit var gson: Gson

    @InjectViewModel
    lateinit var mainViewModel: MainViewModel

    private lateinit var fragments: MutableList<Fragment>

    private lateinit var titles: MutableList<String>


    override fun getLayout(): Int {
        return R.layout.fra_main
    }

    override fun initData() {
        lifecycleScope.launch {
            val async = async(Dispatchers.IO) {
                fragments = mutableListOf<Fragment>().apply {
                    add(
                        buildARouter(AppConstant.RoutePath.MAIN_LEFT_FRAGMENT)
                            .navigation() as Fragment
                    )
                    add(
                        buildARouter(AppConstant.RoutePath.MAIN_RIGHT_FRAGMENT)
                            .navigation() as Fragment
                    )
                }
                titles = mutableListOf<String>().apply {
                    add("首页")
                    add("时间线")
                }
                true
            }
            val await = async.await()
            withContext(Dispatchers.Main) {
                if (await) {
                    initViewPager()
                    initTabLayout()
                }
            }

        }
    }

    override fun initView() {


    }

    private fun initViewPager() {

        viewPager.adapter = TabAndViewPagerFragmentAdapter(this, fragments, titles)
        viewPager.offscreenPageLimit = fragments.size

    }

    private fun initTabLayout() {

        TabLayoutMediator(
            tabLayout,
            viewPager,
            true
        ) { tab, position ->
            tab.text = titles[position]
            tab.view.setOnLongClickListener { true }
        }.attach()
    }

    override fun initUIChangeLiveData(): UIChangeLiveData? {
        return mainViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }


}