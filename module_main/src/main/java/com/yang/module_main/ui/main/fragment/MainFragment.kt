package com.yang.module_main.ui.main.fragment

import android.content.Intent
import android.util.Log
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bytedance.sdk.openadsdk.*
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.gson.Gson
import com.lxj.xpopup.XPopup
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.fragment.BaseLazyFragment
import com.yang.lib_common.bus.event.LiveDataBus
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.dialog.ImageViewPagerDialog
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.*
import com.yang.lib_common.widget.CommonToolBar
import com.yang.lib_common.widget.GridNinePictureView
import com.yang.module_main.R
import com.yang.module_main.data.model.DynamicData
import com.yang.module_main.ui.main.activity.AddDynamicActivity
import com.yang.module_main.ui.main.activity.MainActivity
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fra_main.*
import kotlinx.android.synthetic.main.view_dynamic_item.*
import kotlinx.android.synthetic.main.view_normal_recyclerview.*
import java.util.*
import javax.inject.Inject
import kotlin.random.Random


@Route(path = AppConstant.RoutePath.MAIN_FRAGMENT)
class MainFragment : BaseLazyFragment(), OnRefreshLoadMoreListener {

    @Inject
    lateinit var gson: Gson

    @InjectViewModel
    lateinit var mainViewModel: MainViewModel

    private lateinit var mAdapter: MAdapter

    private var mutableListOf: MutableList<DynamicData> = mutableListOf()

    private var pageNum = 1

    private var mTTAdNative: TTAdNative? = null


    override fun getLayout(): Int {
        return R.layout.fra_main
    }

    override fun initData() {
        smartRefreshLayout.autoRefresh()
        initSmartRefreshLayout()

        LiveDataBus.instance.with("refresh_dynamic").observe(this, Observer {

            onRefresh(smartRefreshLayout)
        })

        mainViewModel.dynamicListLiveData.observe(this, Observer {
            if (it.isNotEmpty() && mainViewModel.mTTNativeExpressAdList.isNotEmpty()){
                mainViewModel.mTTNativeExpressAdList.forEach { adItem ->
                    it.add(Random.nextInt(0,it.size),adItem)
                }
            }
            when {
                smartRefreshLayout.isRefreshing -> {
                    smartRefreshLayout.finishRefresh()
                    if (it.isNullOrEmpty()) {
                        mainViewModel.showRecyclerViewEmptyEvent()
                    } else {
                        mAdapter.replaceData(it)

                    }
                }
                smartRefreshLayout.isLoading -> {
                    smartRefreshLayout.finishLoadMore()
                    if (it.isNullOrEmpty()) {
                        smartRefreshLayout.setNoMoreData(true)
                    } else {
                        smartRefreshLayout.setNoMoreData(false)
                        mAdapter.addData(it)
                    }
                }
                else -> {
                    if (it.isNullOrEmpty()) {
                        mainViewModel.showRecyclerViewEmptyEvent()
                    } else {
                        mAdapter.replaceData(it)
                    }
                }
            }
        })
    }

    override fun initView() {
        initRecyclerView()
        val registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode != AppCompatActivity.RESULT_OK) {
                    return@registerForActivityResult
                }
                val images = it.data?.getStringArrayListExtra(AppConstant.Constant.DATA)
                val content = it.data?.getStringExtra(AppConstant.Constant.CONTENT)
                val mutableListOf = mutableListOf<DynamicData>().apply {
                    if (!content.isNullOrEmpty()) {
                        add(DynamicData().apply {
                            this.content = content
                        })
                    }
                    if (!images.isNullOrEmpty()) {
                        add((DynamicData().apply {
                            imageUrls = images.formatWithSymbol("#")
                        }))
                    }
                }
                mAdapter.addData(0, mutableListOf)
                recyclerView.scrollToPosition(0)
            }
        commonToolBar.ivBack.let {
            val mLayoutParams = it.layoutParams as ConstraintLayout.LayoutParams
            it.setPadding(
                0f.dip2px(requireContext()),
                0f.dip2px(requireContext()),
                0f.dip2px(requireContext()),
                0f.dip2px(requireContext())
            )
            mLayoutParams.marginStart = 15f.dip2px(requireContext())
            it.shapeAppearanceModel = ShapeAppearanceModel.builder()
                .setAllCornerSizes(ShapeAppearanceModel.PILL)
                .build()
        }
        val userInfo = getUserInfo()
        Glide.with(this)
            .load(
                userInfo?.userImage
                    ?: "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F39%2Fb7%2F53%2F39b75357f98675e2d6d5dcde1fb805a3.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642840086&t=2a7574a5d8ecc96669ac3e050fe4fd8e"
            )
            .error(R.drawable.iv_image_error)
            .placeholder(R.drawable.iv_image_placeholder)
            .into(commonToolBar.ivBack)

        commonToolBar.imageAddCallBack = object : CommonToolBar.ImageAddCallBack {
            override fun imageAddClickListener() {
                if (getDefaultMMKV().decodeInt(
                        AppConstant.Constant.LOGIN_STATUS,
                        -1
                    ) == AppConstant.Constant.LOGIN_NO_PERMISSION
                ) {
                    buildARouterLogin(mContext)
                    return
                }
                registerForActivityResult.launch(
                    Intent(requireContext(), AddDynamicActivity::class.java)
                )
            }
        }
        commonToolBar.imageBackCallBack = object : CommonToolBar.ImageBackCallBack {
            override fun imageBackClickListener() {
                (activity as MainActivity).drawerLayout.openDrawer(GravityCompat.START)
            }
        }


    }

    private fun initSmartRefreshLayout() {
        smartRefreshLayout.setOnRefreshLoadMoreListener(this)
    }

    override fun initUIChangeLiveData(): UIChangeLiveData? {
        return mainViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }



    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = MAdapter(mutableListOf).apply {
            setOnItemChildClickListener { _, view, _ ->
                when (view.id) {
                    R.id.siv_img -> {
                        buildARouter(AppConstant.RoutePath.MINE_OTHER_PERSON_INFO_ACTIVITY).withString(
                            AppConstant.Constant.ID,
                            ""
                        ).navigation()
                    }
                }
            }

            setOnItemClickListener { _, _, position ->
                val item = mAdapter.getItem(position)
                buildARouter(AppConstant.RoutePath.DYNAMIC_DETAIL_ACTIVITY)
                    .withString(AppConstant.Constant.ID, item?.id)
                    .navigation()
            }
        }
        recyclerView.setOnClickListener {
            Log.i(TAG, "initRecyclerView: ")
        }
        recyclerView.adapter = mAdapter
        registerRefreshAndRecyclerView(smartRefreshLayout, mAdapter)
    }

    inner class MAdapter(list: MutableList<DynamicData>) :
        BaseMultiItemQuickAdapter<DynamicData, BaseViewHolder>(list) {

        init {
            addItemType(AppConstant.Constant.ITEM_CONTENT, R.layout.view_dynamic_item)
            addItemType(AppConstant.Constant.ITEM_AD, R.layout.item_ad)
        }

        override fun convert(helper: BaseViewHolder, item: DynamicData) {
            when (item.mItemType) {
                AppConstant.Constant.ITEM_CONTENT -> {
                    initItemMainTitle(helper, item)

                    if (item.content.isNullOrEmpty()) {
                        helper.setGone(R.id.item_main_content_text, false)
                    } else {
                        helper.setGone(R.id.item_main_content_text, true)
                        initItemMainContentText(helper, item)
                    }

                    if (item.imageUrls.isNullOrEmpty()) {
                        helper.setGone(R.id.mRecyclerView, false)
                    } else {
                        helper.setGone(R.id.mRecyclerView, true)
                        initItemMainContentImage(helper, item)
                    }
                    if (item.imageUrls.isNullOrEmpty()) {
                        helper.setGone(R.id.gridNinePictureView, false)
                    } else {
                        helper.setGone(R.id.gridNinePictureView, true)
                        initItemMainContentImage(helper, item)
                    }

                    initItemMainIdentification(helper, item)
                }
                AppConstant.Constant.ITEM_AD -> {
                    /*广告view*/
                    val adContainer = helper.getView<FrameLayout>(R.id.adContainer)
                    adContainer.removeAllViews()
                    item.mTTNativeExpressAd?.expressAdView?.let {
                        if (it.parent == null) {
                            adContainer.addView(it)
                        }
                    }
                    item.mTTNativeExpressAd?.setDislikeCallback(requireActivity(), object :
                        TTAdDislike.DislikeInteractionCallback {
                        override fun onShow() {

                        }

                        override fun onSelected(p0: Int, p1: String?, p2: Boolean) {
                            val indexOf = mData.indexOf(item)
                            this@MAdapter.remove(indexOf)
                        }

                        override fun onCancel() {

                        }

                    })
                }
            }

        }

        private fun initItemMainTitle(helper: BaseViewHolder, item: DynamicData) {
            val sivImg = helper.getView<ShapeableImageView>(R.id.siv_img)
            helper.addOnClickListener(R.id.siv_img)
            helper.setText(R.id.tv_time, item.createTime)
            Glide.with(sivImg).load(
                item.userImage
                    ?: "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F39%2Fb7%2F53%2F39b75357f98675e2d6d5dcde1fb805a3.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642840086&t=2a7574a5d8ecc96669ac3e050fe4fd8e"
            )
                .error(R.drawable.iv_image_error)
                .placeholder(R.drawable.iv_image_placeholder).into(sivImg)
        }

        private fun initItemMainContentText(helper: BaseViewHolder, item: DynamicData) {
            helper.setText(R.id.tv_text, item.content)
        }

        private fun initItemMainContentImage(helper: BaseViewHolder, item: DynamicData) {

            val gridNinePictureView = helper.getView<GridNinePictureView>(R.id.gridNinePictureView)
            gridNinePictureView.data = item.imageUrls?.symbolToList("#")!!
            gridNinePictureView.imageCallback = object : GridNinePictureView.ImageCallback {
                override fun imageClickListener(position: Int) {
                    val imageViewPagerDialog =
                        ImageViewPagerDialog(
                            requireContext(),
                            item.imageUrls?.symbolToList("#")!!,
                            position,
                            true
                        )
                    XPopup.Builder(requireContext()).asCustom(imageViewPagerDialog).show()
                }

            }
        }
//        private fun initItemMainContentImage(helper: BaseViewHolder, item: DynamicData) {
//            val mRecyclerView = helper.getView<RecyclerView>(R.id.mRecyclerView)
//            mRecyclerView.layoutManager = GridLayoutManager(mContext, 3)
//            val dynamicAdapter = DynamicAdapter(
//                R.layout.view_item_grid_nine_picture,
//                item.imageUrls?.symbolToList("#")!!
//            )
//
//            dynamicAdapter.setOnItemClickListener { adapter, view, position ->
//                val imageViewPagerDialog =
//                    ImageViewPagerDialog(
//                        requireContext(),
//                        item.imageUrls?.symbolToList("#")!!,
//                        position,
//                        true
//                    )
//                XPopup.Builder(requireContext()).asCustom(imageViewPagerDialog).show()
//            }
//            mRecyclerView.adapter = dynamicAdapter
//        }

        private fun initItemMainIdentification(helper: BaseViewHolder, item: DynamicData) {

        }
    }

    private fun getDynamicList() {
        val mutableMapOf = mutableMapOf<String, String>()
        mutableMapOf[AppConstant.Constant.PAGE_NUMBER] = pageNum.toString()
        mutableMapOf[AppConstant.Constant.PAGE_SIZE] =
            AppConstant.Constant.PAGE_SIZE_COUNT.toString()
        mainViewModel.getDynamicList(mutableMapOf)
    }


    override fun onLoadMore(refreshLayout: RefreshLayout) {
        pageNum++
        getDynamicList()
        mainViewModel.loadMainAd()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageNum = 1
        getDynamicList()
        mainViewModel.loadMainAd()
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