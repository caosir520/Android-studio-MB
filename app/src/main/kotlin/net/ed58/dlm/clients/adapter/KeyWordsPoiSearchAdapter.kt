package net.ed58.dlm.clients.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.amap.api.services.core.PoiItem
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import com.wise.common.baseapp.AppManager
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreActivity

/**
 * Created by sunpeng on 17/10/26.
 */
class KeyWordsPoiSearchAdapter(context: Context?, mDatass: List<PoiItem>?) : CommonRecycleViewAdapter<PoiItem>(context, R.layout.item_poi_keyword_search, mDatass) {
    private var type: Int = -1

    override fun convert(helper: ViewHolderHelper?, poiItem: PoiItem?) {
        helper?.setText(R.id.text_address, "${poiItem?.toString()}")

        helper?.setOnClickListener(R.id.layout_item) {
            val intent = Intent()
            intent.putExtra("latitude",poiItem?.latLonPoint?.latitude)
            intent.putExtra("longtitude",poiItem?.latLonPoint?.longitude)
            (mContext as BaseCoreActivity).setResult(Activity.RESULT_OK, intent)
            AppManager.getAppManager().finishActivity()
        }
    }

    fun setType(type: Int) {
        this.type = type
    }


}