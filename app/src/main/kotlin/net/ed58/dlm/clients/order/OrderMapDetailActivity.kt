package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.wise.common.baseapp.AppManager
import com.wise.common.commonutils.MoneyUtil
import kotlinx.android.synthetic.main.activity_order_map_detail.*
import kotlinx.android.synthetic.main.common_item_order.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.entity.OrderM
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.map.DrivingRouteOverlay
import net.ed58.dlm.clients.sp.MyConfiguration
import net.ed58.dlm.clients.util.CommonDialogUtil

/**
 * Created by Administrator on 2017/11/10.
 */
class OrderMapDetailActivity : BaseCoreMVPActivity<OrderMapDetailPresent, OrderMapDetailPresent.IOrderMapDetailListener>(), View.OnClickListener, OrderMapDetailPresent.IOrderMapDetailListener, RouteSearch.OnRouteSearchListener, AMap.InfoWindowAdapter {

    private var aMap: AMap? = null
    private var mRouteSearch: RouteSearch? = null

    var orderId: String = ""
    private var orderM: OrderM? = null
    var fromPage: Int = 1//来自哪个页面
    var list: List<LatLonPoint>? = null//途经点集合
    private var drivingRouteOverlay: DrivingRouteOverlay? = null
    private var startDistanceContent = ""
    private var endDistanceContent = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapView.onCreate(savedInstanceState)// 此方法必须重写

        orderId = intent.getStringExtra("orderId")
        fromPage = intent.getIntExtra("fromPage", 1)


        initMap()
        initView()
    }

    /**
     * 初始化AMap对象
     */
    private fun initMap() {

        if (aMap == null) {
            aMap = mapView.map
        }
        aMap?.uiSettings!!.isZoomControlsEnabled = false
        aMap?.setInfoWindowAdapter(this)// 添加显示infowindow监听事件
        mRouteSearch = RouteSearch(this)
        mRouteSearch?.setRouteSearchListener(this)
    }


    private fun initView() {
        check_my_order.setOnClickListener(this)
        text_receive_order.setOnClickListener(this)
        image_back.setOnClickListener(this)
        layout_send_phone.setOnClickListener(this)
        layout_receive_phone.setOnClickListener(this)

        presenter.getOrderById(orderId)
    }


    /**
     * 添加marker标记
     */
    private fun setfromandtoMarker(mPoint: LatLonPoint, mStartPoint: LatLonPoint, mEndPoint: LatLonPoint) {
        val fromAndTo = RouteSearch.FromAndTo(
                mPoint, mEndPoint)

        list = listOf(mStartPoint)
        // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        val query = RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, list, null, "")
        mRouteSearch?.calculateDriveRouteAsyn(query)// 异步路径规划驾车模式查询
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_order_map_detail
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.text_receive_order -> {
                //接单按钮
                presenter.receiveOrder(orderId)
            }

            R.id.check_my_order -> {
                if (fromPage == RECEIVE_LIST) {
                    AppManager.getAppManager().finishActivity()
                    mRxManager.post(RxKey.EVENT_GO_MY_RECEIVE_ORDER_LIST, 2)
                }
            }

            R.id.image_back -> {
                onBackPressed()
            }
            R.id.layout_send_phone->{
                CommonDialogUtil.showCallDialog(mContext, orderM?.fetchPhone!!)
            }
            R.id.layout_receive_phone->{
                CommonDialogUtil.showCallDialog(mContext, orderM?.receiverPhone!!)
            }
        }
    }

    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker?): View {
        val view = layoutInflater.inflate(R.layout.item_map_info_window,
                null)
        var text_window_info = view.findViewById<TextView>(R.id.text_window_info)
        if (marker?.equals(drivingRouteOverlay?.endMarker)!!) {
            text_window_info.text = startDistanceContent
        } else if (marker == drivingRouteOverlay?.throughPointMarkerList?.get(0)) {
            text_window_info.text = endDistanceContent
        }
        return view
    }

    override fun onDriveRouteSearched(result: DriveRouteResult?, errorCode: Int) {
        //绘制地图路线
        aMap?.clear()// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result?.paths != null) {
                if (result.paths.size > 0) {
                    val drivePath = result.paths[0]
                    drivingRouteOverlay = DrivingRouteOverlay(
                            mContext, aMap, drivePath,
                            result.startPos,
                            result.targetPos, list)
                    drivingRouteOverlay?.setNodeIconVisibility(false)//设置节点marker是否显示
                    drivingRouteOverlay?.removeFromMap()
                    drivingRouteOverlay?.addToMap()
                    drivingRouteOverlay?.zoomToSpan()
                } else if (result.paths == null) {
//                    ToastUtil.show(mContext, R.string.no_result)
                }

            } else {
//                ToastUtil.show(mContext, R.string.no_result)
            }
        } else {
//            ToastUtil.showerror(this.applicationContext, errorCode)
        }
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
    }

    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
    }


    /**
     * UI展示
     */
    override fun showDetails(data: OrderM) {
        this.orderM = data
        text_state.text = ""
        layout_body.visibility = View.VISIBLE
        text_order_no.text = data.orderNo
        text_send_address.text = data.fetchAddress + data.fetchFloorHouseNum
        text_receive_address.text = data.receiverAddress + data.receiverFloorHouseNum
        text_category.text = data.categoryName
        if (!TextUtils.isEmpty(data.originatorRemark)) {
            text_mark.text = data.originatorRemark
        } else {
            text_mark.text = "无"
        }
        text_price.text = "${data.orderAmount}"
        text_weight.text = "${data.cargoWeight}公斤"

        text_send_name.text = data.fetchName
        text_send_phone.text = data.fetchPhone
        text_receive_name.text = data.receiverName
        text_receive_phone.text = data.receiverPhone

        when (fromPage) {
            RECEIVE_LIST -> {

            }
            MY_RECEIVE_LIST -> {
                //隐藏查看我的订单 并且设置时间,显示发件人电话和收件人电话
//                layout_receive_phone.visibility = View.VISIBLE
//                layout_send_phone.visibility = View.VISIBLE

                check_my_order.visibility = View.GONE
                text_receive_order.text = data.createTime

                text_receive_order.setBackgroundResource(R.color.white)
                text_receive_order.setTextColor(resources.getColor(R.color.title_color))
            }
        }

        val mPoint = LatLonPoint(MyConfiguration.getInstance().getMyLatitude(this), MyConfiguration.getInstance().getMyLongtitude(this))
        val mStartPoint = LatLonPoint(orderM?.fetchLat!!, orderM?.fetchLng!!)
        val mEndPoint = LatLonPoint(orderM?.receiverLat!!, orderM?.receiverLng!!)

        setfromandtoMarker(mPoint, mStartPoint, mEndPoint)

        //计算距离
        val mPointlatlng = LatLng(MyConfiguration.getInstance().getMyLatitude(this), MyConfiguration.getInstance().getMyLongtitude(this))
        val mStartlatlng = LatLng(orderM?.fetchLat!!, orderM?.fetchLng!!)
        val mEndlatlng = LatLng(orderM?.receiverLat!!, orderM?.receiverLng!!)
        startDistanceContent = "${MoneyUtil.MoneyFomatWithTwoPoint((AMapUtils.calculateLineDistance(mPointlatlng, mStartlatlng) / 1000).toDouble())}km"
        endDistanceContent = "${MoneyUtil.MoneyFomatWithTwoPoint((AMapUtils.calculateLineDistance(mPointlatlng, mEndlatlng) / 1000).toDouble())}km"
    }


    override fun getUi(): OrderMapDetailPresent.IOrderMapDetailListener {
        return this
    }

    override fun createPresenter(): OrderMapDetailPresent {
        return OrderMapDetailPresent()
    }

    companion object {
        const val RECEIVE_LIST: Int = 1//接单list
        const val MY_RECEIVE_LIST: Int = 2//我已经接的单list

        fun startActivity(context: Activity, orderId: String, fromPage: Int) {
            val intent = Intent(context, OrderMapDetailActivity::class.java)
            intent.putExtra("orderId", orderId)
            intent.putExtra("fromPage", fromPage)
            context.startActivity(intent)
        }
    }

    /**
     * 方法必须重写
     */
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    /**
     * 方法必须重写
     */
    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    /**
     * 方法必须重写
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    /**
     * 方法必须重写
     */
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}
