package com.yang.module_mine.ui.obtain.activity

import android.annotation.SuppressLint
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.room.BaseAppDatabase
import com.yang.lib_common.room.entity.MineGoodsDetailData
import com.yang.lib_common.util.clicks
import com.yang.module_mine.R
import kotlinx.android.synthetic.main.act_mine_create_order_detail.iv_image
import kotlinx.android.synthetic.main.act_mine_order_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * @Author Administrator
 * @ClassName MineOrderDetailActivity
 * @Description
 * @Date 2021/10/11 9:38
 */
@Route(path = AppConstant.RoutePath.MINE_ORDER_DETAIL_ACTIVITY)
class MineOrderDetailActivity : BaseActivity() {

    private var id:String = ""

    private var mineGoodsDetailData: MineGoodsDetailData? = null

    override fun getLayout(): Int {
        return R.layout.act_mine_order_detail
    }

    override fun initData() {
        intent?.apply {
            id = getStringExtra(AppConstant.Constant.ID)?:""
        }

        lifecycleScope.launch(Dispatchers.IO){
            mineGoodsDetailData = BaseAppDatabase.instance.mineGoodsDetailDao().queryDataById(id)
        }
    }

    override fun initView() {
        Glide.with(this)
            .load("https://img.alicdn.com/bao/uploaded/i2/2209667639897/O1CN015Oh5X32MysY6ea4fc_!!0-item_pic.jpg_200x200q90.jpg_.webp")
            .into(iv_image)
        createDataTimer()


        tv_cancel_order.clicks().subscribe {
            mineGoodsDetailData?.let {
                lifecycleScope.launch(Dispatchers.IO){
                    BaseAppDatabase.instance.mineGoodsDetailDao().deleteData(it.id)
                }
            }

        }

    }

    override fun initViewModel() {

    }

    @SuppressLint("SetTextI18n")
    private fun createDataTimer() {
        lifecycleScope.launch {
            for (index in 15 * 60 downTo 0) {
                val h = index / 60 % 60
                val s = index % 60
                tv_hold_order_time.text = "${String.format("%02d", h)}分${String.format("%02d", s)}秒后释放该订单"
                delay(1000)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.cancel()
    }
}