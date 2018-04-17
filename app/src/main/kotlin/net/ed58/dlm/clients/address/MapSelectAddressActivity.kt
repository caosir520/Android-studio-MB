package net.ed58.dlm.clients.address

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.TranslateAnimation
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.wise.common.baseapp.AppManager
import com.wise.common.commonutils.DisplayUtil
import com.wise.common.commonutils.LogUtil
import kotlinx.android.synthetic.main.activity_map_select_address.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.PoilSearchAdapter
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.history.HistoryActivity
import net.ed58.dlm.clients.order.FillAddressInfoActivity

class MapSelectAddressActivity : BaseCoreActivity(), LocationSource, AMapLocationListener, AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener, View.OnClickListener {


    private var aMap: AMap? = null
    private var mlocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null
    private var geocoderSearch: GeocodeSearch? = null
    private var screenMarker: Marker? = null

    var type: Int = -1//-1是从填写地址页面来的
    var isFillAddressPage: Boolean = false
    var latitude: Double = 0.00
    var longtitude: Double = 0.00

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        mapView.onCreate(savedInstanceState)// 此方法必须重写
        geocoderSearch = GeocodeSearch(this)
        geocoderSearch!!.setOnGeocodeSearchListener(this)
        if (aMap == null) {
            aMap = mapView.map
            setUpMap()
        }

        mRxManager.on<String>("sendMapSelectAddressActivity") { address ->
            LogUtil.d("dasdas", address)

        }
    }

    private fun initView() {
        type = intent.getIntExtra("type", -1)
        if (type == FillAddressInfoActivity.SEND_TYPE) {
            text_hint.hint = "请输入发货地址"
        } else if (type == FillAddressInfoActivity.RECEIVE_TYPE) {
            text_hint.hint = "请输入收货地址"
        }
        latitude = intent.getDoubleExtra("latitude", 0.00)
        longtitude = intent.getDoubleExtra("longtitude", 0.00)
        isFillAddressPage = intent.getBooleanExtra("isFillAddressPage", false)

        image_back.setOnClickListener(this)
        layout_input_address.setOnClickListener(this)
        image_common_address.setOnClickListener(this)
        image_locaiton.setOnClickListener(this)
    }


    /**
     * 设置一些amap的属性
     */
    private fun setUpMap() {

        aMap?.setOnMapLoadedListener({
            addMarkerInScreenCenter()

        })
        aMap?.setOnCameraChangeListener(this)// 对amap添加移动地图事件监听器
        aMap?.setLocationSource(this)// 设置定位监听
        aMap?.uiSettings!!.isZoomControlsEnabled = false
        aMap?.uiSettings!!.isMyLocationButtonEnabled = false
        aMap?.isMyLocationEnabled = false// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()
        val marker1 = LatLng(latitude, longtitude)
//                设置中心点和缩放比例
        aMap?.moveCamera(CameraUpdateFactory.changeLatLng(marker1))
        aMap?.moveCamera(CameraUpdateFactory.zoomTo(18f))
    }

    /**
     * 在屏幕中心添加一个Marker
     */
    private fun addMarkerInScreenCenter() {

        val latLng = LatLng(latitude, longtitude)
        val screenPosition = aMap?.projection?.toScreenLocation(latLng)
        if (type == FillAddressInfoActivity.SEND_TYPE) {
            //显示"发"图标
            screenMarker = aMap?.addMarker(MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_fetch)))
        } else if (type == FillAddressInfoActivity.RECEIVE_TYPE) {
            //显示"收"图标
            screenMarker = aMap?.addMarker(MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_receive)))
        }



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
        LogUtil.d("activate")
        if (mlocationClient == null) {
            mlocationClient = AMapLocationClient(this)
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
        LogUtil.d("onLocationChanged")
        if (amapLocation != null) {
            if (amapLocation.errorCode == 0) {
                val marker1 = LatLng(amapLocation.latitude, amapLocation.longitude)
//                设置中心点和缩放比例
                aMap?.moveCamera(CameraUpdateFactory.changeLatLng(marker1))
                aMap?.moveCamera(CameraUpdateFactory.zoomTo(18f))
            } else {
                val errText = "定位失败," + amapLocation.errorCode + ": " + amapLocation.errorInfo
                Log.e("AmapErr", errText)
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
//        searchPoiItem(latLonPoint)
        getAddress(latLonPoint)
    }

    override fun onCameraChange(cameraPosition: CameraPosition?) {

    }

    /**
     * 响应逆地理编码
     */
    private fun getAddress(latLonPoint: LatLonPoint) {
        val query = RegeocodeQuery(latLonPoint, 1000f,
                GeocodeSearch.AMAP)// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch!!.getFromLocationAsyn(query)// 设置异步逆地理编码请求
    }


    override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.regeocodeAddress != null
                    && result.regeocodeAddress.formatAddress != null) {
//                searchPoiItem("${result.regeocodeAddress.pois[0]}", result.regeocodeAddress.cityCode)
                showPoiltemList(result.regeocodeAddress.pois)
            } else {
//                ToastUtil.show(this@ReGeocoderActivity, R.string.no_result)
            }
        } else {
//            ToastUtil.showerror(this, rCode)
        }
    }


    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
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

//    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
//
//    }
//
//    override fun onPoiSearched(poiResult: PoiResult?, p1: Int) {
//        //POS搜索回调
//        val pois = poiResult?.pois
//        showPoiltemList(poiResult?.pois!!)
//    }

    override fun getLayoutId(): Int {
        return R.layout.activity_map_select_address
    }

//    private fun searchPoiItem(latLonPoint: LatLonPoint) {
//        val query = PoiSearch.Query("", "", "")
//        query.pageSize = 20
//        query.pageNum = 0
//        val poiSearch = PoiSearch(this, query)
//        poiSearch.bound = PoiSearch.SearchBound(latLonPoint, 1000)
//        poiSearch.setOnPoiSearchListener(this)
//        poiSearch.searchPOIAsyn()
//    }

    /**
     * 展示list
     */
    private fun showPoiltemList(pois: List<PoiItem>) {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val poilSearchAdapter = PoilSearchAdapter(this, pois)
        poilSearchAdapter.setType(isFillAddressPage, type)
        recyclerView.adapter = poilSearchAdapter
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_common_address -> {
                HistoryActivity.startActivity(this, type, isFillAddressPage)
            }
            R.id.layout_input_address -> {
                KeyWordsSelectAddressActivity.startActivityForResult(this)
            }
            R.id.image_back -> {
                AppManager.getAppManager().finishActivity()
            }
            R.id.image_locaiton -> {
                activate(null)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_FIRST_CODE) {
                latitude = data?.getDoubleExtra("latitude", 0.00)!!
                longtitude = data.getDoubleExtra("longtitude", 0.00)
                val latlon = LatLng(latitude, longtitude)
                aMap?.moveCamera(CameraUpdateFactory.changeLatLng(latlon))
//                addMarkerInScreenCenter()
            }
        }
    }

    companion object {

        /**
         * isFillAddressPage 是填写地址页面跳转来的
         */
        fun startActivityForResult(context: Activity, isFillAddressPage: Boolean, type: Int, latitude: Double?, longtitude: Double?) {
            val intent = Intent(context, MapSelectAddressActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longtitude", longtitude)
            intent.putExtra("isFillAddressPage", isFillAddressPage)
            context.startActivityForResult(intent, Constant.REQUEST_FIRST_CODE)
        }

        fun startActivity(context: Activity, type: Int, latitude: Double, longtitude: Double) {
            val intent = Intent(context, MapSelectAddressActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("latitude", latitude)
            intent.putExtra("longtitude", longtitude)
            context.startActivity(intent)
        }


    }
}
