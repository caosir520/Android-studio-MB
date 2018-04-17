package net.ed58.dlm.clients.main

import net.ed58.dlm.clients.adapter.ReceiveListAdapter
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.entity.CustomerBean
import net.ed58.dlm.clients.entity.MainReceiveOrderBean
import net.ed58.dlm.clients.entity.OrderM
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.NoTipRxSubscriber
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.sp.MyConfiguration

/**
 * Created by sunpeng on 17/2/10.
 */

class MainHomeReceivePresent : BasePresenter<MainHomeReceivePresent.IMainHomeReceive>() {



    //获取认证身份证审核状态
    fun requestAuth() {
        HttpMethods.getInstance().getAuthState(object : NoTipRxSubscriber<CustomerBean>(activity,false) {
            override fun _onNext(customerBean: CustomerBean?) {
                ui.onAuthSuccess(customerBean)
            }

            override fun _onError(code: Int, message: String?) {

            }

        })
    }


    fun requestList() {
        val size: Int?
        if (ui.getAdapter().pageBean?.isRefresh!!) {
            size = 0
        } else {
            size = ui.getAdapter().size
        }
        HttpMethods.getInstance().getWaitReceiveOrderList(object : RxSubscriber<MainReceiveOrderBean>(activity) {
            override fun _onNext(orderBeanList: MainReceiveOrderBean?) {
                ui.onWaitReceiveListSuccess(orderBeanList?.list)
            }

            override fun _onError(code: Int, message: String?) {
                ui.onListServerError()
            }

        }, MyConfiguration.getInstance().getMyLongtitude(activity), MyConfiguration.getInstance().getMyLatitude(activity),size)
    }


    interface IMainHomeReceive : BaseUI {
        fun onAuthSuccess(customerBean: CustomerBean?)
        fun onWaitReceiveListSuccess(orderBeanList: List<OrderM>?)
        fun onListServerError()

        fun getAdapter(): ReceiveListAdapter

    }

}