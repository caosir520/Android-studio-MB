package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_remark_information.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreActivity

class RemarkInformationActivity : BaseCoreActivity(),View.OnClickListener {


    override fun getLayoutId(): Int {
        return R.layout.activity_remark_information
    }

    var markContent:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        markContent = intent.getStringExtra("markContent")
        editText.setText(markContent)
        val length = 100-markContent.length
        text_length.text = "$length"

        remark_close.setOnClickListener(this)
        remark_submit.setOnClickListener(this)

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val length = 100 - s.toString().length
                text_length.text = "$length"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }


        })

    }


    override fun onClick(view: View?) {

        when(view?.id){
            R.id.remark_close ->{
                onBackPressed()
            }

            R.id.remark_submit ->{
                val intent = Intent()
                intent.putExtra("markContent",editText.text.toString().trim())
                setResult(Activity.RESULT_OK,intent)
                onBackPressed()
            }
        }

    }

    companion object {
        fun startActivityForResult(context: Activity, markContent: String?, requestCode: Int){
            val intent = Intent(context, RemarkInformationActivity::class.java)
            intent.putExtra("markContent", markContent)
            context.startActivityForResult(intent, requestCode)
        }
    }
}
