package com.yang.module_mine.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.lxj.xpopup.XPopup
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.data.MediaInfoBean
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.entity.UserInfoData
import com.yang.lib_common.util.buildARouter
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.getUserInfo
import com.yang.lib_common.widget.CommonToolBar
import com.yang.module_mine.R
import com.yang.module_mine.adapter.MineActivityInfoAdapter
import com.yang.module_mine.data.MineActivityInfoData
import com.yang.module_mine.viewmodel.MineViewModel
import kotlinx.android.synthetic.main.act_change_user_info.*
import kotlinx.android.synthetic.main.act_other_person_info.commonToolBar


/**
 * @Author Administrator
 * @ClassName ChangeUserInfoActivity
 * @Description 修改信息
 * @Date 2021/8/27 14:53
 */
@Route(path = AppConstant.RoutePath.MINE_CHANGE_USER_INFO_ACTIVITY)
class MineChangeUserInfoActivity : BaseActivity() {

    @InjectViewModel
    lateinit var mineViewModel: MineViewModel

    private lateinit var mineActivityInfoAdapter: MineActivityInfoAdapter

    private var imageUrl = ""

    private var selectSex = ""

    private var sexArray = arrayOf("男", "女")

    private var url: String? = null


    override fun getLayout(): Int {
        return R.layout.act_change_user_info
    }

    override fun initData() {
        initRecyclerView()
        val userInfo = getUserInfo()

        userInfo.let {
            Glide.with(this).load(
                userInfo?.userImage
                    ?: "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F39%2Fb7%2F53%2F39b75357f98675e2d6d5dcde1fb805a3.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642840086&t=2a7574a5d8ecc96669ac3e050fe4fd8e"
            ).error(R.drawable.iv_image_error)
                .placeholder(R.drawable.iv_image_placeholder).into(siv_image)
            et_name.setText(it?.userName?:"修改一下昵称吧")
            tv_sex.text = "${it?.userSex}"
            et_desc.setText(it?.userDescribe?:"人生在世总要留点什么吧...")
        }



        mineViewModel.mUserInfoData.observe(this, Observer {
            finish()
        })
        mineViewModel.pictureListLiveData.observe(this, Observer {
            url = it[0]
        })

    }

    override fun initView() {
        commonToolBar.tVRightCallBack = object : CommonToolBar.TVRightCallBack {
            override fun tvRightClickListener() {
                val userInfoData = UserInfoData(
                    "",
                    null,
                    et_name.text.toString(),
                    null,
                    null,
                    null,
                    url,
                    "",
                    "",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    0,
                    false,
                    0,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
                )
                mineViewModel.changeUserInfo(userInfoData)
            }

        }

        ll_sex.clicks().subscribe {
            XPopup.Builder(this).asBottomList(
                "", sexArray
            ) { position, text ->
                selectSex = sexArray[position]
                tv_sex.text = selectSex
            }.show()
        }
        val registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && null != it.data) {
                    it.data!!.getParcelableArrayListExtra<MediaInfoBean>(AppConstant.Constant.DATA)
                        ?.let { beans ->
                            if (beans.isNotEmpty()){
                                imageUrl = beans[0].filePath.toString()
                                mineViewModel.uploadFile(mutableListOf(imageUrl))
                                Glide.with(this).load(imageUrl).centerCrop()
                                    .error(R.drawable.iv_image_error)
                                    .placeholder(R.drawable.iv_image_placeholder).into(siv_image)
                            }
                        }
                }
            }


        ll_image.clicks().subscribe {
            val forName =
                Class.forName("com.yang.module_main.ui.main.activity.PictureSelectActivity")
            val intent = Intent(this, forName)
            intent.putExtra(AppConstant.Constant.TYPE, AppConstant.Constant.NUM_ONE)
            intent.putExtra(AppConstant.Constant.NUM, AppConstant.Constant.NUM_ONE)
            registerForActivityResult.launch(intent)
        }
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mineViewModel.uC
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
    }

    private fun initRecyclerView() {
        mineActivityInfoAdapter = MineActivityInfoAdapter(
            R.layout.item_activity_info,
            mutableListOf<MineActivityInfoData>().apply {
                add(MineActivityInfoData("头像", AppConstant.RoutePath.ADD_DYNAMIC_ACTIVITY))
                add(MineActivityInfoData("签名", AppConstant.RoutePath.ADD_DYNAMIC_ACTIVITY))
                add(MineActivityInfoData("昵称", AppConstant.RoutePath.ADD_DYNAMIC_ACTIVITY))
                add(MineActivityInfoData("性别", AppConstant.RoutePath.ADD_DYNAMIC_ACTIVITY))
                add(MineActivityInfoData("职业", AppConstant.RoutePath.ADD_DYNAMIC_ACTIVITY))
                add(MineActivityInfoData("公司", AppConstant.RoutePath.ADD_DYNAMIC_ACTIVITY))
            })
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = mineActivityInfoAdapter

        mineActivityInfoAdapter.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as MineActivityInfoData
            buildARouter(item.value).navigation()
        }
    }
}