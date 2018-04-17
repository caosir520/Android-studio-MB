package net.ed58.dlm.clients.dialog

import android.app.Dialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import com.aspsine.irecyclerview.layout.CentralBigLayoutManager
import com.wise.common.commonutils.DisplayUtil
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.TestAdapter

/**
 * 选择text的弹出框
 * Created by Administrator on 2017/11/28/028.
 */

class SelectTextDialog: Dialog {
    private var mContext: Context

    private var view: View? = null
    constructor(context: Context, date:List<String>) : super(context, R.style.ActionSheetDialogStyle){
        mContext =context


        view = LayoutInflater.from(context).inflate(R.layout.dialog_recycle,null)

        setContentView(view!!)

        //点击外面的窗口是否消失
        setCanceledOnTouchOutside(true)
        val dialogWindow = window
        //设置Dialog从窗体底部弹出
        dialogWindow!!.setGravity(Gravity.BOTTOM)
        //获得窗体的属性
        val lp = dialogWindow.attributes
        //       将属性设置给窗体
        lp.width = DisplayUtil.getScreenWidth(mContext)
        dialogWindow.attributes = lp

        //获取RecyclerView
        val recyclerView=view!!.findViewById<RecyclerView>(R.id.dialog_recyler)
        val adapter=TestAdapter(getContext(), date)
        recyclerView.layoutManager = CentralBigLayoutManager();
        recyclerView.adapter=adapter
    }

    //设置监听回调
    interface ClickText{
        fun showText(date:String)
    }

}
