package com.yang.module_main.ui.menu.fragment

import android.annotation.SuppressLint
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.amap.api.location.AMapLocation
import com.bumptech.glide.Glide
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.fragment.BaseFragment
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.util.*
import com.yang.module_main.R
import com.yang.module_main.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fra_left.*

@Route(path = AppConstant.RoutePath.LEFT_FRAGMENT)
class LeftFragment : BaseFragment() {


    @InjectViewModel
    lateinit var mainViewModel: MainViewModel

    override fun getLayout(): Int {
        return R.layout.fra_left
    }

    override fun initData() {
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        val userInfo = getUserInfo()
        userInfo.let {
            Glide.with(this)
                .load(it?.userImage ?: "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fedpic%2F39%2Fb7%2F53%2F39b75357f98675e2d6d5dcde1fb805a3.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1642840086&t=2a7574a5d8ecc96669ac3e050fe4fd8e")
                .error(R.drawable.iv_image_error)
                .placeholder(R.drawable.iv_image_placeholder).into(siv_head)
            if (getDefaultMMKV().decodeInt(AppConstant.Constant.LOGIN_STATUS, -1) == AppConstant.Constant.LOGIN_SUCCESS){
                tv_name.text = it?.userName ?: "修改一下昵称吧"
            }else{
                tv_name.text = "点击登录"
            }
            tv_account.text = it?.userAccount
            tv_location.text = it?.userLocationAddress
            tv_desc.text = it?.userDescribe?:"人生在世总要留点什么吧..."
            tv_vip_level.text = "vip${it?.userVipLevel?:""}"
        }



        LocationUtil(requireContext()).apply {
            lifecycle.addObserver(this)
            locationListener = object : LocationUtil.LocationListener {
                override fun onLocationListener(aMapLocation: AMapLocation) {
                    tv_location.text = aMapLocation.city + aMapLocation.district + aMapLocation.aoiName
                    userInfo?.let {
                        it.userLocationAddress = tv_location.text.toString()
                        updateUserInfo(it)
                    }
                }

            }
        }.startLocation()


        lifecycle.addObserver(isv_image)

        cl_user_info.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.MINE_OTHER_PERSON_INFO_ACTIVITY).withString(
                AppConstant.Constant.ID,
                userInfo?.id
            ).navigation()
        }
        tv_open_vip.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.OPEN_VIP_ACTIVITY).navigation()
        }
        tv_my_push.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.MY_PUSH_ACTIVITY).navigation()
        }
        tv_my_collection.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.MY_COLLECTION_ACTIVITY).withString(
                AppConstant.Constant.NAME,
                "我的收藏"
            ).withString(
                AppConstant.Constant.TYPE,
                AppConstant.Constant.COLLECT
            ).navigation()
        }
        tv_my_down.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.MY_COLLECTION_ACTIVITY).withString(
                AppConstant.Constant.NAME,
                "我的下载"
            ).withString(
                AppConstant.Constant.TYPE,
                AppConstant.Constant.DOWNLOAD
            ).navigation()
        }
        tv_privacy.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.PRIVACY_ACTIVITY).navigation()
        }
        tv_accessibility.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.ACCESSIBILITY_ACTIVITY).navigation()
        }
        tv_setting.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.SETTING_ACTIVITY).navigation()
        }
        tv_about_and_help.clicks().subscribe {
            buildARouter(AppConstant.RoutePath.ABOUT_AND_HELP_ACTIVITY).navigation()
        }

        tv_login_out.clicks().subscribe {
            mainViewModel.loginOut()
        }
    }

    override fun initViewModel() {
        InjectViewModelProxy.inject(this)
        mainViewModel.uC.requestSuccessEvent.observe(this, Observer {
            getDefaultMMKV().encode(AppConstant.Constant.LOGIN_STATUS, -1)
            getDefaultMMKV().clearAll()
            removeAllActivity()
            buildARouter(AppConstant.RoutePath.LOGIN_ACTIVITY).withOptionsCompat(
                ActivityOptionsCompat.makeCustomAnimation(
                    requireContext(), R.anim.fade_in,
                    R.anim.fade_out
                )
            ).navigation(requireActivity())
        })

    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mainViewModel.uC
    }

}