package net.ed58.dlm.clients.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.wise.common.baseapp.AppManager
import com.wise.common.commonutils.ToastUtil
import kotlinx.android.synthetic.main.dialog_add_account.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.entity.WithdrawAccountBean
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

/**
 * Created by sunpeng on 17/11/30.
 */
class UpdateAccountActivity : BaseCoreActivity(), View.OnClickListener {


    var isModify: Boolean = false
    var type: Int = 1
    lateinit var withdrawAccountBean: WithdrawAccountBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {

        isModify = intent.getBooleanExtra("isModify", false)
        type = intent.getIntExtra("type", 1)

        if (isModify) {
            withdrawAccountBean = intent.getSerializableExtra("withdrawAccountBean") as WithdrawAccountBean
            edit_name.setText(withdrawAccountBean.accountName)
            edit_account.setText(withdrawAccountBean.accountNo)
            when (type) {
                1 -> {
                    text_title.text = "修改支付宝账户"
                }
                2 -> {
                    text_title.text = "修改微信账户"
                }
            }
        } else {
            when (type) {
                1 -> {
                    text_title.text = "添加支付宝账户"
                }
                2 -> {
                    text_title.text = "添加微信账户"
                }
            }
        }

        im_back.setOnClickListener(this)
        button_confirm.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.im_back -> {
                onBackPressed()
            }
            R.id.button_confirm -> {
                if (isModify) {
                    updateAccount()
                } else {
                    addAccount()
                }
            }
        }
    }

    fun updateAccount() {
        HttpMethods.getInstance().updateAccount(object : RxSubscriber<WithdrawAccountBean>(mContext) {
            override fun _onNext(withdrawAccountBean: WithdrawAccountBean) {
                ToastUtil.showBottomtoast(this@UpdateAccountActivity,"修改成功")
                val intent = Intent()
                intent.putExtra("withdrawAccountBean",withdrawAccountBean)
                setResult(Activity.RESULT_OK,intent)
                AppManager.getAppManager().finishActivity()
            }

            override fun _onError(code: Int, message: String?) {
            }

        }, withdrawAccountBean.id, type, edit_name.text.toString().trim(), edit_account.text.toString().trim(), "", "")
    }

    fun addAccount() {
        HttpMethods.getInstance().addAccount(object : RxSubscriber<WithdrawAccountBean>(mContext) {
            override fun _onNext(withdrawAccountBean: WithdrawAccountBean) {
                ToastUtil.showBottomtoast(this@UpdateAccountActivity,"添加成功")
                val intent = Intent()
                intent.putExtra("withdrawAccountBean",withdrawAccountBean)
                setResult(Activity.RESULT_OK,intent)
                AppManager.getAppManager().finishActivity()

            }

            override fun _onError(code: Int, message: String?) {
            }

        }, type, edit_name.text.toString().trim(), edit_account.text.toString().trim(), "", "")
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_update_account
    }


    companion object {

        /**
         * isModify true是修改账户，false是新建
         * withdrawAccountBean isModify为true的时候传值
         * type 1是支付宝
         */
        fun startActivityForResult(context: Activity, type: Int, isModify: Boolean, withdrawAccountBean: WithdrawAccountBean?, requestCode:Int) {
            val intent = Intent(context, UpdateAccountActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("isModify", isModify)
            intent.putExtra("withdrawAccountBean", withdrawAccountBean)
            context.startActivityForResult(intent,requestCode)
        }


    }
}