package net.ed58.dlm.clients.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.wise.common.commonutils.DisplayUtil
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.view.numbervcodeview.BaseNumberCodeView
import net.ed58.dlm.clients.view.numbervcodeview.BottomSheetNumberCodeView
import net.ed58.dlm.clients.view.numbervcodeview.CenterNumberCodeView

/**
 * Created by sunpeng on 17/11/21.
 */

class InputPasswordDialog : Dialog, View.OnClickListener {


    var mNumberCodeView: BottomSheetNumberCodeView? = null
    var mCenterCodeView: CenterNumberCodeView? = null
    lateinit var text_tip: TextView
    var text_title: TextView
    lateinit var text_resend: TextView
    lateinit var text_contact: TextView
    lateinit var view: View

    var type: Int = 0

    constructor(mContext: Context, type: Int) : super(mContext, R.style.ActionSheetDialogStyle) {
        this.type = type
        //填充对话框的布局

        when (type) {
            1, 2 -> {
                view = LayoutInflater.from(context).inflate(R.layout.dialog_input_password, null)
            }
            3 -> {
                view = LayoutInflater.from(context).inflate(R.layout.dialog_center_password, null)
            }
        }


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


        text_title = view.findViewById<TextView>(R.id.text_title)


        if (type == 1) {
            //取货码
            initView("请输入取货码")
        } else if (type == 2) {
            //请输入收货码
            initView("请输入收货码")
        } else if (type == 3) {
            text_title.text = "请输入支付密码"
            mCenterCodeView = view.findViewById<CenterNumberCodeView>(R.id.center_number_code_view)
            mCenterCodeView?.setIsPassword(true)
        }
    }

    private fun initView(title: String) {
        text_title.text = title
        text_tip = view.findViewById<TextView>(R.id.text_tip)
        mNumberCodeView = view.findViewById<BottomSheetNumberCodeView>(R.id.bottom_sheet_number_code_view)
        mNumberCodeView?.setIsPassword(true)
        text_resend = view.findViewById<TextView>(R.id.text_resend)
        text_contact = view.findViewById<TextView>(R.id.text_contact)
        text_contact.setOnClickListener(this)
    }

    fun setTipText(tip: CharSequence, color: Int) {
        text_tip.text = tip
        text_tip.setTextColor(color)
    }

    fun setReSendClickListener(onClickListener: View.OnClickListener) {
        text_resend.setOnClickListener(onClickListener)

    }

    fun restoreViews() {
        mNumberCodeView?.restoreViews()
        mCenterCodeView?.restoreViews()
    }

    override fun onClick(view: View?) {
        when (view?.id) {

            R.id.text_contact -> {

            }
        }
    }

    fun setNumberCodeCallback(callback: BaseNumberCodeView.OnInputNumberCodeCallback) {
        mNumberCodeView?.setNumberCodeCallback(callback)
    }

    fun setOnHideBottomLayoutListener(callback: BottomSheetNumberCodeView.OnHideBottomLayoutListener) {
        mNumberCodeView?.setOnHideBottomLayoutListener(callback)
    }


    fun setCenterNumberCodeCallback(callback: BaseNumberCodeView.OnInputNumberCodeCallback) {
        mCenterCodeView?.setNumberCodeCallback(callback)
    }

    fun setOnHideCenterLayoutListener(callback: CenterNumberCodeView.OnHideBottomLayoutListener) {
        mCenterCodeView?.setOnHideBottomLayoutListener(callback)
    }

    fun disMiss() {
        if (isShowing) {
            dismiss()
        }
    }
}
