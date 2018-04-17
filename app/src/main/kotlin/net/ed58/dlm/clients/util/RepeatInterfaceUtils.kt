package net.ed58.dlm.clients.util

import android.content.Context
import com.wise.common.baseapp.AppManager
import com.wise.common.baserx.RxBus
import com.wise.common.baserx.RxJavaDemoUtil
import com.wise.common.commonutils.ToastUtil
import net.ed58.dlm.clients.entity.PayBean
import net.ed58.dlm.clients.entity.SingleIntValue
import net.ed58.dlm.clients.entity.SingleStringValue
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.order.OrderPaymentActivity
import net.ed58.dlm.clients.pay.AliPayhelper
import net.ed58.dlm.clients.pay.WXPayHelper
import org.json.JSONException
import org.json.JSONObject
import rx.Subscriber

/**
 * Created by sunpeng on 17/9/15.
 * 重复接口调用
 */

object RepeatInterfaceUtils {


    /**
     * 从订单调起支付
     */
    fun rePay(mContext: Context, orderId: String, payService: Int) {
        HttpMethods.getInstance().repay(object : RxSubscriber<PayBean>(mContext) {
            override fun _onNext(payBean: PayBean) {
                wakePayPage(payBean, payService, mContext)
            }

            override fun _onError(code: Int, message: String) {
                Constant.PAY_POSITION = -1
            }
        }, orderId, payService)
    }

    //支付宝 ，微信，钱包支付成功的回调处理
    fun paySuccess(mContext: Context) {
        ToastUtil.showBottomtoast(mContext, "支付成功")
        when {
            Constant.PAY_POSITION == Constant.PAY_FROM_SUBMIT -> {
                RxBus.getInstance().post(RxKey.EVENT_ORDER_FINISH, true)
                RxBus.getInstance().post(RxKey.EVENT_PAY_GO_MAIN_ORDER_TAB, 1)
            }
            Constant.PAY_POSITION == Constant.PAY_FROM_WAIT_PAY_LIST -> RxBus.getInstance().post(RxKey.EVENT_CANCEL_SEND_LIST_ITEM, true)
            Constant.PAY_POSITION == Constant.PAY_FROM_ALL_ORDER -> RxBus.getInstance().post(RxKey.EVENT_ALL_SEND_LIST_ITEM, -1)
        }
        AppManager.getAppManager().finishActivity(OrderPaymentActivity::class.java)
    }

    //唤起支付
    fun wakePayPage(payBean: PayBean, payService: Int, mContext: Context) {
        val result = payBean.payInfo
        if (payService == 1) {
            //alipay
            AliPayhelper(mContext, result).pay()
        } else {
            try {
                val jsonObject = JSONObject(result)
                val aPackage = jsonObject.getString("package")
                val split = aPackage.split("=".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                WXPayHelper(mContext, split[1]).pay()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    //接单按钮
    fun receiveOrder(mContext: Context, orderId: String, iRepeatInterfaceListener: IRepeatInterfaceListener?) {
        HttpMethods.getInstance().receiveOrder(object : RxSubscriber<SingleIntValue>(mContext) {

            override fun _onNext(t: SingleIntValue?) {
                //弹出对话框
                val showOriginSuccessDialog = CommonDialogUtil.showOriginSuccessDialog(mContext, "接单成功！")
                //倒计时3秒
                RxJavaDemoUtil.CountDowm(object : Subscriber<Long>() {
                    override fun onError(e: Throwable?) {
                    }

                    override fun onCompleted() {
                    }

                    override fun onNext(time: Long?) {
                        if (time?.toInt() == 2) {
                            if (showOriginSuccessDialog.isShowing) {
                                showOriginSuccessDialog.dismiss()
                            }
                            if (iRepeatInterfaceListener != null) {
                                iRepeatInterfaceListener.callBack()
                            } else {
                                AppManager.getAppManager().finishActivity()
                            }
                        }
                    }

                }, 3)


            }

            override fun _onError(code: Int, message: String?) {
                if (code == 20017) {
                    //被别人抢单了
                    val dialog = CommonDialogUtil.showOriginfailDialog(mContext)
                    //倒计时3秒
                    RxJavaDemoUtil.CountDowm(object : Subscriber<Long>() {
                        override fun onError(e: Throwable?) {
                        }

                        override fun onCompleted() {
                        }

                        override fun onNext(time: Long?) {
                            if (time?.toInt() == 2) {
                                if (dialog.isShowing) {
                                    dialog.dismiss()
                                }
                                if (iRepeatInterfaceListener != null) {
                                    iRepeatInterfaceListener.callBack()
                                } else {
                                    AppManager.getAppManager().finishActivity()
                                }
                            }
                        }

                    }, 3)
                }

            }

        }, orderId)
    }

    fun cancelPay(mContext: Context, payDetailId: String) {
//        HttpMethods.getInstance().cancelPay(object : NoTipRxSubscriber<SingleIntValue>(mContext, false) {
//            override fun _onNext(singleBoolValue: SingleIntValue) {
//
//            }
//
//            override fun _onError(code: Int, message: String) {
//
//            }
//        }, payDetailId)
    }

    //action 1联系发货人 2联系收货人
    fun callActionPhone(mContext: Context, orderId: String, action: Int, iRepeatInterfaceListener: IRepeatInterfaceListener?) {
        HttpMethods.getInstance().getActionPhone(object : RxSubscriber<SingleStringValue>(mContext) {

            override fun _onNext(t: SingleStringValue?) {
                iRepeatInterfaceListener?.callBack()
                CommonDialogUtil.showCallDialog(mContext, t?.value!!)
            }

            override fun _onError(code: Int, message: String?) {

            }

        }, orderId, action)
    }


    interface IRepeatInterfaceListener {
        fun callBack()
    }
}
