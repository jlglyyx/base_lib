package com.yang.module_picture.ui.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.MBannerAdapter
import com.yang.lib_common.base.ui.fragment.BaseLazyFragment
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.data.BannerBean
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.entity.ImageDataItem
import com.yang.lib_common.util.buildARouter
import com.yang.module_picture.R
import com.yang.module_picture.adapter.PictureAdapter
import com.yang.module_picture.viewmodel.PictureViewModel
import com.youth.banner.indicator.CircleIndicator
import kotlinx.android.synthetic.main.fra_item_picture.*
import kotlin.random.Random

@Route(path = AppConstant.RoutePath.PICTURE_ITEM_FRAGMENT)
class PictureItemFragment : BaseLazyFragment(), OnRefreshLoadMoreListener {

    @InjectViewModel
    lateinit var pictureModule: PictureViewModel

    private var queryType: String? = null

    private var pageNum = 1


    private lateinit var mAdapter: PictureAdapter


    override fun getLayout(): Int {
        return R.layout.fra_item_picture
    }

    override fun initData() {
        queryType = arguments?.getString(AppConstant.Constant.TYPE)
        pictureModule.getImageInfo(queryType ?: "", pageNum)
        initSmartRefreshLayout()

    }

    override fun initView() {
        initBanner()
        initRecyclerView()
    }

    override fun initUIChangeLiveData(): UIChangeLiveData? {
        return pictureModule.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)

        pictureModule.mImageData.observe(this, Observer {
            if (it.list.isNotEmpty() && pictureModule.mTTNativeExpressAdList.isNotEmpty()){
                pictureModule.mTTNativeExpressAdList.forEach { adItem ->
                    it.list.add(Random.nextInt(0,it.list.size),adItem)
                }
            }
            when {
                smartRefreshLayout.isRefreshing -> {
                    smartRefreshLayout.finishRefresh()
                    if (it.list.isNullOrEmpty()) {
                        pictureModule.showRecyclerViewEmptyEvent()
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
                    if (it.list.isNullOrEmpty()) {
                        pictureModule.showRecyclerViewEmptyEvent()
                    } else {
                        mAdapter.replaceData(it.list)
                    }
                }
            }

        })

    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
    }


    private fun initRecyclerView() {
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mAdapter = PictureAdapter(requireActivity(), mutableListOf()).also {
            it.setOnItemClickListener { adapter, view, position ->
                val imageData = adapter.data[position] as ImageDataItem
                buildARouter(AppConstant.RoutePath.PICTURE_ITEM_ACTIVITY)
                    .withString(AppConstant.Constant.ID, imageData.id)
                    .navigation()
            }
        }
        recyclerView.adapter = mAdapter


        registerRefreshAndRecyclerView(smartRefreshLayout, mAdapter)
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
                add(BannerBean("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F26%2F20200326212002_rxlyj.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1641715864&t=fa48084bc521db8a18fe5f61842cab7e"))
                add(BannerBean("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F26%2F20200326212002_rxlyj.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1641715864&t=fa48084bc521db8a18fe5f61842cab7e"))
                add(BannerBean("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F26%2F20200326212002_rxlyj.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1641715864&t=fa48084bc521db8a18fe5f61842cab7e"))
                add(BannerBean("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F26%2F20200326212002_rxlyj.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1641715864&t=fa48084bc521db8a18fe5f61842cab7e"))
                add(BannerBean("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F26%2F20200326212002_rxlyj.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1641715864&t=fa48084bc521db8a18fe5f61842cab7e"))
                add(BannerBean("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F26%2F20200326212002_rxlyj.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1641715864&t=fa48084bc521db8a18fe5f61842cab7e"))
                add(BannerBean("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F26%2F20200326212002_rxlyj.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1641715864&t=fa48084bc521db8a18fe5f61842cab7e"))
                add(BannerBean("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202003%2F26%2F20200326212002_rxlyj.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1641715864&t=fa48084bc521db8a18fe5f61842cab7e"))
//                add(BannerBean("http://10.16.242.28:20002/files/1/1.jpg"))
//                add(BannerBean("http://10.16.242.28:20002/files/2/1.jpg"))
//                add(BannerBean("http://10.16.242.28:20002/files/3/1.jpg"))
//                add(BannerBean("http://10.16.242.28:20002/files/4/1.jpg"))
//                add(BannerBean("http://10.16.242.28:20002/files/5/1.jpg"))
//                add(BannerBean("http://10.16.242.28:20002/files/6/1.jpg"))
//                add(BannerBean("http://10.16.242.28:20002/files/7/1.jpg"))
//                add(BannerBean("http://10.16.242.28:20002/files/8/1.jpg"))
//                add(BannerBean("http://10.16.242.28:20002/files/9/1.jpg"))

            }))
            .indicator = CircleIndicator(requireContext())
    }


    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        pictureModule.getImageInfo(queryType ?: "", pageNum)
        pictureModule.loadPictureAd()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNum = 1
        pictureModule.getImageInfo(queryType ?: "", pageNum)
        pictureModule.loadPictureAd()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAdapter.data.filter {
            it.mItemType != AppConstant.Constant.ITEM_AD
        }.forEach {
            it.mTTNativeExpressAd?.destroy()
        }
    }
}