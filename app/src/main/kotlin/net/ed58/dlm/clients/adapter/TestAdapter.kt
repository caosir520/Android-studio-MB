package net.ed58.dlm.clients.adapter

import android.content.Context

import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import net.ed58.dlm.clients.R

/**
 * Created by sunpeng on 17/10/13.
 */

class TestAdapter(context: Context,date:List<String>) : CommonRecycleViewAdapter<String>(context, R.layout.item_text,date) {

    override fun convert(helper: ViewHolderHelper, s: String) {
        helper.setText(R.id.text,s);
    }
}
