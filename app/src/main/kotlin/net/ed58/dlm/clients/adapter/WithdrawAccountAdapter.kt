package net.ed58.dlm.clients.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.TextView
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import com.wise.common.baseapp.AppManager
import com.wise.common.baserx.RxBus
import com.wise.common.commonutils.ToastUtil
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.R.id.text_select
import net.ed58.dlm.clients.entity.WithdrawAccountBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.util.CommonDialogUtil
import net.ed58.dlm.clients.wallet.UpdateAccountActivity

/**
 * Created by sunpeng on 17/11/27.
 */

class WithdrawAccountAdapter(mContext: Context, mdatas: List<WithdrawAccountBean>) : CommonRecycleViewAdapter<WithdrawAccountBean>(mContext, R.layout.item_withdraw_account, mdatas) {

    var accountId: String = ""
    var clickPos:Int = 0

    override fun convert(helper: ViewHolderHelper?, withdrawAccountBean: WithdrawAccountBean?) {
        helper?.setText(R.id.text_default_accout, withdrawAccountBean?.accountNo)

        val text_select = helper?.getView<TextView>(text_select)
        text_select?.isSelected = withdrawAccountBean?.defaults!!
        if (withdrawAccountBean.defaults) {
            helper?.setText(R.id.text_select, "默认账户")
        } else {
            helper?.setText(R.id.text_select, "设为默认")
            helper?.setOnClickListener(R.id.text_select) {
                //点击设置 默认
                if (!withdrawAccountBean.defaults) {
                    setDefault(withdrawAccountBean)
                }
            }
        }


        when (withdrawAccountBean.accountType) {
            1 -> {
                helper?.setText(R.id.text_default_tip, "使用支付宝提现")
            }
        }

        helper?.setOnClickListener(R.id.layout_click) {
            //选择一个账户进行提现
            val intent = Intent()
            intent.putExtra("accountBean", withdrawAccountBean)
            (mContext as Activity).setResult(Activity.RESULT_OK, intent)
            AppManager.getAppManager().finishActivity()
        }

        helper?.setOnClickListener(R.id.text_edit){
            clickPos = helper.getmPosition()
            UpdateAccountActivity.startActivityForResult(mContext as Activity, withdrawAccountBean.accountType!!,true,withdrawAccountBean,Constant.REQUEST_SECOND_CODE)
        }

        helper?.setOnClickListener(R.id.text_delete){
            CommonDialogUtil.showOriginConfirmDialog(mContext, "确认要删除该账号吗？", object : CommonDialogUtil.IOnConfirmClick {
                override fun clickConfirm() {
                    deleteAccount(withdrawAccountBean)
                }
            })
        }

    }

    fun setDefault(withdrawAccountBean: WithdrawAccountBean?) {
        HttpMethods.getInstance().setAccountdefaults(object : RxSubscriber<WithdrawAccountBean>(mContext) {
            override fun _onNext(t: WithdrawAccountBean?) {
                withdrawAccountBean?.defaults = true
                notifyDataSetChanged()
            }

            override fun _onError(code: Int, message: String?) {

            }

        }, withdrawAccountBean?.id)
    }

    fun deleteAccount(withdrawAccountBean: WithdrawAccountBean?) {
        HttpMethods.getInstance().deleteAccount(object : RxSubscriber<WithdrawAccountBean>(mContext) {
            override fun _onNext(t: WithdrawAccountBean?) {
                ToastUtil.showBottomtoast(mContext,"删除成功")
                remove(withdrawAccountBean)
                RxBus.getInstance().post(RxKey.EVENT_UPDATE_WITHDRAW_ACCOUNT,WithdrawAccountBean())
            }

            override fun _onError(code: Int, message: String?) {

            }

        }, withdrawAccountBean?.id)
    }

}
