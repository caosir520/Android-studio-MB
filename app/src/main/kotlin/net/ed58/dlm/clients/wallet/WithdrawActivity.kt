package net.ed58.dlm.clients.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import com.wise.common.commonutils.CashierInputFilter
import kotlinx.android.synthetic.main.activity_withdraw.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.entity.WithdrawAccountBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey
import java.math.BigDecimal


/**
 * Created by sunpeng on 17/11/27.
 */
class WithdrawActivity : BaseCoreMVPActivity<WithdrawPresent, WithdrawPresent.IWithdrawListener>(), View.OnClickListener, WithdrawPresent.IWithdrawListener {


    var amount: BigDecimal = BigDecimal("0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

    }

    private fun initView() {
        amount = intent.getSerializableExtra("availableFee") as BigDecimal

        text_total_money.text = amount.toString()

        layout_add.setOnClickListener(this)
        button_confirm.setOnClickListener(this)
        image_back.setOnClickListener(this)
        text_change.setOnClickListener(this)

        val filters = arrayOf<InputFilter>(CashierInputFilter(amount))
        edit_input.filters = filters

        mRxManager.on<WithdrawAccountBean>(RxKey.EVENT_UPDATE_WITHDRAW_ACCOUNT) { withdrawAccountBean ->
            if (!TextUtils.isEmpty(withdrawAccountBean.id)) {
                //修改操作
                presenter.accoutId = withdrawAccountBean.id
                showDefaultAccount(withdrawAccountBean)
            }else{
                //删除操作
                presenter.accoutId = ""
                layout_default_account.visibility = View.GONE
            }

        }

        presenter.reqeustDefaultAccount()
    }

    /**
     * 显示选择的提现账号
     */
    override fun showDefaultAccount(withdrawAccountBean: WithdrawAccountBean) {
        layout_default_account.visibility = View.VISIBLE
        text_default_accout.text = withdrawAccountBean.accountNo
        when (withdrawAccountBean.accountType) {
            1 -> {
                image_pay.setImageResource(R.mipmap.ic_zhifubao_pay)
                text_default_tip.text = "使用支付宝提现"
            }
            2 -> {
                image_pay.setImageResource(R.mipmap.ic_zhifubao_pay)
                text_default_tip.text = "使用微信提现"
            }
        }
    }

    override fun getInputMoney(): String {
        return edit_input.text.toString().trim()
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_withdraw
    }

    override fun createPresenter(): WithdrawPresent {
        return WithdrawPresent()
    }

    override fun getUi(): WithdrawPresent.IWithdrawListener {
        return this
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layout_add -> {
                SelectAcountActivity.startActivityForResult(this, presenter.accoutId, Constant.REQUEST_FIRST_CODE)
            }
            R.id.button_confirm -> {
                //确定提现，输入密码
                presenter.showPasswordDialog()
            }
            R.id.text_change -> {
                //点击更换提现方式
                SelectAcountActivity.startActivityForResult(this, presenter.accoutId, Constant.REQUEST_FIRST_CODE)
            }
            R.id.image_back->{
                onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.REQUEST_FIRST_CODE -> {
                    val accountBean = data?.getSerializableExtra("accountBean") as WithdrawAccountBean
                    presenter.accoutId = accountBean.id
                    showDefaultAccount(accountBean)
                }
            }
        }
    }


    companion object {
        fun startActivity(context: Activity, availableFee: BigDecimal) {
            val intent = Intent(context, WithdrawActivity::class.java)
            intent.putExtra("availableFee", availableFee)
            context.startActivity(intent)
        }
    }
}