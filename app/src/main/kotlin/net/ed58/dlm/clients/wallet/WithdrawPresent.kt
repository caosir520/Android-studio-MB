package net.ed58.dlm.clients.wallet

import android.text.TextUtils
import com.wise.common.baseapp.AppManager
import com.wise.common.baserx.RxBus
import com.wise.common.commonutils.ToastUtil
import com.wise.common.security.Md5Security
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.dialog.InputPasswordDialog
import net.ed58.dlm.clients.entity.WithdrawAccountBean
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.view.numbervcodeview.BaseNumberCodeView
import net.ed58.dlm.clients.view.numbervcodeview.CenterNumberCodeView
import java.math.BigDecimal

/**
 * Created by sunpeng on 17/11/28.
 */
class WithdrawPresent :BasePresenter<WithdrawPresent.IWithdrawListener>(){


    var accoutId: String = ""

    fun reqeustDefaultAccount() {

        HttpMethods.getInstance().getAccountList(object : RxSubscriber<WithdrawAccountBean>(activity){
            override fun _onNext(withdrawAccountBean: WithdrawAccountBean) {
                for (item in withdrawAccountBean.list!!){
                    if (item.defaults) {
                        accoutId = item.id
                        ui.showDefaultAccount(item)
                        break
                    }
                }
            }

            override fun _onError(code: Int, message: String?) {

            }

        })
    }


    fun showPasswordDialog() {
        if (TextUtils.isEmpty(accoutId)) {
            ToastUtil.showBottomtoast(activity,"请选择提现账户")
            return
        }
        if (TextUtils.isEmpty(ui.getInputMoney())) {
            ToastUtil.showBottomtoast(activity,"请输入提现金额")
            return
        }
        val dialog = InputPasswordDialog(activity, 3)
        dialog.setCenterNumberCodeCallback(BaseNumberCodeView.OnInputNumberCodeCallback { result ->
            //输完6位密码 验证接口
            HttpMethods.getInstance().applyWithdraw(object : RxSubscriber<WithdrawAccountBean>(activity) {
                override fun _onNext(t: WithdrawAccountBean?) {
                    RxBus.getInstance().post(RxKey.EVENT_WALLET_CHANGE, true)
                    ToastUtil.showBottomtoast(activity,"提现成功")
                    dialog.disMiss()
                    AppManager.getAppManager().finishActivity()
                }

                override fun _onError(code: Int, message: String?) {
                    dialog.restoreViews()
                }

            }, accoutId,BigDecimal(ui.getInputMoney()), Md5Security.getMD5("C5873E57D909A025${result}"))

        })
        dialog.setOnHideCenterLayoutListener(CenterNumberCodeView.OnHideBottomLayoutListener {
            //左上角图标 关闭对话框
            if (dialog.isShowing) {
                dialog.disMiss()
            }
        })
        dialog.show()
    }

    interface IWithdrawListener:BaseUI{
        fun showDefaultAccount(withdrawAccountBean: WithdrawAccountBean)
        fun getInputMoney():String
    }
}