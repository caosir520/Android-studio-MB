package net.ed58.dlm.clients.entity

import java.io.Serializable

/**
 * 创建人: caosir
 * 创建时间：2017/10/30
 * 修改人：
 * 修改时间：
 * 类说明：历史数据返回Json数据
 */

class Address : Serializable {
    var total: Int = 0
    /**
     * name : 曹臻睿
     * tel : 15870617281
     * lng : 0
     * lat : 0
     * cityCode :
     * adCode :
     * geoHash : s00000
     * address : 曹张新村自行车
     * floorHouseNum :
     * isDefault : false
     * createTime : 2017-10-30 17:32:04
     * lastModifyTime : 2017-10-30 17:32:04
     * id : 8a8080885f6c9293015f6c9fade10000
     */
    var list: List<ListEntity>? = null

    //单条的历史数据
    class ListEntity {
        var name: String? = null
        var tel: String? = null
        var lng: Double = 0.00
        var lat: Double = 0.00
        var cityCode: String? = null
        var adCode: String? = null
        var geoHash: String? = null
        var address: String? = null
        var floorHouseNum: String? = null
        var isIsDefault: Boolean = false
        var createTime: String? = null
        var lastModifyTime: String? = null
        var id: String? = null
    }
}
