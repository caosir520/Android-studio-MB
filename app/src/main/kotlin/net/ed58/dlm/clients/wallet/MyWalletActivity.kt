package net.ed58.dlm.clients.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_my_wallet.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.entity.AccountBean
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

class MyWalletActivity : BaseCoreActivity(), View.OnClickListener {

    var accountBean: AccountBean? = null
    private var accountId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        initView()
    }

    private fun initView() {
        image_back.setOnClickListener(this)
        layout_trade_detail.setOnClickListener(this)
        layout_recharge.setOnClickListener(this)
        layout_withdraw.setOnClickListener(this)

        //注册 充值成功 监听
        mRxManager.on<Boolean>(RxKey.EVENT_WALLET_CHANGE){
            request(false)
        }

       request(true)
    }

    fun request(isShowDialog:Boolean) {
        HttpMethods.getInstance().getWalletMoney(object : RxSubscriber<AccountBean>(this,isShowDialog) {
            override fun _onError(code: Int, message: String?) {
            }

            override fun _onNext(accountBean: AccountBean) {
                this@MyWalletActivity.accountBean = accountBean
                text_money.text = "${accountBean.amountFee}"
                accountId = accountBean.accountId
            }
        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_wallet
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_back -> {
                onBackPressed()
            }
            R.id.layout_trade_detail -> {
                //交易明细
                Log.d("tag",accountId)
                TransactionDetailsActivity.startActivity(this,accountId )
            }
            R.id.layout_recharge -> {
                //充值
                RechargeActivity.startActivity(this)
            }
            R.id.layout_withdraw -> {
                //提现
                WithdrawActivity.startActivity(this, accountBean?.availableFee!!)
            }
        }

    }

    companion object {
        fun startActivity(context: Activity) {
            val intent = Intent(context, MyWalletActivity::class.java)
            context.startActivity(intent)
        }
    }
}