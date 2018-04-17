package net.ed58.dlm.clients.adapter

import android.app.Activity
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.entity.OrderM
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.order.*

/**
 * Created by sunpeng on 17/10/26.
 *
 */
class SendListAdapter(context: Context?) : CommonRecycleViewAdapter<OrderM>(context, R.layout.item_send_list) {


    var position: Int = -1//点击的条目位置
    var index: Int = 0

    override fun convert(helper: ViewHolderHelper?, orderBean: OrderM?) {
        helper?.setText(R.id.text_order_no, orderBean?.orderNo)
        helper?.setText(R.id.text_send_address, "${orderBean?.fetchAddress}${orderBean?.fetchFloorHouseNum}")
        helper?.setText(R.id.text_receive_address, "${orderBean?.receiverAddress}${orderBean?.receiverFloorHouseNum}")
        helper?.setText(R.id.text_product, "物品种类：" + orderBean?.categoryName)
        helper?.setText(R.id.text_time, orderBean?.createTime)
        helper?.setText(R.id.text_price, "¥" + orderBean?.orderAmount)


        //改变按钮颜色
        helper?.setVisible(R.id.text_one_button, true)
        helper?.setVisible(R.id.text_two_button, true)
        helper?.setVisible(R.id.view_bottom_divider, true)
        helper?.setBackgroundRes(R.id.text_one_button, R.drawable.shape_orange_stroke)
        helper?.setBackgroundRes(R.id.text_two_button, R.drawable.shape_black_stroke)
        helper?.setTextColorRes(R.id.text_one_button, R.color.main_color)
        helper?.setTextColorRes(R.id.text_two_button, R.color.title_color)

        when (index) {
            //已接单item 显示骑手好评率
            2->{
                helper?.setVisible(R.id.text_rating,true)
                val text_rating = helper?.getView<TextView>(R.id.text_rating)
                val span = SpannableString("骑手好评率${orderBean?.asSenderRating!!}")
                span.setSpan(ForegroundColorSpan(mContext.resources.getColor(R.color.font_blue)), 5, span.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                text_rating?.text = span

                helper?.setOnClickListener(R.id.text_rating){
                    RiderEvaluationRecordActivity.startActivity(mContext, orderBean.carrier?.id!!)
                }
            }
            else->{
                helper?.setVisible(R.id.text_rating,false)
            }
        }

        when (orderBean?.orderStatus) {
            0 -> {
                //待支付
                helper?.setText(R.id.text_state, "待支付")
                helper?.setText(R.id.text_one_button, "  支付  ")
                helper?.setText(R.id.text_two_button, "取消订单")
            }
            100 -> {
                //支付中
                helper?.setText(R.id.text_state, "待支付")
                helper?.setText(R.id.text_one_button, "  支付  ")
                helper?.setText(R.id.text_two_button, "取消订单")
            }
            200 -> {
                //支付失败
                helper?.setText(R.id.text_state, "支付失败")
                helper?.setVisible(R.id.text_one_button, false)
                helper?.setVisible(R.id.text_two_button, false)
                helper?.setVisible(R.id.view_bottom_divider, false)
            }
            300 -> {
                //待接单
                helper?.setText(R.id.text_state, "待接单")
                helper?.setVisible(R.id.text_two_button, false)
                helper?.setText(R.id.text_one_button, "取消订单")
                helper?.setBackgroundRes(R.id.text_one_button, R.drawable.shape_black_stroke)
                helper?.setTextColorRes(R.id.text_one_button, R.color.title_color)
            }
            400 -> {
                //已接单
                helper?.setText(R.id.text_state, "待取货")
                helper?.setVisible(R.id.text_one_button, false)
                helper?.setVisible(R.id.text_two_button, false)
                helper?.setVisible(R.id.view_bottom_divider, false)
            }
            500 -> {
                //已取货
                helper?.setText(R.id.text_state, "配送中")
                helper?.setVisible(R.id.text_one_button, false)
                helper?.setVisible(R.id.text_two_button, false)
                helper?.setVisible(R.id.view_bottom_divider, false)
            }
            600 -> {
                //送货完成
                helper?.setText(R.id.text_state, "已完成")
                if (orderBean.originatorReviewStatus == 1) {
                    //评价过了
                    helper?.setVisible(R.id.text_one_button, false)
                    helper?.setVisible(R.id.text_two_button, false)
                    helper?.setVisible(R.id.view_bottom_divider, false)
                } else {
                    helper?.setText(R.id.text_one_button, "评价订单")
                    helper?.setVisible(R.id.text_two_button, false)
                }

            }
            700 -> {
                //已经评价完成
                helper?.setText(R.id.text_state, "已完成")
                helper?.setVisible(R.id.text_one_button, false)
                helper?.setVisible(R.id.text_two_button, false)
                helper?.setVisible(R.id.view_bottom_divider, false)
            }
            -1, -100, -200, -300 -> {
                //已取消
                helper?.setText(R.id.text_state, "已取消")
                helper?.setVisible(R.id.text_one_button, false)
                helper?.setVisible(R.id.text_two_button, false)
                helper?.setVisible(R.id.view_bottom_divider, false)
            }
            -400 -> {
                helper?.setText(R.id.text_state, "取货失败")
                helper?.setVisible(R.id.text_one_button, false)
                helper?.setVisible(R.id.text_two_button, false)
                helper?.setVisible(R.id.view_bottom_divider, false)
            }
            -500 -> {
                helper?.setText(R.id.text_state, "签收失败")
                helper?.setVisible(R.id.text_one_button, false)
                helper?.setVisible(R.id.text_two_button, false)
                helper?.setVisible(R.id.view_bottom_divider, false)
            }
        }

        helper?.setOnClickListener(R.id.text_one_button) {
            position = helper.getmPosition()
            when (orderBean?.orderStatus) {
                0, 100 -> {
                    //去支付
                    when (index) {
                    //tab位置
                        0 -> OrderPaymentActivity.startActivity(mContext, orderBean.id, "${orderBean.orderAmount}", 2)
                        3 -> OrderPaymentActivity.startActivity(mContext, orderBean.id, "${orderBean.orderAmount}", 3)
                    }

                }
                300 -> {
                    //取消订单
                    cancelOrder(mContext, orderBean.id!!)
                }
                600 -> {
                    //评价
                    if (index == 2) {
                        //已结单列表操作评价的时候
                        EvaluateActivity.startActivityForResult(mContext as Activity, orderBean.id!!, 3, Constant.REQUEST_SECOND_CODE)
                    } else if (index == 3) {
                        //全部列表操作评价的时候
                        EvaluateActivity.startActivity(mContext as Activity, orderBean.id!!, 2)
                    }

                }
            }
        }
        helper?.setOnClickListener(R.id.text_two_button) {
            position = helper.getmPosition()
            // 减去头部刷新底部刷新
            when (orderBean?.orderStatus) {
                0, 100, 400 -> {
                    //取消订单
                    cancelOrder(mContext, orderBean.id!!)
                }
            }
        }

        helper?.setOnClickListener(R.id.layout_click){
            OrderDetailsActivity.startActivity(mContext as Activity,orderBean?.id!!)
        }
        helper?.setOnClickListener(R.id.layout_common){
            OrderDetailsActivity.startActivity(mContext as Activity,orderBean?.id!!)
        }
    }

    private fun cancelOrder(context: Context, orderId: String) {
        OrderUndistributeReasonActivity.startActivityForResult(context as Activity, orderId, position, 1, index)
    }


}