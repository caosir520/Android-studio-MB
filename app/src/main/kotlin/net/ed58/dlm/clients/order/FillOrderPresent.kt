package net.ed58.dlm.clients.order

import com.wise.common.commonutils.TimeUtil
import com.wise.common.commonwidget.citypickerview.widget.DayPicker
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BasePresenter
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.entity.PreOrderBean
import net.ed58.dlm.clients.entity.ProductTypeListBean
import net.ed58.dlm.clients.entity.SingleStringValue
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.NoTipRxSubscriber
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by sunpeng on 17/10/31.
 */
class FillOrderPresent : BasePresenter<FillOrderPresent.IFillOrderListener>() {

    var cityPicker: DayPicker? = null

    fun submitOrder() {
        var hashMap = HashMap<String, Any>()
        hashMap.put("receiverName", ui.getReceiverName())
        hashMap.put("receiverAddress", ui.getReceiverAddress())
        hashMap.put("receiverLat", ui.getReceiverLat())
        hashMap.put("receiverLng", ui.getReceiverLng())
        hashMap.put("receiverPhone", ui.getReceiverPhone())
        hashMap.put("receiverFloorHouseNum", ui.getReceiverFloorHouseNum())
        hashMap.put("fetchName", ui.getFetchName())
        hashMap.put("fetchAddress", ui.getFetchAddress())
        hashMap.put("fetchLat", ui.getFetchLat())
        hashMap.put("fetchLng", ui.getFetchLng())
        hashMap.put("fetchPhone", ui.getFetchPhone())
        hashMap.put("fetchFloorHouseNum", ui.getFetchFloorHouseNum())
        hashMap.put("originatorRemark", ui.getOriginatorRemark())
        hashMap.put("expectedFetchTime", ui.getExpectedFetchTime())
        hashMap.put("categoryName", ui.getCategoryName())
        hashMap.put("cargoAmount", ui.getCargoAmount())
        hashMap.put("cargoWeight", ui.getCargoWeight())
        hashMap.put("supportValueId", ui.getSupportValueId())
//        hashMap.put("tips",ui.getTips())
        hashMap.put("receiverCityCode", ui.getReceiverCityCode())
        hashMap.put("receiverAdCode", ui.getReceiverAdCode())
        hashMap.put("fetchCityCode", ui.getFetchCityCode())
        hashMap.put("fetchAdCode", ui.getFetchAdCode())
        hashMap.put("orderAmount", ui.getOrderAmount())
        HttpMethods.getInstance().submitOrder(object : RxSubscriber<PreOrderBean>(activity) {
            override fun _onNext(preOrderBean: PreOrderBean?) {
                ui.goPayPage(preOrderBean!!)
            }

            override fun _onError(code: Int, message: String?) {

            }

        }, hashMap)
    }

    /**
     * 获取品类 type 1品类，type2价格类
     */
    fun getCategory() {
        activity.startProgressDialog()
        HttpMethods.getInstance().getProductType(object : RxSubscriber<ProductTypeListBean>(activity, false) {

            override fun _onNext(productBean: ProductTypeListBean?) {
                ui.setProductTypeBean(productBean?.list?.get(0)!!)
                HttpMethods.getInstance().getProductType(object : NoTipRxSubscriber<ProductTypeListBean>(activity, false) {
                    override fun _onNext(productBean: ProductTypeListBean?) {
                        ui.setMoneyTypeBean(productBean?.list?.get(0)!!)
                        getCurrentTime()
                    }

                    override fun _onError(code: Int, message: String?) {
                        activity.stopProgressDialog()
                    }
                }, 2)
            }

            override fun _onError(code: Int, message: String?) {
                activity.stopProgressDialog()
            }
        }, 1)
    }

    fun getCurrentTime() {
        HttpMethods.getInstance().getCurrentTime(object : NoTipRxSubscriber<SingleStringValue>(activity, false) {
            override fun _onNext(singleStringValue: SingleStringValue?) {
                activity.stopProgressDialog()

                val mSimpleDateFormat = SimpleDateFormat(TimeUtil.dateFormat)
                ui.setTimeAndSetVisible(mSimpleDateFormat.format(TimeUtil.toDate(singleStringValue?.value)))

                //获取时间后 初始化对话框
                val builder = DayPicker.Builder(activity)
                builder.initCurrentTime(TimeUtil.getDatelongMills(TimeUtil.dateFormatYMDHMSSSS,singleStringValue?.value))
                        .textSize(20)
                        .backgroundPop(-0x60000000)
                        .titleBackgroundColor("#ffffff")
                        .confirTextColor("#333333")
                        .titleTextColor("#333333")
                        .contentBackgroundColor("#ffffff")
                        .textColor(activity.resources.getColor(R.color.title_color))
                        .provinceCyclic(false)
                        .cityCyclic(false)
                        .districtCyclic(false)
                        .visibleItemsCount(5)
                        .itemPadding(10)
                cityPicker = builder.build()
                cityPicker?.setOnCityItemClickListener(object : DayPicker.OnCityItemClickListener {
                    override fun onSelected(vararg citySelected: String?) {
                        val selectedShowTime = citySelected[0] + citySelected[1]!!.replace(" ", "") + citySelected[2]
                        if ("今天立即发单" == selectedShowTime) {
                            ui.setSelectedTime("立即发单", citySelected[3]!!)
                        }else{
                            ui.setSelectedTime(selectedShowTime, citySelected[3]!!)
                        }

                    }

                })
            }

            override fun _onError(code: Int, message: String?) {
                activity.stopProgressDialog()
            }

        })
    }

    fun showSelectTimeDialog() {
        if (!(cityPicker?.isShow)!!) {
            cityPicker?.show()
        }
    }


    interface IFillOrderListener : BaseUI {
        fun getReceiverName(): String
        fun getReceiverAddress(): String
        fun getReceiverLat(): Double
        fun getReceiverLng(): Double
        fun getReceiverPhone(): String
        fun getReceiverFloorHouseNum(): String
        fun getFetchName(): String
        fun getFetchAddress(): String
        fun getFetchLat(): Double
        fun getFetchLng(): Double
        fun getFetchPhone(): String
        fun getFetchFloorHouseNum(): String
        fun getOriginatorRemark(): String
        fun getExpectedFetchTime(): String
        fun getCategoryName(): String
        fun getCargoAmount(): String
        fun getCargoWeight(): Double
        fun getSupportValueId(): String
        //        fun getTips():BigDecimal
        fun getReceiverCityCode(): String

        fun getReceiverAdCode(): String
        fun getFetchCityCode(): String
        fun getFetchAdCode(): String
        fun getOrderAmount(): BigDecimal

        fun setProductTypeBean(categoryProductTypeBean: ProductTypeListBean.ProductTypeBean)//设置品类
        fun setMoneyTypeBean(categoryMoneyTypeBean: ProductTypeListBean.ProductTypeBean)//设置价值
        fun goPayPage(preOrderBean: PreOrderBean)//跳转去支付页

        fun setTimeAndSetVisible(time:String)//设置当前服务器时间并展示布局
        fun setSelectedTime(selectedShowTime: String, selectedFormatTime: String)//设置选中的时间
    }

}