package net.ed58.dlm.clients.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.wise.common.security.Md5Security
import kotlinx.android.synthetic.main.activity_order_payment.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.dialog.InputPasswordDialog
import net.ed58.dlm.clients.entity.SingleStringValue
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.Constant.PAY_POSITION
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.util.RepeatInterfaceUtils
import net.ed58.dlm.clients.view.numbervcodeview.BaseNumberCodeView
import net.ed58.dlm.clients.view.numbervcodeview.CenterNumberCodeView

//支付的activity
class OrderPaymentActivity : BaseCoreActivity(), View.OnClickListener {

    var orderId: String? = null
    var payService = 2//支付方式 1是支付宝 2是微信
    var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderId = intent.getStringExtra("orderId")
        type = intent.getIntExtra("type", -1)

        initView()
    }

    private fun initView() {
        text_order_price.text = intent.getStringExtra("order_price")
        wx_pay_checked.setOnClickListener(this)
        alipay_checked.setOnClickListener(this)
        wallet_pay_checked.setOnClickListener(this)
        button_pay.setOnClickListener(this)
        image_back.setOnClickListener(this)

        rb_wxpay.isChecked = true
        button_pay.isEnabled = true
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.wx_pay_checked -> {
                rb_wxpay.isChecked = true
                payService = 2
                button_pay.isEnabled = true
            }
            R.id.alipay_checked -> {
                rb_alipay.isChecked = true
                payService = 1
                button_pay.isEnabled = true
            }
            R.id.wallet_pay_checked -> {
                rb_wallet.isChecked = true
                payService = 3
                button_pay.isEnabled = true
            }
            R.id.button_pay -> {
                //在哪边点击的支付
                when (type) {
                    PAY_FROM_SUBMIT -> {
                        PAY_POSITION = Constant.PAY_FROM_SUBMIT
                    }
                    PAY_FROM_ORDER -> {
                        PAY_POSITION = Constant.PAY_FROM_WAIT_PAY_LIST
                    }
                    PAY_FROM_ALL_ORDER -> {
                        PAY_POSITION = Constant.PAY_FROM_ALL_ORDER
                    }
                }
                //支付方式
                when (payService) {
                    1, 2 -> {
                        RepeatInterfaceUtils.rePay(this, orderId!!, payService)
                    }
                    3 -> {
                        walletPay()
                    }
                }

            }
            R.id.image_back -> {
                onBackPressed()
            }
        }
    }

    //钱包支付
    fun walletPay() {
        val dialog = InputPasswordDialog(this, 3)
        dialog.setCenterNumberCodeCallback(BaseNumberCodeView.OnInputNumberCodeCallback { result ->
            //输完6位密码 验证接口
            HttpMethods.getInstance().walletPay(object : RxSubscriber<SingleStringValue>(this) {

                override fun _onNext(t: SingleStringValue?) {
                    RepeatInterfaceUtils.paySuccess(this@OrderPaymentActivity)
                }

                override fun _onError(code: Int, message: String?) {

                }
            }, orderId, Md5Security.getMD5("C5873E57D909A025${result}"))
        })
        dialog.setOnHideCenterLayoutListener(CenterNumberCodeView.OnHideBottomLayoutListener {
            //左上角图标 关闭对话框
            if (dialog.isShowing) {
                dialog.disMiss()
            }
        })
        dialog.show()
    }

    override fun onBackPressed() {
        if (type == OrderPaymentActivity.PAY_FROM_SUBMIT) {
            mRxManager.post(RxKey.EVENT_ORDER_FINISH, true)
            mRxManager.post(RxKey.EVENT_PAY_GO_MAIN_ORDER_TAB, 0)
        }

        super.onBackPressed()
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_order_payment
    }

    companion object {
        const val PAY_FROM_SUBMIT: Int = 1//正常下单 来的支付
        const val PAY_FROM_ORDER: Int = 2//订单列表 来的支付
        const val PAY_FROM_ALL_ORDER: Int = 3//全部订单列表 来的支付

        fun startActivity(context: Context, orderId: String?, order_price: String?, type: Int?) {
            val intent = Intent(context, OrderPaymentActivity::class.java)
            intent.putExtra("orderId", orderId)
            intent.putExtra("order_price", order_price)
            intent.putExtra("type", type)
            context.startActivity(intent)
        }
    }
}
