package net.ed58.dlm.clients.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.wise.common.basedialog.dialogplus.DialogPlus
import com.wise.common.basedialog.dialogplus.ViewHolder
import com.wise.common.commonutils.DisplayUtil
import com.wise.common.commonutils.PackageUtils
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.view.numbervcodeview.BottomSheetNumberCodeView

/**
 * Created by sunpeng on 17/11/10.
 */
class CommonDialogUtil {

    companion object {
        fun showConfirmDialog(mContext: Context, title: String, iOnConfirmClick: IOnConfirmClick) {
            val holder = ViewHolder(R.layout.dialog_confirm)
            var dialog = DialogPlus.newDialog(mContext)
                    .setContentHolder(holder)
                    .setBackgroundColorResourceId(R.drawable.shape_order_item)
                    .setGravity(Gravity.CENTER)
                    .setOnClickListener({ dialog1, view1 ->
                        when (view1.id) {
                            R.id.button_confirm -> {
                                dialog1.dismiss()
                                iOnConfirmClick.clickConfirm()
                            }
                            R.id.button_cancel -> dialog1?.dismiss()
                        }
                    })
                    .create()
            val holderView = dialog?.holderView
            val text_title = holderView?.findViewById<TextView>(R.id.text_title)
            text_title!!.text = title
            dialog?.show()
        }

        fun showOriginConfirmDialog(mContext: Context, title: String, iOnConfirmClick: IOnConfirmClick):Dialog {
            val dialog = Dialog(mContext, R.style.ActionSheetDialogStyle)
            //填充对话框的布局
            val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_confirm, null)
            //初始化控件
            val texttitle = view.findViewById<TextView>(R.id.text_title)
            val buttonConfirm = view.findViewById<Button>(R.id.button_confirm)
            val buttonCancel = view.findViewById<Button>(R.id.button_cancel)
            buttonConfirm.setOnClickListener({
                dialog.dismiss()
                iOnConfirmClick.clickConfirm()
            })
            buttonCancel.setOnClickListener({
                dialog.dismiss()
            })
            texttitle!!.text = title

            //将布局设置给Dialog
            dialog.setContentView(view)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            //获取当前Activity所在的窗体
            val dialogWindow = dialog.window
            //设置Dialog从窗体底部弹出
            dialogWindow!!.setGravity(Gravity.CENTER)
            //获得窗体的属性
            val lp = dialogWindow.attributes
            lp.width = (DisplayUtil.getScreenWidth(mContext)*0.8).toInt()
            //       将属性设置给窗体
            dialogWindow.attributes = lp
            dialog.show()//显示对话框

            return dialog
        }
        fun showReceiveFail(mContext: Context) {
            val holder = ViewHolder(R.layout.dialog_fail_receive_orders)
            var dialogOrder = DialogPlus.newDialog(mContext)
                    .setContentHolder(holder)
                    .setGravity(Gravity.CENTER)
                    .setBackgroundColorResourceId(R.drawable.shape_order_item)
                    .create()
            dialogOrder?.show()
        }

        fun showOriginSuccessDialog(mContext: Context, message: String):Dialog {
            val dialog = Dialog(mContext, R.style.ActionSheetDialogStyle)
            //填充对话框的布局
            val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_successful_receive_orders, null)
            //初始化控件
            val text_message = view.findViewById<TextView>(R.id.text_message)
            text_message?.text = message

            //将布局设置给Dialog
            dialog.setContentView(view)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            //获取当前Activity所在的窗体
            val dialogWindow = dialog.window
            //设置Dialog从窗体底部弹出
            dialogWindow!!.setGravity(Gravity.CENTER)
            //获得窗体的属性
            val lp = dialogWindow.attributes
            //       将属性设置给窗体
            dialogWindow.attributes = lp
            dialog.show()//显示对话框

            return dialog
        }

        fun showOriginfailDialog(mContext: Context):Dialog {
            val dialog = Dialog(mContext, R.style.ActionSheetDialogStyle)
            //填充对话框的布局
            val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_fail_receive_orders, null)

            //将布局设置给Dialog
            dialog.setContentView(view)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            //获取当前Activity所在的窗体
            val dialogWindow = dialog.window
            //设置Dialog从窗体底部弹出
            dialogWindow!!.setGravity(Gravity.CENTER)
            //获得窗体的属性
            val lp = dialogWindow.attributes
            //       将属性设置给窗体
            dialogWindow.attributes = lp
            dialog.show()//显示对话框

            return dialog
        }

        fun showInputPasswordDialog(mContext: Context) {
            val holder = ViewHolder(R.layout.dialog_input_password)
            var dialogOrder = DialogPlus.newDialog(mContext)
                    .setContentHolder(holder)
                    .setGravity(Gravity.BOTTOM)
                    .setBackgroundColorResourceId(R.drawable.shape_order_item)
                    .create()
            val holderView = dialogOrder?.holderView!!
            var mNumberCodeView = holderView.findViewById<BottomSheetNumberCodeView>(R.id.bottom_sheet_number_code_view)
            var text_tip = holderView.findViewById<TextView>(R.id.text_tip)
            var text_title = holderView.findViewById<TextView>(R.id.text_title)
            var text_resend = holderView.findViewById<TextView>(R.id.text_resend)
            var text_contact = holderView.findViewById<TextView>(R.id.text_contact)
//            mNumberCodeView.setNumberCodeCallback(mCallback)
//            mNumberCodeView.setOnHideBottomLayoutListener(mOnHideBottomLayoutListener)
            mNumberCodeView.setIsPassword(true)
            dialogOrder.show()
        }

        fun showCallDialog(mContext: Context, phone: String) {
            AlertDialog.Builder(mContext)
                    .setTitle(kotlin.String.format(mContext.resources.getString(R.string.call_phone_tip), phone))
                    //相当于点击确认按钮
                    .setPositiveButton("确认", { dialogInterface, i -> PackageUtils.callPhone(mContext, phone) }).setNegativeButton("取消", null).create().show()
        }
    }




    interface IOnConfirmClick {
        fun clickConfirm()//点击确认回调
    }
}