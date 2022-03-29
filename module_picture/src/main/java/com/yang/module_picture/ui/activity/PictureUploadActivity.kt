package com.yang.module_picture.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.data.MediaInfoBean
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.upload.UploadService
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.dip2px
import com.yang.lib_common.util.showShort
import com.yang.lib_common.widget.CommonToolBar
import com.yang.module_picture.R
import com.yang.module_picture.viewmodel.PictureViewModel
import kotlinx.android.synthetic.main.act_picture_upload.*
import java.util.*

/**
 * @Author Administrator
 * @ClassName UploadActivity
 * @Description
 * @Date 2021/11/19 11:08
 */
@Route(path = AppConstant.RoutePath.PICTURE_UPLOAD_ACTIVITY)
class PictureUploadActivity : BaseActivity() {

    @InjectViewModel
    lateinit var pictureViewModel: PictureViewModel

    private var selectType = ""

    private var selectFileList = mutableListOf<MediaInfoBean>()

    private val FILE_CODE = 1002

    private var pictureTypeList = mutableListOf<String>()

    private lateinit var pictureUploadAdapter:PictureUploadAdapter

    private var uploadServiceBinder: UploadService.UploadServiceBinder? = null

    private var uploadServiceConnection = object : ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            uploadServiceBinder = service as UploadService.UploadServiceBinder
        }
        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun getLayout(): Int {
        return R.layout.act_picture_upload
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return pictureViewModel.uC
    }

    override fun initData() {
        bindService(Intent(this,UploadService::class.java),uploadServiceConnection, BIND_AUTO_CREATE)
        pictureViewModel.getImageTypeData()
        pictureViewModel.mImageTypeData.observe(this, Observer {
            it?.map { bean ->
                bean.name
            }?.let { map -> pictureTypeList.addAll(map) }
        })
    }

    override fun initView() {
        icv_type.clicks().subscribe {
            val asBottomList = XPopup.Builder(this).asBottomList(
                "", pictureTypeList.toTypedArray()
            ) { position, text ->
                selectType = pictureTypeList[position]
                icv_type.rightContentFontSize = 15f
                icv_type.rightContent = selectType
            }
            asBottomList.findViewById<RecyclerView>(com.lxj.xpopup.R.id.recyclerView).let {
                it.post {
                    if(it.measuredHeight > 300f.dip2px(this)){
                        it.layoutParams.height = 300f.dip2px(this)
                    }
                }
            }
            asBottomList.show()
        }

        ll_image.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.PICTURE_SELECT_ACTIVITY)
                .withParcelableArrayList(AppConstant.Constant.DATA, selectFileList as ArrayList)
                .withInt(AppConstant.Constant.TYPE, AppConstant.Constant.NUM_ONE)
                .withInt(AppConstant.Constant.NUM, AppConstant.Constant.NUM_ONE)
                .navigation(this, FILE_CODE)
        }

        commonToolBar.tVRightCallBack = object : CommonToolBar.TVRightCallBack{
            override fun tvRightClickListener() {
                if (TextUtils.isEmpty(selectType)){
                    showShort("请选择文件类别")
                    return
                }
                if (pictureUploadAdapter.data.isEmpty()){
                    showShort("请选择文件")
                    return
                }
                uploadServiceBinder?.startUpload(pictureUploadAdapter.data[0])
                //buildARouter(AppConstant.RoutePath.VIDEO_UPLOAD_TASK_ACTIVITY).navigation()
            }
        }
        initRecyclerView()
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun initRecyclerView(){
        recyclerView.layoutManager = LinearLayoutManager(this)
        pictureUploadAdapter = PictureUploadAdapter(R.layout.item_upload,mutableListOf())
        recyclerView.adapter = pictureUploadAdapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let {
            if (requestCode == FILE_CODE && resultCode == RESULT_OK) {
                it.getParcelableArrayListExtra<MediaInfoBean>(AppConstant.Constant.DATA)
                    ?.let { beans ->
                        selectFileList = beans
                        pictureUploadAdapter.setNewData(beans.map { bean ->
                            Log.i(TAG, "onActivityResult: ${ bean.filePath}")
                            bean.filePath
                        })
                    }
            }
        }
    }

    inner class PictureUploadAdapter(layoutResId: Int, data: MutableList<String>?) :
        BaseQuickAdapter<String, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: String) {
            helper.setText(R.id.tv_content,item)
        }
    }
}