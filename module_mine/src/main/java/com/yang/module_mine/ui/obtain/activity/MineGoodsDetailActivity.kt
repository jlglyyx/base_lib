package com.yang.module_mine.ui.obtain.activity

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.appbar.AppBarLayout
import com.lxj.xpopup.XPopup
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.MBannerAdapter
import com.yang.lib_common.adapter.NormalImageAdapter
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.data.BannerBean
import com.yang.lib_common.dialog.ImageViewPagerDialog
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.showShort
import com.yang.module_mine.R
import com.yang.module_mine.dialog.MineChooseGoodsDialog
import com.yang.module_mine.viewmodel.MineViewModel
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.act_mine_exchange_goods_detail.*
import kotlin.math.abs

/**
 * @Author Administrator
 * @ClassName MineExchangeActivity
 * @Description 我的兑换-物品详情
 * @Date 2021/9/14 10:39
 */
@Route(path = AppConstant.RoutePath.MINE_GOODS_DETAIL_ACTIVITY)
class MineGoodsDetailActivity : BaseActivity() {

    @InjectViewModel
    lateinit var mineViewModel: MineViewModel

    private var alphaPercent = 0f

    private lateinit var mAdapter: NormalImageAdapter<String>

    private var id: String = ""

    private var currentPagePosition = 0

    override fun getLayout(): Int {
        return R.layout.act_mine_exchange_goods_detail
    }

    override fun initData() {
        intent?.apply {
            id = getStringExtra(AppConstant.Constant.ID) ?: ""
        }
        initBanner()
    }

    override fun initView() {
        initRecyclerView()
        initAppBarLayout()
        tv_exchange.clicks().subscribe {
            XPopup.Builder(this).asCustom(MineChooseGoodsDialog(this).apply {
                clickListener = object : MineChooseGoodsDialog.ClickListener {
                    override fun onClick(data:String) {
                        showShort(data)
                        dismiss()
                        buildARouter(AppConstant.RoutePath.MINE_CREATE_ORDER_ACTIVITY).navigation()
                    }
                }
            }).show()

        }
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mineViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun initAppBarLayout() {
        commonToolBar.tvCenterContent.alpha = alphaPercent
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            //滑动状态
            alphaPercent = abs(verticalOffset).toFloat() / appBarLayout.totalScrollRange.toFloat()
            if (alphaPercent <= 0.2f) {
                alphaPercent = 0f
            }
            if (alphaPercent >= 0.8f) {
                alphaPercent = 1f
            }
            commonToolBar.tvCenterContent.alpha = alphaPercent
        })
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter = NormalImageAdapter(mutableListOf<String>().apply {
            add("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg")
            add("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg")
            add("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg")
            add("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg")
            add("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg")
            add("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg")
            add("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg")
            add("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg")
            add("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg")
            add("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg")
        })
        mAdapter.setOnItemClickListener { adapter, view, position ->
            val imageViewPagerDialog =
                ImageViewPagerDialog(
                    this@MineGoodsDetailActivity,
                    mAdapter.data,
                    position,
                    true
                )
            XPopup.Builder(this@MineGoodsDetailActivity).asCustom(imageViewPagerDialog).show()
        }

        recyclerView.adapter = mAdapter

    }

    private fun initBanner() {
//        banner.setPageTransformer(AlphaPageTransformer())
        //       banner.addPageTransformer(DepthPageTransformer())
//        banner.addPageTransformer(RotateDownPageTransformer())
//        banner.addPageTransformer(RotateUpPageTransformer())
//        banner.addPageTransformer(RotateYTransformer())
//        banner.addPageTransformer(ScaleInTransformer())
        //       banner.addPageTransformer(ZoomOutPageTransformer())
        banner.addBannerLifecycleObserver(this)//添加生命周期观察者
            .setAdapter(MBannerAdapter(mutableListOf<BannerBean>().apply {
                add(BannerBean("https://scpic.chinaz.net/Files/pic/pic9/202107/bpic23678_s.jpg"))
                add(BannerBean("https://scpic1.chinaz.net/Files/pic/pic9/202107/apic33909_s.jpg"))
                add(BannerBean("https://scpic2.chinaz.net/Files/pic/pic9/202107/bpic23656_s.jpg"))
            }), false)
            .indicator = CircleIndicator(this)
    }


}