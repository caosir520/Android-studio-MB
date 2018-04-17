package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IntegerRes
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.aspsine.irecyclerview.universaladapter.recyclerview.OnItemClickListener
import com.wise.common.commonutils.LogUtil
import com.wise.common.commonutils.ToastUtil
import kotlinx.android.synthetic.main.activity_complaints_report.*
import kotlinx.android.synthetic.main.complaint_reason_item.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.ComplaintReasonAdapter
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.entity.ComplaintReason
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by XiongHongJie on 2017/11/16.
 */
class ComplaintsReportActivity : BaseCoreMVPActivity<ComplaintsReportPresent,
        ComplaintsReportPresent.IComplaintReport>(),View.OnClickListener,
        ComplaintsReportPresent.IComplaintReport {

    var adapter : ComplaintReasonAdapter? = null
    var complaintObject: Int = 1
    var reasonAll: ArrayList<String>? = null
    var mosaicReason: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pei_song_yuan.setOnClickListener(this)
        send_order_reason.setOnClickListener(this)
        worker_reason.setOnClickListener(this)
        image_back.setOnClickListener(this)
        button_confirm.setOnClickListener(this)

        presenter.complaintReasonList(1,"0","10")

        worker_name.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                button_confirm.isEnabled = worker_name.text !=null
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })


    }

    override fun showReasonList(reason: List<ComplaintReason.ReasonList>?) {
        reasonAll = ArrayList<String>()
        adapter = ComplaintReasonAdapter(this, reason)
        adapter!!.setOnItemClickListener(object : OnItemClickListener<ComplaintReason.ReasonList>{
            override fun onItemClick(parent: ViewGroup?, view: View?, t: ComplaintReason.ReasonList?, position: Int) {

                val getview= view as ViewGroup
                val image = getview.findViewById<ImageView>(R.id.reason_button)
                if (!adapter?.get(position)!!.isSelect){

                    reasonAll?.add(t?.id!!)
                    LogUtil.d(reasonAll?.size.toString())
                    adapter?.get(position)!!.isSelect = !adapter?.get(position)!!.isSelect
                    image.isSelected = adapter?.get(position)!!.isSelect
                }else{
                    reasonAll?.remove(t?.id)
                    adapter?.get(position)!!.isSelect = !adapter?.get(position)!!.isSelect
                    image.isSelected = adapter?.get(position)!!.isSelect
                }
            }

            override fun onItemLongClick(parent: ViewGroup?, view: View?, t: ComplaintReason.ReasonList?, position: Int): Boolean {
                return false
            }

        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_complaints_report
    }

    override fun onClick(view: View?) {

        when(view?.id){

            R.id.image_back -> {
                onBackPressed()
            }

            R.id.pei_song_yuan -> {
                pei_song_button.isChecked = true
                presenter.complaintReasonList(1,"0","10")
//                stopProgressDialog()
                complaintObject = 1
            }

            R.id.send_order_reason -> {
                send_order_reason_button.isChecked = true
                presenter.complaintReasonList(2,"0","10")
//                stopProgressDialog()
                complaintObject = 2
            }

            R.id.worker_reason -> {
                worker_reason_button.isChecked = true
                presenter.complaintReasonList(3,"0","10")
//                stopProgressDialog()
                complaintObject = 3
            }

            R.id.button_confirm -> {

                if(check()){
                    if (reasonAll!!.size == 1){
                        mosaicReason = reasonAll!!.get(0)
                    }else{
                        for (item in reasonAll!!){
                            mosaicReason = item + "," + mosaicReason
                        }
                    }


                    presenter.complaintAdd(complaintObject,worker_name.text.toString(),worker_mobile.text.toString(), mosaicReason,other_reason.text.toString() )
                }

            }
        }

    }

    fun check():Boolean{

        if (TextUtils.isEmpty(worker_name.text.toString().trim())){
            ToastUtil.showBottomtoast(this,"请输入业务员姓名")
            return false
        }

        if (TextUtils.isEmpty(worker_mobile.text.toString().trim())){
            ToastUtil.showBottomtoast(this,"请输入业务员电话号码")
            return false
        }

        if (reasonAll?.size == 0){
            ToastUtil.showBottomtoast(this,"请选择举报原因")
            return false
        }

        return true
    }

    override fun createPresenter(): ComplaintsReportPresent {
        return ComplaintsReportPresent()
    }

    override fun getUi(): ComplaintsReportPresent.IComplaintReport {
        return this
    }

    companion object {

        fun startActivity(context: Activity,reportCode: Int) {
            val intent = Intent(context, ComplaintsReportActivity::class.java)
            context.startActivity(intent)
        }

    }


}