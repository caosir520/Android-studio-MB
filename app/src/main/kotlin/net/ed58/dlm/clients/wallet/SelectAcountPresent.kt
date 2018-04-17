package net.ed58.dlm.clients.wallet

import android.os.Bundle
import net.ed58.dlm.clients.adapter.WithdrawAccountAdapter
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.dialog.AddCountDialog
import net.ed58.dlm.clients.entity.WithdrawAccountBean
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

/**
 * Created by sunpeng on 17/11/28.
 */
class SelectAcountPresent :BasePresenter<SelectAcountPresent.IAddAccountListener>() {


    var list:MutableList<WithdrawAccountBean>? = null
    lateinit var withdrawAccountAdapter: WithdrawAccountAdapter
    lateinit var dialog:AddCountDialog

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
    }

    //获取所有添加的账号
    fun requestAccountList() {
        HttpMethods.getInstance().getAccountList(object : RxSubscriber<WithdrawAccountBean>(activity){
            override fun _onNext(withdrawAccountBean: WithdrawAccountBean) {
                list = withdrawAccountBean.list
                withdrawAccountAdapter = WithdrawAccountAdapter(activity,list!!)
                ui.setAdapter(withdrawAccountAdapter)
            }

            override fun _onError(code: Int, message: String?) {

            }

        })
    }


    interface IAddAccountListener:BaseUI{
        fun setAdapter(withdrawAccountAdapter: WithdrawAccountAdapter)
    }


}