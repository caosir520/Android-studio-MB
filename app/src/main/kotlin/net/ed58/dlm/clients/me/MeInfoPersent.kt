package net.ed58.dlm.clients.me

import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.entity.CustomerM
import net.ed58.dlm.clients.entity.SingleStringValue
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Func1
import rx.schedulers.Schedulers
import java.io.File

/**
 * Created by Administrator on 2017/11/27/027.
 */

class MeInfoPersent : BasePresenter<MeInfoPersent.Ui>() {

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

    fun upDate(nickName: String, head: String, sex: Int, address: String) {
        var file: File? = null
        if (!head.contains("http")) {
            file = File(head)
        }

        HttpMethods.getInstance().upload(null, head, file)
                .subscribeOn(Schedulers.io()) //回到IO线程发送更改数据请求
                .flatMap(Func1<SingleStringValue, Observable<CustomerM>> { singleStringValue ->
                    HttpMethods.getInstance().update(nickName, singleStringValue.value, sex, address)
                })
                .subscribeOn(Schedulers.io()) //在IO请求中发送图片
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : RxSubscriber<CustomerM>(activity) {
                    override fun _onNext(t: CustomerM?) {
                        ui.update()
                    }

                    override fun _onError(code: Int, message: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
    }


    interface Ui : BaseUI {

        fun showMe(date: CustomerM);
        fun update();
    }

}
