package com.yang.module_main.ui.main.activity

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.adapter.CommentAdapter
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.dialog.ImageViewPagerDialog
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.*
import com.yang.module_main.R
import com.yang.module_main.adapter.DynamicAdapter
import com.yang.module_main.data.model.DynamicData
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.act_dynamic_detail.*

/**
 * @Author Administrator
 * @ClassName DynamicDetailActivity
 * @Description
 * @Date 2021/8/2 17:19
 */
@Route(path = AppConstant.RoutePath.DYNAMIC_DETAIL_ACTIVITY)
class DynamicDetailActivity : BaseActivity() {

    @InjectViewModel
    lateinit var mainViewModel: MainViewModel


    private var id:String? = ""

    override fun getLayout(): Int {

        return R.layout.act_dynamic_detail
    }

    override fun initData() {
        id = intent.getStringExtra(AppConstant.Constant.ID)
        getDynamicDetail()
        mainViewModel.dynamicListLiveData.observe(this, Observer {
            val dynamicData = it[0]
            initItemMainContentImage(dynamicData)
            initItemMainContentText(dynamicData)
        })
    }

    private fun initItemMainContentText(item: DynamicData) {
        tv_task_title.text = item.taskTitle
        tv_task_content.text = item.taskContent
        tv_task_shop.text = item.taskShop
        tv_task_link.text = item.taskLink
        tv_task_key_word.text = item.taskKeyword
        tv_task_number.text = "${item.taskNumber}"
        tv_task_price.text = item.taskPrice
        tv_task_commission.text = item.taskCommission
        icv_advance_payment.rightContent = if (item.taskPayUser == 0) "是" else "否"
    }

    private fun initItemMainContentImage(item: DynamicData) {
        recyclerView.layoutManager = GridLayoutManager(this@DynamicDetailActivity,3)
        val dynamicAdapter = DynamicAdapter(
            R.layout.view_item_grid_nine_picture,
            item.imageUrls?.symbolToList("#")!!
        )
        recyclerView.adapter = dynamicAdapter
        dynamicAdapter.setOnItemClickListener { adapter, view, position ->
            val imageViewPagerDialog =
                ImageViewPagerDialog(
                    this@DynamicDetailActivity,
                    item.imageUrls?.symbolToList("#")!!,
                    position,
                    true
                )
            XPopup.Builder(this@DynamicDetailActivity).asCustom(imageViewPagerDialog).show()
        }
    }

    override fun initView() {
        initRecyclerView()

    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mainViewModel.uC
    }


    private fun getDynamicDetail() {
        val mutableMapOf = mutableMapOf<String, String>()
        mutableMapOf[AppConstant.Constant.ID] = id?:""
        mainViewModel.getDynamicDetail(mutableMapOf)
    }


    private fun initRecyclerView() {


    }
}