package com.yang.module_main.ui.menu.activity

import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.lxj.xpopup.XPopup
import com.yang.lib_common.base.ui.activity.BaseActivity
import com.yang.lib_common.constant.AppConstant
import com.yang.lib_common.util.simpleDateFormat
import com.yang.module_main.R
import com.yang.module_main.ui.menu.adapter.OpenVipAdapter
import com.yang.module_main.ui.menu.data.OpenVipData
import kotlinx.android.synthetic.main.act_open_vip.*
import java.util.*

/**
 * @Author Administrator
 * @ClassName OpenVipActivity
 * @Description 开通会员
 * @Date 2021/9/28 10:47
 */
@Route(path = AppConstant.RoutePath.OPEN_VIP_ACTIVITY)
class OpenVipActivity:BaseActivity() {

    lateinit var mAdapter: OpenVipAdapter

    private var dateTime = Date(System.currentTimeMillis())

    private var level = 1

    private var maxProgress = 100

    private var payTypeArray = arrayOf("微信","支付宝")

    private var selectPayType = ""

    override fun getLayout(): Int {
        return R.layout.act_open_vip
    }

    override fun initData() {
        tv_time.text = "到期时间："+simpleDateFormat.format(dateTime)
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun initViewModel() {
    }

    private fun initRecyclerView(){
        val mutableListOf = mutableListOf<OpenVipData>()
        mutableListOf.add(OpenVipData("试用会员",7,"9.9",10))
        mutableListOf.add(OpenVipData("普通会员",30,"19.9",20))
        mutableListOf.add(OpenVipData("试用会员",60,"29.9",30))
        mutableListOf.add(OpenVipData("试用会员",90,"39.9",40))
        mutableListOf.add(OpenVipData("试用会员",120,"49.9",50))
        mutableListOf.add(OpenVipData("试用会员",150,"59.9",60))

        mAdapter = OpenVipAdapter(R.layout.item_open_vip,mutableListOf)
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = GridLayoutManager(this,3)

        mAdapter.setOnItemClickListener { adapter, view, position ->

            val asBottomList = XPopup.Builder(this).asBottomList(
                "", payTypeArray
            ) { position, text ->
                selectPayType = payTypeArray[position]
                val item = adapter.getItem(position) as OpenVipData
                tv_time.text = setTime(item.time)
                setMProgress(item.experience)
            }
            asBottomList.show()
        }
    }

    private fun setTime(day:Int):String{
        val instance = Calendar.getInstance()
        instance.time = dateTime
        instance.add(Calendar.DATE,day)
        dateTime = instance.time
        return "到期时间：${simpleDateFormat.format(instance.time)}"
    }
    private fun setMProgress(progress:Int){
        val mProgress = prb_vip.progress + progress
        if (mProgress >= prb_vip.max){
            tv_level.text = "当前等级：vip${level++}"
            prb_vip.progress = mProgress - prb_vip.max
            maxProgress = level*100
            prb_vip.max = maxProgress
        }else{
            prb_vip.progress = mProgress
        }
        tv_progress.text = "下一级进度：${prb_vip.progress}/$maxProgress"
    }
}