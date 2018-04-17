package net.ed58.dlm.clients.main

import android.os.Bundle
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.entity.CustomerM
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

/**
 * Created by sunpeng on 17/2/10.
 */

class MainMePresent : BasePresenter<MainMePresent.IMainMe>() {

    fun getMe() {

        val subscribe: RxSubscriber<CustomerM> = object : RxSubscriber<CustomerM>(activity) {

            override fun _onNext(t: CustomerM?) {
                if (t != null) {
                    ui.showMe(t)
                };
            }

            override fun _onError(code: Int, message: String?) {

            }

        }

        HttpMethods.getInstance().getMe(subscribe)
    }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
    }


    interface IMainMe : BaseUI {
        fun showMe(date:CustomerM);
    }

}