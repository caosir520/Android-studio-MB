package net.ed58.dlm.clients.order

import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.entity.OrderM
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

/**
 * Created by Administrator on 2017/11/10/010.
 */

class OrderDetailsPresent : BasePresenter<OrderDetailsPresent.UI>() {

    fun getOrderById(id:String){
        val sub:RxSubscriber<OrderM> = object : RxSubscriber<OrderM>(activity){
            override fun _onNext(t: OrderM?) {
                if (t != null) {
                    ui.showDetails(t)
                };
            }

            override fun _onError(code: Int, message: String?) {
                if (message != null) {
                    ui.showNoDetails(message)
                }
            }
        }
        HttpMethods.getInstance().orderMById(sub,id)
    }

    interface UI : BaseUI{
        //
        fun showDetails(date:OrderM)

        fun showNoDetails(deta:String)
    }
}
