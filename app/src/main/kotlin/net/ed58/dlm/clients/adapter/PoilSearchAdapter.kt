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
import net.ed58.dlm.clients.entity.AddressInfoBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.order.FillAddressInfoActivity

/**
 * Created by sunpeng on 17/10/26.
 */
class PoilSearchAdapter(context: Context?,  mDatass: List<PoiItem>?) : CommonRecycleViewAdapter<PoiItem>(context, R.layout.shipments_adddress_item, mDatass) {

    private var type:Int = -1
    private var isFillAddressPage:Boolean = false

    override fun convert(helper: ViewHolderHelper?, poiItem: PoiItem?) {
        helper?.setText(R.id.text_distance,"${poiItem?.distance}米")
        helper?.setText(R.id.text_main_address,"${poiItem?.toString()}")
        helper?.setText(R.id.text_detail_address,"${poiItem?.snippet}")
//        LogUtil.d(poiItem?.adCode+","+poiItem?.cityCode+","+poiItem?.adName)

        helper?.setOnClickListener(R.id.layout_item) {
            val addressInfo = AddressInfoBean()
            addressInfo.apply {
                this.address = "${poiItem?.toString()}"
                this.adCode = poiItem?.adCode
                this.cityCode = poiItem?.cityCode
                this.latitude = poiItem?.latLonPoint?.latitude
                this.longtitude = poiItem?.latLonPoint?.longitude
            }
            if (isFillAddressPage) {
                //true是从填写地址页面来的
                val intent = Intent()
                intent.putExtra("addressInfo", addressInfo)
                (mContext as BaseCoreActivity).setResult(Activity.RESULT_OK, intent)
                AppManager.getAppManager().finishActivity()
            }else{
                FillAddressInfoActivity.startActivity(mContext as Activity,FillAddressInfoActivity.RECEIVE_TYPE,addressInfo,Constant.REQUEST_FIRST_CODE)
                AppManager.getAppManager().finishActivity()
            }
        }
    }


    fun setType(isFillAddressPage:Boolean,type: Int) {
        this.isFillAddressPage = isFillAddressPage
        this.type = type
    }

}