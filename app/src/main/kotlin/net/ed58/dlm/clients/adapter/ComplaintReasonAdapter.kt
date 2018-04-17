package net.ed58.dlm.clients.adapter

import android.content.Context
import android.view.View
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.entity.ComplaintReason

/**
 * Created by XiongHongJie on 2017/11/23.
 */
class ComplaintReasonAdapter(context: Context?, mDatass: List<ComplaintReason.ReasonList>?) : CommonRecycleViewAdapter<ComplaintReason.ReasonList>(context, R.layout.complaint_reason_item,mDatass) {

    override fun convert(helper: ViewHolderHelper?, t: ComplaintReason.ReasonList?) {
        helper?.setText(R.id.complaint_reason, t?.reason)
        helper?.getView<View>(R.id.reason_button)?.setSelected(t!!.isSelect)
    }

}