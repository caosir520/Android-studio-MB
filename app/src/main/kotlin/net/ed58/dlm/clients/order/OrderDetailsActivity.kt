package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.wise.common.baseapp.AppManager
import kotlinx.android.synthetic.main.activity_order.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.entity.OrderM

/**
 * Created by Administrator on 2017/11/10/010.
 *
 * 订单详情的activity
 */

class OrderDetailsActivity : BaseCoreMVPActivity<OrderDetailsPresent,OrderDetailsPresent.UI>() ,OrderDetailsPresent.UI {
    override fun showNoDetails(deta: String) {

    }

    override fun showDetails(date: OrderM) {
        //拼接数据
        cardView.visibility = View.VISIBLE
        order_number.text=date.orderNo //订单号
        order_class.text=date.categoryName //种类
        order_class_money.text =date.cargoAmount // 种类价值
        order_time.text=date.expectedFetchTime //取件时间
        order_money.text=""+date.orderAmount+"元"; //总花费
        order_details_insured.text=""+date.insuranceFee+"元保价" // 保价信息
        send.text = date.fetchAddress +date.fetchFloorHouseNum//发送地址
        send_name_tel.text = date.fetchName+"  "+date.fetchPhone //发送人信息
        tv_re.text=date.receiverAddress+date.receiverFloorHouseNum; //收件人地址
        tv_re_name.text =date.receiverName+"  "+date.receiverPhone // 收件人信息
        order_details_cancel_reason.text="个人原因"
        when(date.orderStatus){
            -300 ,-200, -100 -> {
                VisibiltyCancle()
                order_details_cancel_people.text="寄件人"
                order_status.text="已取消"
                order_details_cancel_reason1.text=date.canceledReason//取消订单原因

            }
            -1->{
                VisibiltyCancle()
                order_details_cancel_people.text="系统"
                order_details_cancel_reason.text="系统原因"
                order_status.text="已取消"
                order_details_cancel_reason1.text=date.canceledReason//取消订单原因
            }
            400->  order_status.text="已接单"
            500->    order_status.text="配送中"
            600,700-> order_status.text="已完成"
            0-> order_status.text="待支付"
            300-> order_status.text="待接单"
            -500-> order_status.text="签收失败"
            -400-> order_status.text="取货失败"
            100-> order_status.text="待支付"
            200-> order_status.text="支付失败"
        }

    }
    //显示取消条目
    fun VisibiltyCancle(){
        v_cut_of2.visibility=View.VISIBLE
        textView7.visibility=View.VISIBLE
        order_details_cancel_reason1.visibility=View.VISIBLE
    }



    override fun createPresenter(): OrderDetailsPresent {
        return  OrderDetailsPresent()
    }

    override fun getUi(): OrderDetailsPresent.UI {
        return this
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar.navigationIcon=resources.getDrawable(R.mipmap.icon_back)
        toolbar.title="订单详情"
        toolbar.titleMarginStart=(toolbar.x/2).toInt()
        toolbar.setNavigationOnClickListener {
            AppManager.getAppManager().finishActivity()
        }
        presenter.getOrderById(intent.getStringExtra("id"))
    }

    //静态方法调用
    companion object {
        fun startActivity(context:Activity,id:String){
            val intent = Intent(context,OrderDetailsActivity::class.java)
            intent.putExtra("id",id)
            context.startActivity(intent)
        }
    }


}
