package net.ed58.dlm.clients.login


import com.wise.common.baserx.RxJavaDemoUtil
import com.wise.common.commonutils.LogUtil
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.entity.CustomerBean
import net.ed58.dlm.clients.entity.SingleIntValue
import net.ed58.dlm.clients.login.LoginContract.Present
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import rx.Subscriber


/**
 * 创建人: caosir
 * 创建时间：2017/10/18
 * 修改人：
 * 修改时间：
 * 类说明：
 */

class LoginPresent(ui: LoginContract.UI, context: BaseCoreActivity) : Present() {
    //构造函数
    init {
        onUiReady(ui, context)
    }

    /**
     * 说明:发送验证码
     * @author: caosir
     * @time：2017/10/18
     * @param：
     * @return
     */
    override fun sendYanZhenMa(number: String) {
        LogUtil.d("发送验证短信")
        val httpMethods = HttpMethods.getInstance()
        val subscriber = object : RxSubscriber<SingleIntValue>(activity) {


            override fun _onNext(singleIntValue: SingleIntValue) {
                val subscriber1 = object : Subscriber<Long>() {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onNext(aLong: Long) {
                        LogUtil.d("" + aLong)
                        val string = "" + (59 - aLong) + "s"

                        if (aLong.toInt() == 59) {
                            ui.showTextSend()
                        } else {
                            ui.showTextTime(string)
                        }

                    }
                }
                RxJavaDemoUtil.CountDowm(subscriber1, 60)
            }

            override fun _onError(code: Int, message: String) {

            }
        }
        httpMethods.sendLoginSmsCode(subscriber, number)
    }

    /**
     * 说明:发送登陆请求
     * @author: caosir
     * @time：2017/10/18
     * @param：
     * @return
     */
    override fun login(number: String, YanZhenMa: String) {
        LogUtil.d("登陆操作")
        //登陆的subscribe
        val subscriber = object : RxSubscriber<CustomerBean>(activity) {

            override fun _onNext(customerBean: CustomerBean) {
                ui.showLoginSuccess()
            }

            override fun _onError(code: Int, message: String) {

            }
        }
        HttpMethods.getInstance().login(subscriber, number, YanZhenMa)
    }


}
