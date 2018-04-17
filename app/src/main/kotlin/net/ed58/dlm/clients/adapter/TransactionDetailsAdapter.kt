package net.ed58.dlm.clients.adapter

import android.content.Context
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.entity.AccountLogM

/**
 * Created by XiongHongJie on 2017/11/27.
 */
class TransactionDetailsAdapter(context: Context):CommonRecycleViewAdapter<AccountLogM.AccountLogMList>(context, R.layout.item_transaction_details) {


    override fun convert(helper: ViewHolderHelper?, list: AccountLogM.AccountLogMList?) {
        helper?.setText(R.id.accrualsFee ,list?.symbol+ list?.accrualsFee.toString())
        helper?.setText(R.id.createTime,list?.createTime)
        helper?.setText(R.id.logType,list?.logTypeDesc)
    }

}