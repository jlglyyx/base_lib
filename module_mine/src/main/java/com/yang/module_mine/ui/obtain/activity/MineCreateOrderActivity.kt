package com.yang.module_mine.ui.obtain.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.yang.apt_annotation.annotain.InjectViewModel
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.bus.event.UIChangeLiveData
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.proxy.InjectViewModelProxy
import com.yang.lib_common.room.BaseAppDatabase
import com.yang.lib_common.room.entity.MineGoodsDetailData
import com.yang.lib_common.util.clicks
import com.yang.lib_common.util.formatDate_YYYYMMMDDHHMMSS
import com.yang.lib_common.util.simpleDateFormat
import com.yang.lib_common.widget.CommonToolBar
import com.yang.module_mine.R
import com.yang.module_mine.data.MineShippingAddressData
import com.yang.module_mine.viewmodel.MineViewModel
import kotlinx.android.synthetic.main.act_mine_create_order_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.*

/**
 * @Author Administrator
 * @ClassName MineOrderDetailActivity
 * @Description
 * @Date 2021/10/11 9:38
 */
@Route(path = AppConstant.RoutePath.MINE_CREATE_ORDER_ACTIVITY)
class MineCreateOrderActivity : BaseActivity() {

    @InjectViewModel
    lateinit var mineViewModel: MineViewModel

    private lateinit var registerForActivityResult: ActivityResultLauncher<Intent>

    override fun getLayout(): Int {
        return R.layout.act_mine_create_order_detail
    }

    override fun initData() {
        createOrder()
        registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK && it.data != null){
                    val mineShippingAddressData = it.data?.getParcelableExtra<MineShippingAddressData>(AppConstant.Constant.DATA)
                    mineShippingAddressData?.let { bean ->
                        val spannableString = SpannableString(bean.name + "-" + bean.phone)
                        spannableString.setSpan(ForegroundColorSpan(ActivityCompat.getColor(this,R.color.black)),0,bean.name?.length!!, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        spannableString.setSpan(AbsoluteSizeSpan(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,13f,resources.displayMetrics).toInt()),bean.name?.length!!,spannableString.length,Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                        tv_phone.text = spannableString
                        tv_address.text = bean.address
                        tv_phone.visibility = View.VISIBLE
                        tv_address.visibility = View.VISIBLE
                        tv_choose_address.visibility = View.GONE
                    }
                }
            }
    }

    override fun initView() {
        Glide.with(this)
            .load("https://img.alicdn.com/bao/uploaded/i2/2209667639897/O1CN015Oh5X32MysY6ea4fc_!!0-item_pic.jpg_200x200q90.jpg_.webp")
            .into(iv_image)
        cv_address.clicks().subscribe {
            registerForActivityResult.launch(Intent(this@MineCreateOrderActivity,MineAddressActivity::class.java))
        }

        commonToolBar.tVRightCallBack = object : CommonToolBar.TVRightCallBack{
            override fun tvRightClickListener() {
                mineViewModel.exchangeGoods()
//                lifecycleScope.launch(Dispatchers.IO) {
//                    BaseAppDatabase.instance.mineGoodsDetailDao().insertData(MineGoodsDetailData(UUID.randomUUID().toString().replace("-",""), 2, "==========================="))
//                    withContext(Dispatchers.Main){
//                        showShort("兑换成功")
//                        finish()
//                    }
//                }

            }
        }
    }

    override fun initViewModel() {

        InjectViewModelProxy.inject(this)
    }

    override fun initUIChangeLiveData(): UIChangeLiveData {
        return mineViewModel.uC
    }

    @SuppressLint("SetTextI18n")
    private fun createOrder(){
        val date = Date()
        var hashCode =  UUID.randomUUID().toString().hashCode()
        if (hashCode < 0){
            hashCode = -hashCode
        }
        val format = String.format("%015d", hashCode)
        tv_good_code.text = "订单编号：${formatDate_YYYYMMMDDHHMMSS.format(date)}$format"
        tv_create_time.text = "创建时间：${simpleDateFormat.format(date)}"
        lifecycleScope.launch(Dispatchers.IO) {
            BaseAppDatabase.instance.mineGoodsDetailDao().insertData(MineGoodsDetailData(UUID.randomUUID().toString().replace("-",""), 1, "${formatDate_YYYYMMMDDHHMMSS.format(date)}$format"))
        }
        mineViewModel.createGoods()
    }



    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }
}