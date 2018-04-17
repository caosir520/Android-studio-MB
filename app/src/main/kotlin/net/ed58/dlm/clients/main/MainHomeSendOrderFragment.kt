package net.ed58.dlm.clients.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.UiSettings
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.TranslateAnimation
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.wise.common.commonutils.DisplayUtil
import com.wise.common.commonutils.LogUtil
import com.wise.foundation.base.onNotNull
import kotlinx.android.synthetic.main.fragment_send_order.*
import net.ed58.dlm.clients.MyApplication
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.address.MapSelectAddressActivity
import net.ed58.dlm.clients.base.BaseCoreMVPFragment
import net.ed58.dlm.clients.entity.AddressInfoBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.order.FillAddressInfoActivity
import net.ed58.dlm.clients.order.FillOrderActivity
import net.ed58.dlm.clients.order.OpinionFeedbackActivity
import net.ed58.dlm.clients.order.RiderEvaluationRecordActivity
import net.ed58.dlm.clients.sp.SPKey
import net.ed58.dlm.clients.sp.SettingUtils
import net.ed58.dlm.clients.wallet.MyWalletActivity
import net.ed58.dlm.clients.wallet.TransactionDetailsActivity

/**
 * Created by sunpeng on 17/2/10.
 */

class MainHomeSendOrderFragment : BaseCoreMVPFragment<MainHomePresent, MainHomePresent.IMainScan>(), MainHomePresent.IMainScan, LocationSource, AMapLocationListener, AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener {

    private var mView: View? = null
    private var aMap: AMap? = null
    private var mlocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null
    private var mUiSettings: UiSettings? = null
    private var geocoderSearch: GeocodeSearch? = null
    private var screenMarker: Marker? = null

    private var topAddressInfo: AddressInfoBean? = null
    private var bottomAddressInfo: AddressInfoBean? = null

    override fun createPresenter(): MainHomePresent {
        return MainHomePresent()
    }

    override fun getUi(): MainHomePresent.IMainScan {
        return this
    }

    override fun createView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (mView == null) {
            mView = inflater?.inflate(R.layout.fragment_send_order, container, false)
        }
        return mView!!
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)// 此方法必须重写
        geocoderSearch = GeocodeSearch(activity)
        geocoderSearch!!.setOnGeocodeSearchListener(this)
        if (aMap == null) {
            aMap = mapView.map
            setUpMap()
        }
        initView()

    }

    private fun initView() {
        //初始化对象
        topAddressInfo = AddressInfoBean()
        bottomAddressInfo = AddressInfoBean()


        view_map_top.setOnClickListener {
            //填写信息
            topAddressInfo.onNotNull {
                FillAddressInfoActivity.startActivity(activity, FillAddressInfoActivity.SEND_TYPE, topAddressInfo!!, Constant.REQUEST_FIRST_CODE)
            }

        }
        view_map_bottom.setOnClickListener {
            //地图选址
            if (bottomAddressInfo!!.isCompleted) {
                FillAddressInfoActivity.startActivity(activity, FillAddressInfoActivity.RECEIVE_TYPE, bottomAddressInfo!!)
            } else {
                MapSelectAddressActivity.startActivity(activity, FillAddressInfoActivity.RECEIVE_TYPE, topAddressInfo?.latitude!!, topAddressInfo?.longtitude!!)
            }
        }

        mRxManager.on<AddressInfoBean>(RxKey.EVENT_UPDATE_MAIN_BOTTOM_ADDRESS) { addressInfoBean ->
            //下面的地址填写回填
            bottomAddressInfo = addressInfoBean
            bottomAddressInfo?.apply {
                this.isCompleted = true
            }
            text_receive_address.text = "${bottomAddressInfo?.address} ${bottomAddressInfo?.detailAddress}"
            text_bottom_person_info.text = "${bottomAddressInfo?.name} ${bottomAddressInfo?.phone}"
            text_bottom_person_info.visibility = View.VISIBLE
            if (topAddressInfo!!.isCompleted) {
                FillOrderActivity.startActivity(activity, topAddressInfo, bottomAddressInfo)
            }

            //显示"收"的marker标记并隐藏原来的中心点和地图滑动改变位置的监听
            screenMarker?.isVisible = false //隐藏中心点的marker
            geocoderSearch!!.setOnGeocodeSearchListener(null)//滑动地图取消
            aMap?.addMarker(MarkerOptions().position(
                    LatLng(bottomAddressInfo?.latitude!!, bottomAddressInfo?.longtitude!!)).icon(
                    BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_receive)))
            val latLng = LatLng(bottomAddressInfo?.latitude!!, bottomAddressInfo?.longtitude!!)
//                设置中心点和缩放比例
            aMap?.moveCamera(CameraUpdateFactory.changeLatLng(latLng))
        }

        mRxManager.on<Boolean>(RxKey.EVENT_ORDER_FINISH) {
            //订单完成或者取消发单
            topAddressInfo = AddressInfoBean()
            bottomAddressInfo = AddressInfoBean()
            aMap?.clear()
            text_current_address.text = "正在定位"
            text_top_person_info.text = "请填写物品寄出人信息"
            text_receive_address.text = "物品寄到哪里去"
            text_bottom_person_info.visibility = View.GONE
            geocoderSearch!!.setOnGeocodeSearchListener(this)//滑动地图取消
            activate(null)
            addMarkerInScreenCenter()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_FIRST_CODE) {
                topAddressInfo = data?.getSerializableExtra("addressInfo") as AddressInfoBean?
                topAddressInfo?.apply {
                    topAddressInfo
                    this.isCompleted = true
                }
                text_current_address.text = "${topAddressInfo?.address} ${topAddressInfo?.detailAddress}"
                text_top_person_info.text = "${topAddressInfo?.name} ${topAddressInfo?.phone}"

                if (bottomAddressInfo!!.isCompleted) {
                    FillOrderActivity.startActivity(activity, topAddressInfo, bottomAddressInfo)
                }

                //显示"发"的marker标记并隐藏原来的中心点和地图滑动改变位置的监听
                screenMarker?.isVisible = false //隐藏中心点的marker
                geocoderSearch!!.setOnGeocodeSearchListener(null)//滑动地图取消
                aMap?.addMarker(MarkerOptions().position(
                        LatLng(topAddressInfo?.latitude!!, topAddressInfo?.longtitude!!)).icon(
                        BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_fetch)))
                val latLng = LatLng(topAddressInfo?.latitude!!, topAddressInfo?.longtitude!!)
//                设置中心点和缩放比例
                aMap?.moveCamera(CameraUpdateFactory.changeLatLng(latLng))
            }
        }
    }

    /**
     * 设置一些amap的属性
     */
    private fun setUpMap() {
        aMap?.setOnMapLoadedListener({
            addMarkerInScreenCenter()
        })
        // 自定义系统定位小蓝点
        val myLocationStyle = MyLocationStyle()
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)//定位一次，且将视角移动到地图中心点。
        myLocationStyle.showMyLocation(false)
        aMap?.myLocationStyle = myLocationStyle
        aMap?.setOnCameraChangeListener(this)// 对amap添加移动地图事件监听器
        aMap?.setLocationSource(this)// 设置定位监听
        aMap?.uiSettings!!.isMyLocationButtonEnabled = true// 设置默认定位按钮是否显示
        aMap?.uiSettings!!.isZoomControlsEnabled = false
        aMap?.isMyLocationEnabled = true// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
    }

    /**
     * 在屏幕中心添加一个Marker
     */
    private fun addMarkerInScreenCenter() {
        val latLng = aMap?.cameraPosition?.target
        val screenPosition = aMap?.projection?.toScreenLocation(latLng)
        screenMarker = aMap?.addMarker(MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.purple_pin)))
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker?.setPositionByPixels(screenPosition!!.x, screenPosition.y)

    }


    /**
     * 屏幕中心marker 跳动
     */
    private fun startJumpAnimation() {

        if (screenMarker != null) {
            //根据屏幕距离计算需要移动的目标点
            val latLng = screenMarker?.position
            val point = aMap?.projection?.toScreenLocation(latLng)
            point!!.y -= DisplayUtil.dip2px(125f)
            val target = aMap?.projection?.fromScreenLocation(point)
            //使用TranslateAnimation,填写一个需要移动的目标点
            val animation = TranslateAnimation(target)
            animation.setInterpolator { input ->
                // 模拟重加速度的interpolator
                if (input <= 0.5) {
                    (0.5f - 2.0 * (0.5 - input) * (0.5 - input)).toFloat()
                } else {
                    (0.5f - Math.sqrt(((input - 0.5f) * (1.5f - input)).toDouble())).toFloat()
                }
            }
            //整个移动所需要的时间
            animation.setDuration(600)
            //设置动画
            screenMarker?.setAnimation(animation)
            //开始动画
            screenMarker?.startAnimation()

        } else {
            Log.e("amap", "screenMarker is null")
        }
    }


    override fun deactivate() {
        if (mlocationClient != null) {
            mlocationClient!!.stopLocation()
            mlocationClient!!.onDestroy()
        }
        mlocationClient = null
    }

    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(activity)
            mLocationOption = AMapLocationClientOption()
            //设置定位监听
            mlocationClient!!.setLocationListener(this)
            //设置为高精度定位模式
            mLocationOption!!.isOnceLocation = true
            mLocationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //设置定位参数
            mlocationClient!!.setLocationOption(mLocationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        }
        mlocationClient!!.startLocation()
    }

    override fun onLocationChanged(amapLocation: AMapLocation?) {
        if (amapLocation != null) {
            if (amapLocation.errorCode == 0) {
                val marker1 = LatLng(amapLocation.latitude, amapLocation.longitude)
                SettingUtils.setEditor(MyApplication.getContext(), SPKey.KEY_MLAT, "${amapLocation.latitude}")
                SettingUtils.setEditor(MyApplication.getContext(), SPKey.KEY_MLNG, "${amapLocation.longitude}")
//                设置中心点和缩放比例
                aMap?.moveCamera(CameraUpdateFactory.changeLatLng(marker1))
                aMap?.moveCamera(CameraUpdateFactory.zoomTo(18f))
            } else {
                val errText = "定位失败," + amapLocation.errorCode + ": " + amapLocation.errorInfo
                Log.e("AmapErr", errText)
//                if (amapLocation.errorCode == 12) {
//                    //没有权限
//                    DialogPermissionUtil.PermissionDialog(activity,"你未允许驿道获取定位权限,您可在系统设置中开启")
//                }
            }
        }
    }

    override fun onCameraChangeFinish(cameraPosition: CameraPosition?) {
        startJumpAnimation()
        val target = cameraPosition.toString()
        LogUtil.d(target)
        val startIndex = target.indexOf("(")
        val endIndex = target.indexOf(")")

        val latLonString = cameraPosition.toString().substring(startIndex + 1, endIndex)
        val split = latLonString.split(",")
        var latLonPoint = LatLonPoint(split[0].toDouble(), split[1].toDouble())
        getAddress(latLonPoint)
    }

    override fun onCameraChange(cameraPosition: CameraPosition?) {

    }

    /**
     * 响应逆地理编码
     */
    private fun getAddress(latLonPoint: LatLonPoint) {
        val query = RegeocodeQuery(latLonPoint, 200f,
                GeocodeSearch.AMAP)// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch!!.getFromLocationAsyn(query)// 设置异步逆地理编码请求
    }


    override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.regeocodeAddress != null
                    && result.regeocodeAddress.formatAddress != null) {
                LogUtil.d("${result.regeocodeAddress.pois[0]}")
                text_current_address.text = "${result.regeocodeAddress.pois[0]}"
                topAddressInfo?.apply {
                    this.address = "${result.regeocodeAddress.pois[0]}"
                    this.latitude = result.regeocodeAddress.pois[0].latLonPoint.latitude
                    this.longtitude = result.regeocodeAddress.pois[0].latLonPoint.longitude
                    this.adCode = result.regeocodeAddress.pois[0].adCode
                    this.cityCode = result.regeocodeAddress.cityCode
                }
//                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
//                        AMapUtil.convertToLatLng(latLonPoint), 15f))
//                regeoMarker.setPlosition(AMapUtil.convertToLatLng(latLonPoint))
//                ToastUtil.show(this@ReGeocoderActivity, addressName)
            } else {
//                ToastUtil.show(this@ReGeocoderActivity, R.string.no_result)
            }
        } else {
//            ToastUtil.showerror(this, rCode)
        }
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        mapView?.onDestroy()
    }

}
