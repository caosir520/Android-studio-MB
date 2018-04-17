package net.ed58.dlm.clients.order

import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.entity.OrderM
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.util.CommonDialogUtil
import net.ed58.dlm.clients.util.RepeatInterfaceUtils

/**
 * Created by Administrator on 2017/11/10/010.
 */

class OrderMapDetailPresent : BasePresenter<OrderMapDetailPresent.IOrderMapDetailListener>() {

    fun getOrderById(id: String) {
        val sub: RxSubscriber<OrderM> = object : RxSubscriber<OrderM>(activity.mContext, false) {
            override fun _onNext(t: OrderM?) {
                if (t != null) {
                    ui.showDetails(t)
                }
            }

            override fun _onError(code: Int, message: String?) {
            }
        }
        HttpMethods.getInstance().orderMById(sub, id)
    }

    fun receiveOrder(orderId: String) {
        CommonDialogUtil.showOriginConfirmDialog(activity, "确认要接单吗？", object : CommonDialogUtil.IOnConfirmClick {
            override fun clickConfirm() {
                RepeatInterfaceUtils.receiveOrder(activity, orderId, null)
            }
        })
    }

    interface IOrderMapDetailListener : BaseUI {
        fun showDetails(date: OrderM)
    }
}