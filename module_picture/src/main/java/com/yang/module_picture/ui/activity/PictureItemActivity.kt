package com.yang.module_picture.ui.activity

import android.graphics.Rect
import android.text.TextUtils
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.lxj.xpopup.XPopup
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.CommentAdapter
import com.yang.lib_common.adapter.ImageViewPagerAdapter
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.data.CommentData
import com.yang.lib_common.dialog.EditBottomDialog
import com.yang.lib_common.dialog.ImageViewPagerDialog
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.dip2px
import com.yang.module_picture.R
import com.yang.module_picture.viewmodel.PictureViewModel
import com.youth.banner.transformer.ScaleInTransformer
import kotlinx.android.synthetic.main.act_picture_item.*
import kotlinx.android.synthetic.main.fra_item_picture.recyclerView


@Route(path = AppConstant.RoutePath.PICTURE_ITEM_ACTIVITY)
class PictureItemActivity : BaseActivity() {

    private lateinit var commentAdapter: CommentAdapter

    @InjectViewModel
    lateinit var pictureViewModel: PictureViewModel

    private var isScroll = false

    override fun getLayout(): Int {
        return R.layout.act_picture_item
    }

    override fun initData() {
        val intent = intent
        val sid = intent.getStringExtra(AppConstant.Constant.ID)
        pictureViewModel.getImageItemData(sid ?: "")
        addViewHistory()
    }

    override fun initView() {
        initRecyclerView()
        initTabLayout()
        initViewPager()
        tv_send_comment.clicks().subscribe {
            XPopup.Builder(this).autoOpenSoftInput(true).asCustom(EditBottomDialog(this).apply {
                dialogCallBack = object : EditBottomDialog.DialogCallBack {
                    override fun getComment(s: String) {
                        commentAdapter.addData(0, CommentData(0, 0).apply {
                            comment = s
                        })
                        insertComment(s)
                        commentAdapter.getViewByPosition(
                            recyclerView,
                            0,
                            com.yang.lib_common.R.id.siv_img
                        )?.let { it1 ->
                            it1.isFocusable = true
                            it1.requestFocus()
                            scrollToPosition(it1)
                        }
                        nestedScrollView.fullScroll(View.FOCUS_DOWN)
                    }

                }
            }).show()
        }

        iv_back.clicks().subscribe {
            finish()
        }
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun insertComment(comment: String) {
        val mutableMapOf = mutableMapOf<String, String>()
        mutableMapOf[AppConstant.Constant.COMMENT] = comment
        pictureViewModel.insertComment(mutableMapOf)
    }

    private fun addViewHistory() {
        pictureViewModel.addViewHistory("", "")
    }

    private fun scrollToPosition(view: View) {
        val intArray = IntArray(2)
        view.getLocationOnScreen(intArray)
        nestedScrollView.scrollTo(intArray[0], intArray[1])
    }

    override fun initUIChangeLiveData(): UIChangeLiveData? {
        return pictureViewModel.uC
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        commentAdapter =
            CommentAdapter(mutableListOf()).also {
                it.setOnItemChildClickListener { adapter, view, position ->
                    val item = commentAdapter.getItem(position)
                    item?.let {
                        when (view.id) {
                            com.yang.lib_common.R.id.siv_img -> {
                                buildARouter(AppConstant.RoutePath.MINE_OTHER_PERSON_INFO_ACTIVITY).withString(
                                    AppConstant.Constant.ID,
                                    ""
                                ).navigation()
                            }
                            com.yang.lib_common.R.id.siv_reply_img -> {
                                buildARouter(AppConstant.RoutePath.MINE_OTHER_PERSON_INFO_ACTIVITY).withString(
                                    AppConstant.Constant.ID,
                                    ""
                                ).navigation()
                            }
                            com.yang.lib_common.R.id.tv_reply -> {
                                XPopup.Builder(this@PictureItemActivity)
                                    .autoOpenSoftInput(true)
                                    .asCustom(EditBottomDialog(this@PictureItemActivity).apply {
                                        dialogCallBack = object : EditBottomDialog.DialogCallBack {
                                            override fun getComment(s: String) {
                                                when (it.itemType) {
                                                    0 -> {
                                                        it.addSubItem(CommentData(1, 1).apply {
                                                            comment = s
                                                            parentId = it.id

                                                        })
                                                        commentAdapter.collapse(position)
                                                        commentAdapter.expand(position)
                                                    }
                                                    1, 2 -> {
                                                        it.parentId?.let { mParentId ->
                                                            val mPosition =
                                                                commentAdapter.data.indexOf(
                                                                    commentAdapter.data.findLast {
                                                                        TextUtils.equals(
                                                                            it.parentId,
                                                                            mParentId
                                                                        )
                                                                    }?.apply {
                                                                        addSubItem(
                                                                            CommentData(
                                                                                1,
                                                                                2
                                                                            ).apply {
                                                                                comment = s
                                                                                parentId = mParentId
                                                                            })
                                                                    })

                                                            commentAdapter.collapse(mPosition)
                                                            commentAdapter.expand(mPosition)
                                                        }
                                                    }
                                                }
                                                insertComment(s)
                                            }

                                        }
                                    }).show()
                            }
                            else -> {

                            }
                        }
                    }
                }
            }
        recyclerView.adapter = commentAdapter


        nestedScrollView.setOnScrollChangeListener { v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            val rect = Rect()
            cl_picture.getHitRect(rect)
            isScroll = true
            if (cl_picture.getLocalVisibleRect(rect)) {
                tabLayout.getTabAt(0)?.select()
            } else {
                tabLayout.getTabAt(1)?.select()
            }
        }
    }

    private fun initViewPager() {

        val mutableListOf = mutableListOf<String>()
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("http://img.netbian.com/file/2021/1025/9e8c4ee0cdcafa7b1a6af4adf288b4a6.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("http://img.netbian.com/file/2021/1025/9e8c4ee0cdcafa7b1a6af4adf288b4a6.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("http://img.netbian.com/file/2021/0927/cb7c2da3190d6dd54f0113c3462784b3.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("http://img.netbian.com/file/2021/1025/9e8c4ee0cdcafa7b1a6af4adf288b4a6.jpg")
        mutableListOf.add("http://img.netbian.com/file/2021/0927/cb7c2da3190d6dd54f0113c3462784b3.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")
        mutableListOf.add("https://desk-fd.zol-img.com.cn/t_s1680x1050c5/g6/M00/0A/08/ChMkKV_ceSGIcOJdABOhSqd4uy8AAG8WwO_rfYAE6Fi406.jpg")

        val imageViewPagerAdapter = ImageViewPagerAdapter(mutableListOf)
        viewPager.adapter = imageViewPagerAdapter
        //viewPager.offscreenPageLimit = mutableListOf.size

        imageViewPagerAdapter.clickListener = object : ImageViewPagerAdapter.ClickListener {
            override fun onClickListener(view: View, position: Int) {
                val imageViewPagerDialog =
                    ImageViewPagerDialog(
                        this@PictureItemActivity,
                        imageViewPagerAdapter.data,
                        position,
                        true,
                        true
                    )
                imageViewPagerDialog.imageViewPagerDialogCallBack = object :
                    ImageViewPagerDialog.ImageViewPagerDialogCallBack {
                    override fun getPosition(position: Int) {
                        //viewPager.setCurrentItem(position, false)
                    }

                    override fun onViewClickListener(view: View) {

                    }

                }
                XPopup.Builder(this@PictureItemActivity).asCustom(imageViewPagerDialog).show()
            }

        }

        pictureViewModel.mImageItemData.observe(this, Observer {
            imageViewPagerAdapter.data = it.map { item ->
                item.imageUrl
            } as MutableList<String>
            imageViewPagerAdapter.notifyDataSetChanged()
        })

        val pageRecyclerView = viewPager.getChildAt(0) as RecyclerView
        pageRecyclerView.setPadding(5f.dip2px(this), 0, 5f.dip2px(this), 0)
        pageRecyclerView.clipToPadding = false
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(3f.dip2px(this)))
        compositePageTransformer.addTransformer(ScaleInTransformer())
        viewPager.setPageTransformer(compositePageTransformer)
    }


    private fun initTabLayout() {
        tabLayout.addTab(tabLayout.newTab().apply {
            view.setOnClickListener {
                isScroll = false
            }
        }.setText("图片"))
        tabLayout.addTab(tabLayout.newTab().apply {
            view.setOnClickListener {
                isScroll = false
            }
        }.setText("评论"))


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (!isScroll) {
                    if (tab?.position == 0) {
                        nestedScrollView.fullScroll(View.FOCUS_UP)
                    } else {
                        cl_picture.post {
                            nestedScrollView.scrollY = cl_picture.height
                        }
                    }
                }

            }

        })
    }


}