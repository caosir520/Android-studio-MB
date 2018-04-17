package net.ed58.dlm.clients.entity

import java.io.Serializable

/**
 * Created by sunpeng on 17/10/27.
 */

class AddressInfoBean : Serializable {
    var address: String? = null
    var detailAddress:String?=null
    var name: String? = null
    var phone: String? = null
    var adCode: String? = null
    var cityCode: String? = null
    var latitude: Double? = null
    var longtitude: Double? = null

    var isCompleted: Boolean = false

}
