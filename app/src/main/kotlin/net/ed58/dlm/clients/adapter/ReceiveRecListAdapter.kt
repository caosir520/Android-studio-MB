package net.ed58.dlm.clients.adapter

import android.app.Activity
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import com.wise.common.commonutils.MoneyUtil
import com.wise.common.commonutils.ToastUtil
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.dialog.InputPasswordDialog
import net.ed58.dlm.clients.entity.OrderM
import net.ed58.dlm.clients.entity.SingleIntValue
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.order.EvaluateActivity
import net.ed58.dlm.clients.order.OrderMapDetailActivity
import net.ed58.dlm.clients.order.OrderUndistributeReasonActivity
import net.ed58.dlm.clients.order.TakeProductActivity
import net.ed58.dlm.clients.util.RepeatInterfaceUtils
import net.ed58.dlm.clients.view.numbervcodeview.BaseNumberCodeView
import net.ed58.dlm.clients.view.numbervcodeview.BottomSheetNumberCodeView

/**
 * Created by Administrator on 2017/11/13/013.
 */
class ReceiveRecListAdapter(context: Context) : CommonRecycleViewAdapter<OrderM>(context, R.layout.item_receive_tab_list) {


    var position: Int = -1//点击的条目位置
    var index: Int = 0

    override fun convert(helper: ViewHolderHelper?, orderBean: OrderM?) {
        helper?.setText(R.id.text_order_no, orderBean?.orderNo)
        helper?.setText(R.id.text_send_address, orderBean?.fetchAddress + orderBean?.fetchFloorHouseNum)
        helper?.setText(R.id.text_receive_address, orderBean?.receiverAddress + orderBean?.receiverFloorHouseNum)

        helper?.setVisible(R.id.text_top_distance, true)
        helper?.setVisible(R.id.text_bottom_distance, true)
        helper?.setText(R.id.text_top_distance, MoneyUtil.MoneyFomatWithTwoPoint(orderBean?.topDistance!! / 1000) + "km")
        helper?.setText(R.id.text_bottom_distance, MoneyUtil.MoneyFomatWithTwoPoint(orderBean?.bottomDistance!! / 1000) + "km")
        helper?.setVisibleNoGone(R.id.text_state, false)
        helper?.setText(R.id.text_time,orderBean?.createTime)
        helper?.setText(R.id.text_price,"¥"+orderBean?.orderAmount)

        val text_order_no = helper?.getView<TextView>(R.id.text_order_no)

        when (index) {
            0,1->{
                text_order_no?.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f)
                helper?.setVisible(R.id.text_time,false)
                helper?.setVisible(R.id.text_price,false)
                helper?.setText(R.id.text_order_no, "¥${orderBean?.orderAmount}")
            }
            2->{
                text_order_no?.setTextSize(TypedValue.COMPLEX_UNIT_SP,14f)
                helper?.setVisibleNoGone(R.id.text_state, true)
                helper?.setVisible(R.id.text_time,true)
                helper?.setVisible(R.id.text_price,true)
            }
        }

        helper?.setVisible(R.id.text_receive, true)
        helper?.setVisible(R.id.text_cancel, true)
        when (orderBean?.orderStatus) {
            400 -> {//已接单
                helper?.setText(R.id.text_state, "待取货")
                if (orderBean.contactFetcher) {//已经联系过发货人
                    helper?.setText(R.id.text_receive, "填写取货码")
                    if (orderBean.checkDeliverLockerCode) {//已经填写过取货码
                        helper?.setText(R.id.text_receive, "拍货品取货")
                    }
                } else {
                    helper?.setText(R.id.text_receive, "联系发货人")
                }
            }
            500 -> {//已取货（配送中）
                helper?.setText(R.id.text_state, "配送中")
                if (orderBean.contactReceiver) {//已经联系过收货人
                    helper?.setText(R.id.text_receive, "填写收货码")
                    if (orderBean.checkPickupLockerCode) {
                        helper?.setText(R.id.text_receive, "确认送达")
                    }
                } else {
                    helper?.setText(R.id.text_receive, "联系收货人")
                }
            }
            600 -> {//签收成功
                helper?.setText(R.id.text_state, "已完成")
                if (orderBean.carrierReviewStatus == 1) {
                    //评价过了
                    helper?.setVisible(R.id.layout_button,false)
                }else{
                    helper?.setText(R.id.text_receive, "评价订单")
                    helper?.setVisible(R.id.text_cancel, false)    
                }
                
            }
            700 -> {//已完成
                helper?.setText(R.id.text_state, "已完成")
                helper?.setVisible(R.id.layout_button,false)
            }
            -1,-100,-200,-300->{
                helper?.setText(R.id.text_state, "已取消")
                helper?.setVisible(R.id.text_receive, false)
                helper?.setVisible(R.id.text_cancel, false)
                helper?.setVisible(R.id.layout_button,false)
            }
            -400,-500->{
                helper?.setText(R.id.text_state, "已取消配送")
                helper?.setVisible(R.id.text_receive, false)
                helper?.setVisible(R.id.text_cancel, false)
                helper?.setVisible(R.id.layout_button,false)
            }
        }

        helper?.setOnClickListener(R.id.text_receive) {
            position = helper.getmPosition()
            when (orderBean?.orderStatus) {
                400 -> {
                    if (orderBean.contactFetcher && !orderBean.checkDeliverLockerCode) {//已经联系过发货人
                        //点击填写取货码
                        showInputPasswordDialog(orderBean, 1)
                    } else if (orderBean.checkDeliverLockerCode) {
                        //点击拍货品取货
                        TakeProductActivity.startActivityForResult(mContext as Activity, orderBean.id!!,index ,helper.getmPosition())
                    } else {
                        //点击联系发货人
                        RepeatInterfaceUtils.callActionPhone(mContext, orderBean.id!!, 1, object : RepeatInterfaceUtils.IRepeatInterfaceListener {
                            override fun callBack() {
                                orderBean.contactFetcher = true
                                notifyDataSetChanged()
                            }
                        })
                    }
                }
                500 -> {
                    if (orderBean.contactReceiver && !orderBean.checkPickupLockerCode) {//已经联系过发货人
                        //点击填写收货码
                        showInputPasswordDialog(orderBean, 2)

                    } else if (orderBean.checkPickupLockerCode) {
                        //点击确认送达
                        HttpMethods.getInstance().arrivalSuccess(object : RxSubscriber<SingleIntValue>(mContext) {
                            override fun _onNext(t: SingleIntValue?) {
                                ToastUtil.showBottomtoast(mContext, "订单已完成，快去评价吧")
                                removeAt(helper.getmPosition())
                            }

                            override fun _onError(code: Int, message: String?) {
                            }

                        }, orderBean.id)
                    } else {
                        //点击联系发货人
                        RepeatInterfaceUtils.callActionPhone(mContext, orderBean.id!!, 2, object : RepeatInterfaceUtils.IRepeatInterfaceListener {
                            override fun callBack() {
                                orderBean.contactReceiver = true
                                notifyDataSetChanged()
                            }
                        })
                    }
                }
                600 -> {
                    //评价晒单
                    EvaluateActivity.startActivity(mContext as Activity, orderBean.id!!,1)
                }
            }
        }

        helper?.setOnClickListener(R.id.text_cancel) {
            position = helper.getmPosition()
            OrderUndistributeReasonActivity.startActivityForResult(mContext as Activity, orderBean?.id!!, position, 2,index)
        }

        helper?.setOnClickListener(R.id.layout_click){
            OrderMapDetailActivity.startActivity(mContext as Activity, orderBean?.id!!, OrderMapDetailActivity.MY_RECEIVE_LIST)
        }
        helper?.setOnClickListener(R.id.layout_common){
            OrderMapDetailActivity.startActivity(mContext as Activity, orderBean?.id!!, OrderMapDetailActivity.MY_RECEIVE_LIST)
        }
    }

    //type 1是取货码，2是收货
    private fun showInputPasswordDialog(orderBean: OrderM, type: Int) {
        val dialog = InputPasswordDialog(mContext, type)
        var span: SpannableString? = null
        if (type == 1) {
            span = SpannableString("取货码已发送至发件人手机${orderBean.fetchPhone}上")
        } else if (type == 2) {
            span = SpannableString("收货码已发送至收件人手机${orderBean.receiverPhone}上")
        }
        span?.setSpan(ForegroundColorSpan(mContext.resources.getColor(R.color.main_color)), 12, span.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        dialog.setTipText(span!!, mContext.resources.getColor(R.color.title_color))

        dialog.setNumberCodeCallback(BaseNumberCodeView.OnInputNumberCodeCallback { result ->
            //输完6位密码 验证接口
            if (type == 1) {
                HttpMethods.getInstance().deliverSuccess(object : RxSubscriber<SingleIntValue>(mContext) {
                    override fun _onNext(t: SingleIntValue?) {
                        ToastUtil.showBottomtoast(mContext,"取货码正确")
                        dialog.disMiss()
                        orderBean.checkDeliverLockerCode = true
                        notifyDataSetChanged()
                    }

                    override fun _onError(code: Int, message: String?) {
                        dialog.setTipText("取货码输入错误，请重新输入", mContext.resources.getColor(R.color.main_color))
                        dialog.restoreViews()
                    }

                }, orderBean.id, result)
            } else if (type == 2) {
                HttpMethods.getInstance().pickupSuccess(object : RxSubscriber<SingleIntValue>(mContext) {
                    override fun _onNext(t: SingleIntValue?) {
                        ToastUtil.showBottomtoast(mContext,"收货码正确")
                        dialog.disMiss()
                        orderBean.checkPickupLockerCode = true
                        notifyDataSetChanged()
                    }

                    override fun _onError(code: Int, message: String?) {
                        dialog.setTipText("收货码输入错误，请重新输入", mContext.resources.getColor(R.color.main_color))
                        dialog.restoreViews()
                    }

                }, orderBean.id, result)
            }

        })
        dialog.setOnHideBottomLayoutListener(BottomSheetNumberCodeView.OnHideBottomLayoutListener {
            //左上角图标 关闭对话框
            if (dialog.isShowing) {
                dialog.disMiss()
            }
        })
        dialog.setReSendClickListener(View.OnClickListener {
            //点击重新发送短信码
            if (type == 1) {
                HttpMethods.getInstance().deliverLockerCode(object : RxSubscriber<SingleIntValue>(mContext) {
                    override fun _onError(code: Int, message: String?) {

                    }

                    override fun _onNext(t: SingleIntValue?) {
                        ToastUtil.showBottomtoast(mContext, "发送成功")
                        val span = SpannableString("取货码已发送至发件人手机${orderBean.fetchPhone}上")
                        span.setSpan(ForegroundColorSpan(mContext.resources.getColor(R.color.main_color)), 12, span.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        dialog.setTipText(span, mContext.resources.getColor(R.color.title_color))
                    }
                }, orderBean.id)
            } else if (type == 2) {
                HttpMethods.getInstance().pickupLockerCode(object : RxSubscriber<SingleIntValue>(mContext) {
                    override fun _onError(code: Int, message: String?) {

                    }

                    override fun _onNext(t: SingleIntValue?) {
                        ToastUtil.showBottomtoast(mContext, "发送成功")
                        val span = SpannableString("收货码已发送至收件人手机${orderBean.receiverPhone}上")
                        span.setSpan(ForegroundColorSpan(mContext.resources.getColor(R.color.main_color)), 12, span.length - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        dialog.setTipText(span, mContext.resources.getColor(R.color.title_color))
                    }
                }, orderBean.id)
            }

        })
        dialog.show()
    }

}