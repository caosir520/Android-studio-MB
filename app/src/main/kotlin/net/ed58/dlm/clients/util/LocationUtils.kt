package net.ed58.dlm.clients.util

import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import net.ed58.dlm.clients.MyApplication
import net.ed58.dlm.clients.sp.SPKey
import net.ed58.dlm.clients.sp.SettingUtils


/**
 * Created by sunpeng on 17/11/9.
 */
class LocationUtils {

    companion object {
        var mLocationClient: AMapLocationClient? = null
        var mLocationOption: AMapLocationClientOption? = null
        //声明定位回调监听器
        var mLocationListener = AMapLocationListener { amapLocation ->
            if (amapLocation != null) {
                if (amapLocation.errorCode == 0) {
//                    LogUtil.d("服务定位${amapLocation.latitude},${amapLocation.longitude}")
                    SettingUtils.setEditor(MyApplication.getContext(),SPKey.KEY_MLAT,"${amapLocation.latitude}")
                    SettingUtils.setEditor(MyApplication.getContext(),SPKey.KEY_MLNG,"${amapLocation.longitude}")
                } else {
//                    ToastUtil.showBottomtoast(MyApplication.getContext(), "定位失败")
                }
            }
        }

        fun location() {
            mLocationClient = AMapLocationClient(MyApplication.getContext())
            mLocationOption = AMapLocationClientOption()
            mLocationOption?.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            mLocationOption?.isNeedAddress = false//不需要地址信息
            mLocationOption?.interval = 60000
            mLocationOption
            //设置定位回调监听
            mLocationClient?.setLocationListener(mLocationListener)

            mLocationClient?.setLocationOption(mLocationOption)
            //启动定位
            mLocationClient?.startLocation()
        }

        fun stopLocation() {
            mLocationClient?.stopLocation()
        }
    }


}