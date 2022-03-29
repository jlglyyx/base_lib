package com.yang.module_mine.ui.obtain.activity

import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayoutMediator
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.TabAndViewPagerAdapter
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.module_mine.R
import com.yang.module_mine.viewmodel.MineViewModel
import kotlinx.android.synthetic.main.act_mine_exchange.*

/**
 * @Author Administrator
 * @ClassName MineExchangeActivity
 * @Description 我的兑换
 * @Date 2021/9/14 10:39
 */
@Route(path = AppConstant.RoutePath.MINE_EXCHANGE_ACTIVITY)
class MineExchangeActivity :BaseActivity() {

    @InjectViewModel
    lateinit var mineViewModel: MineViewModel

    private lateinit var fragments: MutableList<Fragment>

    private lateinit var titles: MutableList<String>


    override fun getLayout(): Int {
        return R.layout.act_mine_exchange
    }

    override fun initData() {
        titles = mutableListOf()
        fragments = mutableListOf()
        titles.add("全部")
        titles.add("待付款")
        titles.add("待发货")
        titles.add("待收货")
        fragments.add(buildARouter(AppConstant.RoutePath.MINE_EXCHANGE_STATUS_FRAGMENT).withInt(AppConstant.Constant.TYPE,AppConstant.Constant.NUM_ZERO).navigation() as Fragment)
        fragments.add(buildARouter(AppConstant.RoutePath.MINE_EXCHANGE_STATUS_FRAGMENT).withInt(AppConstant.Constant.TYPE,AppConstant.Constant.NUM_ONE).navigation() as Fragment)
        fragments.add(buildARouter(AppConstant.RoutePath.MINE_EXCHANGE_STATUS_FRAGMENT).withInt(AppConstant.Constant.TYPE,AppConstant.Constant.NUM_TWO).navigation() as Fragment)
        fragments.add(buildARouter(AppConstant.RoutePath.MINE_EXCHANGE_STATUS_FRAGMENT).withInt(AppConstant.Constant.TYPE,AppConstant.Constant.NUM_THREE).navigation() as Fragment)
    }

    override fun initView() {
        initViewPager()
        initTabLayout()
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mineViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun initViewPager() {

        viewPager.adapter = TabAndViewPagerAdapter(this, fragments, titles)
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