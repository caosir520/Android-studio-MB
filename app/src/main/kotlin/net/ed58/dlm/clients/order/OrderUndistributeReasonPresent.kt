package net.ed58.dlm.clients.order

import android.app.Activity
import android.util.Log
import com.wise.common.baseapp.AppManager
import com.wise.common.baserx.RxBus
import com.wise.common.commonutils.ToastUtil
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.entity.SingleIntValue
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

/**
 * Created by Administrator on 2017/11/14.
 */
class OrderUndistributeReasonPresent : BasePresenter<OrderUndistributeReasonPresent.IOrderUndistributeReason>() {

    fun cancelorder(orderId: String, reason: String, type: Int, index: Int) {

        if (type == 1) {
            HttpMethods.getInstance().canceleOrder(object : RxSubscriber<SingleIntValue>(activity) {


                override fun _onNext(t: SingleIntValue?) {
                    handleResponse(type, index)
                }

                override fun _onError(code: Int, message: String?) {
                    Log.d("tag", "shibai")
                }

            }, orderId, reason)
        } else {
            HttpMethods.getInstance().unableDeliver(object : RxSubscriber<SingleIntValue>(activity) {


                override fun _onNext(t: SingleIntValue?) {
                    handleResponse(type, index)
                }

                override fun _onError(code: Int, message: String?) {
                    Log.d("tag", "shibai")
                }

            }, orderId, reason)

        }


    }

    private fun handleResponse(type: Int, index: Int) {
        ToastUtil.showBottomtoast(activity, "取消订单成功")
        if (type == 1) {
            //取消订单
            when (index) {
                0, 1 -> {
                    activity.setResult(Activity.RESULT_OK)
                }
                3 -> RxBus.getInstance().post(RxKey.EVENT_ALL_SEND_LIST_ITEM, ui.getPosition())
            }

        } else {
            //无法配送
            when (index) {
            //在哪个tab
                0, 1 -> activity.setResult(Activity.RESULT_OK)
                2 -> RxBus.getInstance().post(RxKey.EVENT_ALL_RECEIVE_LIST_ITEM, -1)//全部订单列表就刷新
            }

        }
        AppManager.getAppManager().finishActivity()
    }

    interface IOrderUndistributeReason : BaseUI {
        fun callBack()
        fun getPosition(): Int
    }
}