package net.ed58.dlm.clients.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import com.wise.common.commonutils.CashierInputFilter
import com.wise.common.commonutils.CollectionUtils
import com.wise.common.commonutils.ToastUtil
import kotlinx.android.synthetic.main.activity_recharge.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.ProductTypeAdapter
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.entity.PayBean
import net.ed58.dlm.clients.entity.ProductTypeListBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.Constant.PAY_POSITION
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.util.RepeatInterfaceUtils
import java.math.BigDecimal

class RechargeActivity : BaseCoreActivity(), View.OnClickListener {


    var payService = 2//支付方式 1是支付宝 2是微信
    lateinit var productTypeAdapter: ProductTypeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initView()
    }

    private fun initView() {
        wx_pay_checked.setOnClickListener(this)
        alipay_checked.setOnClickListener(this)
        button_pay.setOnClickListener(this)
        image_back.setOnClickListener(this)

        edit_price.filters = arrayOf<InputFilter>(CashierInputFilter(BigDecimal("99999")))

        text_agree.isSelected = true
        rb_wxpay.isChecked = true

        HttpMethods.getInstance().getProductType(object : RxSubscriber<ProductTypeListBean>(this) {
            override fun _onNext(productTypeListBean: ProductTypeListBean) {
                button_pay.isEnabled = true
                recyclerView.layoutManager = GridLayoutManager(this@RechargeActivity, 3)
                val list = productTypeListBean.list
                var mDatas :MutableList<ProductTypeListBean.ProductTypeBean>? = mutableListOf()
                if (!CollectionUtils.isNullOrEmpty(list)) {
                    mDatas= list?.get(0)?.children
                }
                mDatas?.add(ProductTypeListBean.ProductTypeBean().apply {
                    this.name = "其他"
                })

                productTypeAdapter = ProductTypeAdapter(this@RechargeActivity, mDatas)
                productTypeAdapter.listener = object : ProductTypeAdapter.IButtonListener {
                    override fun onItemClick(position: Int) {
                        if (position == productTypeAdapter.size - 1) {
                            layout_total_price.visibility = View.VISIBLE
                        } else {
                            layout_total_price.visibility = View.GONE
                        }
                    }
                }
                recyclerView.adapter = productTypeAdapter
            }

            override fun _onError(code: Int, message: String?) {
            }

        }, 3)
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_recharge
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
            R.id.button_pay -> {
                val amount: BigDecimal
                if (productTypeAdapter.selectedPos == productTypeAdapter.size - 1 && TextUtils.isEmpty(edit_price.text.toString().trim())) {
                    ToastUtil.showBottomtoast(this, "请输入充值金额")
                    return
                }else if(productTypeAdapter.selectedPos == productTypeAdapter.size - 1 ){
                    amount = BigDecimal(edit_price.text.toString().trim())
                } else{
                    amount = BigDecimal(productTypeAdapter[productTypeAdapter.selectedPos].name?.replace("元", ""))
                }
                PAY_POSITION = Constant.PAY_FROM_WALLET
                HttpMethods.getInstance().payWallet(object : RxSubscriber<PayBean>(this) {
                    override fun _onNext(payBean: PayBean) {
                        RepeatInterfaceUtils.wakePayPage(payBean, payService, this@RechargeActivity)
                    }

                    override fun _onError(code: Int, message: String?) {
                    }

                }, payService, amount)
            }
            R.id.image_back -> {
                onBackPressed()
            }
        }
    }

    companion object {
        fun startActivity(context: Activity) {
            val intent = Intent(context, RechargeActivity::class.java)
            context.startActivity(intent)
        }
    }
}