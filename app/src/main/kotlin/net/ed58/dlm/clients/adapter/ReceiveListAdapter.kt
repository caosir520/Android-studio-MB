package net.ed58.dlm.clients.adapter

import android.app.Activity
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import com.wise.common.commonutils.MoneyUtil
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.entity.OrderM
import net.ed58.dlm.clients.order.OrderMapDetailActivity
import net.ed58.dlm.clients.util.CommonDialogUtil
import net.ed58.dlm.clients.util.RepeatInterfaceUtils

/**
 * Created by sunpeng on 17/10/26.
 *
 */
class ReceiveListAdapter(context: Context?) : CommonRecycleViewAdapter<OrderM>(context, R.layout.item_receive_list) {


    var position: Int = -1//点击的条目位置


    override fun convert(helper: ViewHolderHelper?, orderBean: OrderM?) {
        helper?.setText(R.id.text_order_no, "货品价值${orderBean?.cargoAmount}")
        helper?.setText(R.id.text_send_address, orderBean?.fetchAddress+orderBean?.fetchFloorHouseNum)
        helper?.setText(R.id.text_receive_address, orderBean?.receiverAddress+orderBean?.receiverFloorHouseNum)
        helper?.setText(R.id.text_price, "¥" + orderBean?.orderAmount)
        helper?.setVisible(R.id.text_top_distance, true)
        helper?.setVisible(R.id.text_bottom_distance, true)
        helper?.setText(R.id.text_top_distance, MoneyUtil.MoneyFomatWithTwoPoint(orderBean?.topDistance!! / 1000) + "km")
        helper?.setText(R.id.text_bottom_distance, MoneyUtil.MoneyFomatWithTwoPoint(orderBean?.bottomDistance!! / 1000) + "km")

        val text_state = helper?.getView<TextView>(R.id.text_state)
        val span = SpannableString("发单人评分${orderBean?.asSenderRating!!}")
        span.setSpan(ForegroundColorSpan(mContext.resources.getColor(R.color.font_blue)), 5, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text_state?.text = span

        helper?.setOnClickListener(R.id.text_receive) {

            //接单
            CommonDialogUtil.showOriginConfirmDialog(mContext, "确认要接单吗？", object : CommonDialogUtil.IOnConfirmClick {
                override fun clickConfirm() {
                    RepeatInterfaceUtils.receiveOrder(mContext,orderBean?.id!!,object :RepeatInterfaceUtils.IRepeatInterfaceListener{
                        override fun callBack() {
                            //接单成功回调"
                            removeAt(helper.getmPosition())
                        }
                    })
                }
            })
        }

        helper?.setOnClickListener(R.id.layout_common){
            OrderMapDetailActivity.startActivity(mContext as Activity, orderBean.id!!, OrderMapDetailActivity.RECEIVE_LIST)
        }

    }


}