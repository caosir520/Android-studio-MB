package net.ed58.dlm.clients.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.wise.common.commonutils.DisplayUtil
import kotlinx.android.synthetic.main.dialog_add_account.*
import net.ed58.dlm.clients.R

/**
 * Created by sunpeng on 17/11/21.
 */

class AddCountDialog : Dialog, View.OnClickListener {


    lateinit var mContext: Context
    var iListener:ISubmitListener? = null


    constructor(mContext: Context) : super(mContext, R.style.ActionSheetDialogStyle) {
        //填充对话框的布局
        this.mContext = mContext
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_account, null)

        //将布局设置给Dialog
        setContentView(view)
        setCanceledOnTouchOutside(true)
        //获取当前Activity所在的窗体
        val dialogWindow = window
        //设置Dialog从窗体底部弹出
        dialogWindow!!.setGravity(Gravity.BOTTOM)
        //获得窗体的属性
        val lp = dialogWindow.attributes
        lp.width = DisplayUtil.getScreenWidth(mContext)
        //       将属性设置给窗体
        dialogWindow.attributes = lp


        im_back.setOnClickListener(this)
        button_confirm.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.button_confirm -> {
                iListener?.submit(edit_name.text.toString().trim(),edit_account.text.toString().trim())
            }
            R.id.im_back->{
                disMiss()
            }
        }
    }

    fun setAccountInfo(name:String,account:String) {
        edit_name.setText(name)
        edit_account.setText(account)
    }

    fun setTitle(title:String) {
        text_title.text = title
    }



    fun disMiss() {
        if (isShowing) {
            dismiss()
        }
    }

    interface ISubmitListener {
        fun submit(name:String,account:String)
    }
}