package net.ed58.dlm.clients.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.wise.common.baserx.RxBus
import kotlinx.android.synthetic.main.activity_add_account.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.WithdrawAccountAdapter
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.entity.WithdrawAccountBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey

/**
 * Created by sunpeng on 17/11/27.
 */
class SelectAcountActivity : BaseCoreMVPActivity<SelectAcountPresent, SelectAcountPresent.IAddAccountListener>(), View.OnClickListener, SelectAcountPresent.IAddAccountListener {

    var accountId: String = ""//默认选中的账号

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

    }

    private fun initView() {
        accountId = intent.getStringExtra("accountId")
        recyclerView.layoutManager = LinearLayoutManager(this)

        layout_add_alipay.setOnClickListener(this)
        layout_add_wx.setOnClickListener(this)
        image_back.setOnClickListener(this)

        presenter.requestAccountList()
    }

    override fun setAdapter(withdrawAccountAdapter: WithdrawAccountAdapter) {
        withdrawAccountAdapter.accountId = accountId
        recyclerView.adapter = withdrawAccountAdapter
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_add_account
    }

    override fun createPresenter(): SelectAcountPresent {
        return SelectAcountPresent()
    }

    override fun getUi(): SelectAcountPresent.IAddAccountListener {
        return this
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layout_add_alipay -> {
                //添加支付宝账户
                UpdateAccountActivity.startActivityForResult(this,1,false,null,Constant.REQUEST_FIRST_CODE)
            }
            R.id.layout_add_wx -> {

            }
            R.id.image_back -> {
                onBackPressed()
            }
        }
    }

    companion object {
        //accountId用于寻找 选中的item
        fun startActivityForResult(context: Activity, accountId: String, requestCode: Int) {
            val intent = Intent(context, SelectAcountActivity::class.java)
            intent.putExtra("accountId", accountId)
            context.startActivityForResult(intent, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Constant.REQUEST_FIRST_CODE->{
                    //添加账户
                    val withdrawAccountBean = data?.getSerializableExtra("withdrawAccountBean") as WithdrawAccountBean
                    presenter.withdrawAccountAdapter.add(withdrawAccountBean)
                }
                Constant.REQUEST_SECOND_CODE->{
                    //修改账户
                    val withdrawAccountBean = data?.getSerializableExtra("withdrawAccountBean") as WithdrawAccountBean
                    presenter.withdrawAccountAdapter.replaceAt(presenter.withdrawAccountAdapter.clickPos,withdrawAccountBean)
                    if (presenter.withdrawAccountAdapter.accountId == withdrawAccountBean.id) {
                        //如果修改的是选中的则刷新账户
                        RxBus.getInstance().post(RxKey.EVENT_UPDATE_WITHDRAW_ACCOUNT,withdrawAccountBean)
                    }
                }
            }
        }
    }
}