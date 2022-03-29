package com.yang.module_main.ui.main.activity

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.data.MediaInfoBean
import com.yang.lib_common.dialog.ImageViewPagerDialog
import com.yang.lib_common.dialog.SearchRecyclerViewDialog
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.*
import com.yang.lib_common.widget.CommonToolBar
import com.yang.module_main.R
import com.yang.module_main.adapter.PictureSelectAdapter
import com.yang.module_main.data.model.DynamicData
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.act_add_dynamic.*
import kotlinx.android.synthetic.main.act_add_dynamic.commonToolBar
import kotlinx.android.synthetic.main.act_add_dynamic.recyclerView
import kotlinx.android.synthetic.main.act_add_dynamic.tv_location
import kotlinx.android.synthetic.main.act_upload.*
import kotlinx.android.synthetic.main.fra_left.*
import java.util.*

@Route(path = AppConstant.RoutePath.ADD_DYNAMIC_ACTIVITY)
class AddDynamicActivity : BaseActivity() {

    @InjectViewModel
    lateinit var mainViewModel: MainViewModel

    private lateinit var pictureSelectAdapter: PictureSelectAdapter

    private var data: MutableList<MediaInfoBean> = mutableListOf()

    private lateinit var imageView:ImageView


    override fun getLayout(): Int {
        return R.layout.act_add_dynamic
    }

    override fun initData() {
    }

    override fun initView() {
        commonToolBar.tVRightCallBack = object : CommonToolBar.TVRightCallBack {
            override fun tvRightClickListener() {
                if (pictureSelectAdapter.data.isEmpty()){
                    if (!TextUtils.isEmpty(et_dynamic.text.toString())){
                        addDynamic()
                    }else{
                        showShort("要说些什么才能发表哦")
                    }
                    return
                }
                uploadFile(pictureSelectAdapter.data)
            }
        }

        tv_location.clicks().subscribe {
            val searchRecyclerViewDialog = SearchRecyclerViewDialog(this)
            searchRecyclerViewDialog.searchRecyclerViewDialogCallBack = object : SearchRecyclerViewDialog.SearchRecyclerViewDialogCallBack{
                override fun getText(s: String) {
                    tv_location.text = s
                }
            }
            XPopup.Builder(this).asCustom(searchRecyclerViewDialog).show()
        }
        tv_visibility.clicks().subscribe {
            XPopup.Builder(this).asBottomList("", arrayOf("公开","仅自己")
            ) { position, text ->
                tv_visibility.text = text
            }.show()
        }

        initRecyclerView()
    }

    private fun addDynamic() {
        val dynamicData = DynamicData()
        dynamicData.userId = getUserInfo()?.id
        dynamicData.content = et_dynamic.text.toString()
        dynamicData.imageUrls = mainViewModel.pictureListLiveData.value?.formatWithSymbol("#")
        mainViewModel.addDynamic(dynamicData)
    }

    private fun uploadFile(data: MutableList<MediaInfoBean>) {
        mainViewModel.uploadFile(data)
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mainViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
        mainViewModel.pictureListLiveData.observe(this, Observer {
            addDynamic()
        })
    }



    private fun initRecyclerView() {
        pictureSelectAdapter = PictureSelectAdapter(R.layout.item_picture_select,data,false)
        recyclerView.adapter = pictureSelectAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        val registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK){
                    val mediaInfoBeans = it.data?.getParcelableArrayListExtra<MediaInfoBean>(AppConstant.Constant.DATA)
                    if (mediaInfoBeans?.size!! >= 9){
                        imageView.visibility = View.GONE
                    }else{
                        imageView.visibility = View.VISIBLE
                    }
                    pictureSelectAdapter.setNewData(mediaInfoBeans)
                }
            }
        imageView = ImageView(this).apply {
            setImageResource(R.drawable.iv_add)
            scaleType = ImageView.ScaleType.CENTER_CROP
            setBackgroundResource(android.R.color.darker_gray)
            val i =
                (getScreenPx(this@AddDynamicActivity)[0] - 20f.dip2px(this@AddDynamicActivity)) / 3
            layoutParams = ViewGroup.LayoutParams(i, i)
            setOnClickListener {
                registerForActivityResult.launch(Intent(this@AddDynamicActivity,PictureSelectActivity::class.java)
                    .putParcelableArrayListExtra(AppConstant.Constant.DATA, pictureSelectAdapter.data as ArrayList
                    ))

            }
        }
        pictureSelectAdapter.addFooterView(imageView)
        pictureSelectAdapter.setOnItemLongClickListener { adapter, view, position ->
            pictureSelectAdapter.remove(position)
            if (!imageView.isVisible) {
                imageView.visibility = View.VISIBLE
            }
            return@setOnItemLongClickListener false
        }
        pictureSelectAdapter.setOnItemClickListener { adapter, view, position ->
            val imageList =(adapter.data as MutableList<MediaInfoBean>).map {
                it.filePath
            } as MutableList<String>
            val imageViewPagerDialog =
                ImageViewPagerDialog(this, imageList , position)
            XPopup.Builder(this).asCustom(imageViewPagerDialog).show()
        }
    }


}