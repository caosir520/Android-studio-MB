package net.ed58.dlm.clients.order

import android.util.Log
import com.wise.common.baseapp.AppManager
import com.wise.common.baserx.RxJavaDemoUtil
import com.wise.common.commonutils.LogUtil
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.entity.ComplaintBean
import net.ed58.dlm.clients.entity.ComplaintReason
import net.ed58.dlm.clients.entity.SingleIntValue
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.util.CommonDialogUtil
import rx.Subscriber

/**
 * Created by XiongHongJie on 2017/11/17.
 */
class ComplaintsReportPresent : BasePresenter<ComplaintsReportPresent.IComplaintReport>() {

    fun complaintAdd(complaintObject:Int,name: String,mobile: String,complaintReasonIds: String ,otherReason: String){

        HttpMethods.getInstance().complaintAdd(object: RxSubscriber<ComplaintBean>(activity){
            override fun _onNext(t: ComplaintBean?) {
                val dialog = CommonDialogUtil.showOriginSuccessDialog(activity,"提交成功！")




                RxJavaDemoUtil.CountDowm(object : Subscriber<Long>(){
                    override fun onError(e: Throwable?) {
                    }

                    override fun onCompleted() {
                    }

                    override fun onNext(time: Long?) {
                        if (time?.toInt() == 2) {
                            dialog.dismiss()
                            AppManager.getAppManager().finishActivity()
                        }
                    }

                }, 10)
                Log.d("tag","chengg")
            }

            override fun _onError(code: Int, message: String?) {
                LogUtil.d("失败")

            }

        },complaintObject,name,mobile,complaintReasonIds,otherReason)


    }

    fun complaintReasonList(complaintObject: Int,start: String,limit: String){
        HttpMethods.getInstance().getcomplaintReasonList(object : RxSubscriber<ComplaintReason>(activity){
            override fun _onNext(t: ComplaintReason?) {
                LogUtil.d("chenggong" + t?.total + t?.list)
                ui.showReasonList(t?.list)

            }

            override fun _onError(code: Int, message: String?) {

            }

        },complaintObject,start,limit)
    }

    interface IComplaintReport: BaseUI {

        fun showReasonList(reason:List<ComplaintReason.ReasonList>?)
//        fun callBack()
    }
}
