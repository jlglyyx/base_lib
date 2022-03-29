package com.yang.module_video.ui.activity

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.BaseAppDatabase
import com.yang.lib_common.room.entity.UploadTaskData
import com.yang.lib_common.upload.UploadListener
import com.yang.lib_common.upload.UploadManage
import com.yang.module_video.R
import com.yang.module_video.viewmodel.VideoViewModel
import kotlinx.android.synthetic.main.act_video_upload.*
import kotlinx.coroutines.*

/**
 * @Author Administrator
 * @ClassName UploadActivity
 * @Description
 * @Date 2021/11/19 11:08
 */
@Route(path = AppConstant.RoutePath.VIDEO_UPLOAD_TASK_ACTIVITY)
class VideoUploadTaskActivity : BaseActivity(), UploadListener {

    @InjectViewModel
    lateinit var videoViewModel: VideoViewModel


    private var videoUploadTaskAdapter: VideoUploadTaskAdapter? = null


    override fun getLayout(): Int {
        return R.layout.act_video_upload_task
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return videoViewModel.uC
    }

    override fun initData() {
        UploadManage.instance.addUploadListener(this)
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun initViewModel() {

        InjectViewModelProxy.inject(this)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        videoUploadTaskAdapter = VideoUploadTaskAdapter(R.layout.item_upload_task, mutableListOf())
        recyclerView.adapter = videoUploadTaskAdapter
        CoroutineScope(Dispatchers.IO).launch {
            val queryData = BaseAppDatabase.instance.uploadTaskDao().queryData()
            videoUploadTaskAdapter?.replaceData(queryData)
        }
        videoUploadTaskAdapter?.setOnItemChildClickListener { adapter, view, position ->
            val item = videoUploadTaskAdapter?.getItem(position)
            if (view.id == R.id.tv_progress) {
                when (item?.status) {
                    0 -> {
                        /*在下载 点击暂停*/
                        CoroutineScope(Dispatchers.IO).launch {
                            item.status = 2
                            val queryData =
                                BaseAppDatabase.instance.uploadTaskDao().queryData(item.id)
                            BaseAppDatabase.instance.uploadTaskDao().updateData(queryData)
                            withContext(Dispatchers.Main){
                                videoUploadTaskAdapter?.notifyItemChanged(position)
                            }
                        }
                        UploadManage.instance.cancelUpload(item.id)
                    }
                    1 -> {
                        /*已完成 不能点击*/

                    }
                    2 -> {
                        /*已暂停 点击继续*/
                        CoroutineScope(Dispatchers.IO).launch {
                            item.status = 0
                            val queryData =
                                BaseAppDatabase.instance.uploadTaskDao().queryData(item.id)
                            BaseAppDatabase.instance.uploadTaskDao().updateData(queryData)
                            withContext(Dispatchers.Main){
                                videoUploadTaskAdapter?.notifyItemChanged(position)
                            }
                        }
                        UploadManage.instance.resumeUpload(item.id)
                    }
                }

            }

        }
    }

    inner class VideoUploadTaskAdapter(layoutResId: Int, data: MutableList<UploadTaskData>?) :
        BaseQuickAdapter<UploadTaskData, BaseViewHolder>(layoutResId, data) {
        override fun convert(helper: BaseViewHolder, item: UploadTaskData) {
            when (item.status ){
                0 ->{
                    helper.setText(R.id.tv_progress, "${item.progress}")
                }
                1 ->{
                    helper.setText(R.id.tv_progress, "已完成")
                }
                2 ->{
                    helper.setText(R.id.tv_progress, "继续下载")
                }
            }
            helper.setText(R.id.tv_content, item.filePath)
                .addOnClickListener(R.id.tv_progress)
        }
    }

    override fun onProgress(id: String, progress: Int) {
        videoUploadTaskAdapter?.let {
            it.data.findLast { bean ->
                bean.id == id
            }?.apply {
                if (status == 1) {
                    return
                }
                this.progress = progress
                if (!recyclerView.isComputingLayout) {
                    CoroutineScope(Dispatchers.Main).launch {
                        it.notifyItemChanged(it.data.indexOf(this@apply))
                    }
                }
                Log.i(TAG, "onProgress: $id  $progress")
            }
        }

    }

    override fun onSuccess(id: String) {

    }

    override fun onFailed(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            videoUploadTaskAdapter?.let {
                it.data.findLast { bean ->
                    bean.id == id
                }.apply {
                    val queryData = BaseAppDatabase.instance.uploadTaskDao().queryData(id)
                    withContext(Dispatchers.Main){
                        it.setData(it.data.indexOf(this@apply),queryData)
                    }
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        UploadManage.instance.removeUploadListener(this)
    }
}