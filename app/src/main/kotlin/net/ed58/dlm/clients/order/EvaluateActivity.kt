package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.wise.common.baserx.RxBus
import com.wise.common.commonutils.ToastUtil
import kotlinx.android.synthetic.main.activity_evaluate.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.entity.ReviewBean
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber

/**
 *
 *
 *@author xionghongjie
 *@date 2017/11/7
 *
 */

class EvaluateActivity : BaseCoreActivity(), View.OnClickListener {

    var orderId: String = ""
    var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderId = intent.getStringExtra("orderId")
        type = intent.getIntExtra("type", 1)

        evaluate_close.setOnClickListener(this)
        evaluate_submit.setOnClickListener(this)

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val Length = 100 - s.toString().length
                evaluate_length.text = "$Length"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 100) {
                    Toast.makeText(applicationContext, "只能写100个字哦！", Toast.LENGTH_SHORT).show()
                }
            }
        })


    }

    override fun getLayoutId(): Int {
        return R.layout.activity_evaluate
    }

    override fun onClick(view: View?) {

        when (view?.id) {

            R.id.evaluate_close -> {
                onBackPressed()
            }

            R.id.evaluate_submit -> {
                HttpMethods.getInstance().addReview(object : RxSubscriber<ReviewBean>(this) {

                    override fun _onNext(reviewBean: ReviewBean?) {
                        ToastUtil.showBottomtoast(this@EvaluateActivity, "评价成功")
                        when (type) {
                            1 -> RxBus.getInstance().post(RxKey.EVENT_ALL_RECEIVE_LIST_ITEM, -1)
                            2 -> RxBus.getInstance().post(RxKey.EVENT_ALL_SEND_LIST_ITEM, -1)
                            3 -> setResult(Activity.RESULT_OK)
                        }
                        onBackPressed()
                    }

                    override fun _onError(code: Int, message: String?) {

                    }

                }, orderId, if (type == 3) 2 else type, ratingBar.star, editText.text.toString().trim())
            }

        }

    }


    companion object {
        /**
         * type 1=骑手对发单人的评价； 2=发单人对骑手的评价
         */
        fun startActivity(context: Activity, orderId: String, type: Int) {
            val intent = Intent(context, EvaluateActivity::class.java)
            intent.putExtra("orderId", orderId)
            intent.putExtra("type", type)
            context.startActivity(intent)
        }

        /**
         * type 1=骑手对发单人的评价； 2=发单人对骑手的评价  这边有个传3的情况是已结单列表操作评价的时候
         */
        fun startActivityForResult(context: Activity, orderId: String, type: Int, requestCode: Int) {
            val intent = Intent(context, EvaluateActivity::class.java)
            intent.putExtra("orderId", orderId)
            intent.putExtra("type", type)
            context.startActivityForResult(intent, requestCode)
        }
    }


}
