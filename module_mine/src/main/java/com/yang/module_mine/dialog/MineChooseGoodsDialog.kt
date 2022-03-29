package com.yang.module_mine.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.lxj.xpopup.core.BottomPopupView
import com.yang.lib_common.util.clicks
import com.yang.module_mine.R
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.dialog_mine_choose_goods.view.*

/**
 * @Author Administrator
 * @ClassName MineChooseGoodsDialog
 * @Description
 * @Date 2022/1/6 9:28
 */
class MineChooseGoodsDialog(context: Context) : BottomPopupView(context) {

    private lateinit var flowLayoutAdapter: FlowLayoutAdapter

    private var list = mutableListOf<String>()

    var clickListener: ClickListener? = null

    private var sellNum = 1

    interface ClickListener {

        fun onClick(data: String)
    }

    override fun onCreate() {
        super.onCreate()
        list.apply {
            add("李四")
            add("王二 水水水水水水水水吉林集安了")
            add("张三水水水水水水水水")
            add("张三水水水水水水水水")
            add("张三水水水水水水水水")
            add("张三水水水水水水水水")
        }
        flowLayoutAdapter = FlowLayoutAdapter(list)
        flowLayout.adapter = flowLayoutAdapter


        tv_add.setOnClickListener {
            tv_num.text = "${++sellNum}"
        }
        tv_reduce.setOnClickListener {
            if (sellNum > 1) {
                tv_num.text = "${--sellNum}"
            }
        }
        tv_confirm.clicks().subscribe {
            clickListener?.onClick("开始兑换")
        }
    }


    override fun getImplLayoutId(): Int {
        return R.layout.dialog_mine_choose_goods
    }

    inner class FlowLayoutAdapter(mData: MutableList<String>) : TagAdapter<String>(mData) {
        override fun getView(parent: FlowLayout, position: Int, t: String): View {
            val view = LayoutInflater.from(context)
                .inflate(R.layout.item_flow_layout, null, false)
            val tvContent = view.findViewById<TextView>(R.id.tv_content)
            val tvDelete = view.findViewById<TextView>(R.id.tv_delete)
            tvDelete.visibility = View.GONE
            tvContent.text = t
            return view
        }

    }
}