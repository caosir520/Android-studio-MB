package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.wise.common.commonutils.ToastUtil
import kotlinx.android.synthetic.main.activity_order_undistribute_reason.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.global.Constant

/**
 * Created by Administrator on 2017/11/14.
 */
class OrderUndistributeReasonActivity : BaseCoreMVPActivity<OrderUndistributeReasonPresent,OrderUndistributeReasonPresent.IOrderUndistributeReason>(), View.OnClickListener, OrderUndistributeReasonPresent.IOrderUndistributeReason {

    private var orderId = ""
    private var position =-1//点的item位置
    private var type =-1//1是发单人取消订单，2是骑手无法配送
    private var index: Int = -1//type==2 无法配送取消订单的时候需要用，用于在哪个tab操作的

    override fun callBack() {
    }

    override fun createPresenter(): OrderUndistributeReasonPresent {
        return OrderUndistributeReasonPresent()
    }

    override fun getUi(): OrderUndistributeReasonPresent.IOrderUndistributeReason {
        return this
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = intent.getStringExtra("orderId")
        position = intent.getIntExtra("clickPos",-1)
        type = intent.getIntExtra("type",-1)
        index = intent.getIntExtra("index",-1)


        if (type == 1) {
            text_title.text = "取消原因"
            reason_edit.hint = "请输入取消具体原因"
        }else if (type == 2) {
            text_title.text = "无法配送"
            reason_edit.hint = "请输入无法配送原因"
        }

        reason_close.setOnClickListener(this)
        button_confirm.setOnClickListener(this)



        reason_edit.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val length =100 - s.toString().length
                reason_edit_length.text = "$length"
                if (length == 0){
                    ToastUtil.showBottomtoast(this@OrderUndistributeReasonActivity,"最多只能有100个字")
                }
                button_confirm.isEnabled = length != 100
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_undistribute_reason
    }

    override fun onClick(view: View?) {

        when(view?.id){


            R.id.reason_close -> {
                onBackPressed()
            }

            R.id.button_confirm -> {
                presenter.cancelorder(orderId,reason_edit.text.toString(),type,index)
            }
        }
    }

    override fun getPosition(): Int {
        return position
    }


    companion object {

        //type 1是发单列表取消订单  2是我的接单列表无法配送
        fun startActivityForResult(context: Activity, orderId: String, position:Int, type :Int, index:Int) {
            val intent = Intent(context, OrderUndistributeReasonActivity::class.java)
            intent.putExtra("orderId",orderId)
            intent.putExtra("clickPos",position)
            intent.putExtra("type",type)
            intent.putExtra("index", index)
            context.startActivityForResult(intent,Constant.REQUEST_FIRST_CODE)
        }
    }
}