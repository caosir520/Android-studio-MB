package net.ed58.dlm.clients.entity

import java.math.BigDecimal

/**
 * Created by Administrator on 2017/11/10/010.
 * 订单详情JavaBean
 */

class OrderM {

    /**
     * topDistance : 0
     * bottomDistance : 0
     * orderNo : 2017103110300539200001903
     * receiverName : 蜀香干锅菜
     * receiverAddress : 永川区其他玉清东路御珑山超市北100米
     * receiverLat : 29.36
     * receiverLng : 105.93
     * receiverPhone : 13022222222
     * receiverTel : 021-6666666
     * fetchName : 微创
     * fetchAddress : 闵行区东川路555号
     * fetchLat : 30.08
     * fetchLng : 105.82
     * fetchPhone : 13011111111
     * fetchTel : 021-77777777
     * originatorRemark :
     * expectedFetchTime : 2017-10-31 16:00:00
     * categoryName :
     * cargoAmount :
     * cargoWeight : 1
     * insuranceFee : 1
     * compensationFee : 10
     * distance : 80708.9
     * orderAmount : 0.01
     * realPriceCount : 0.01
     * orderStatus : -100
     * payService : 0
     * originatorReviewStatus : 0
     * carrierReviewStatus : 0
     * createTime : 2017-10-31 10:30:05
     * lastModifyTime : 2017-10-31 10:30:05
     * id : 2c9181875f70438b015f7043b1520002
     */

    var topDistance: Double = 0.toDouble()
    var bottomDistance: Double = 0.toDouble()
    var asSenderRating: Double = 0.toDouble()
    var orderNo: String? = null
    var receiverName: String? = null
    var receiverAddress: String? = null
    var receiverFloorHouseNum: String = ""
    var receiverLat: Double = 0.toDouble()
    var receiverLng: Double = 0.toDouble()
    var receiverPhone: String? = null
    var receiverTel: String? = null
    var fetchName: String? = null
    var fetchFloorHouseNum: String = ""
    var fetchAddress: String? = null
    var fetchLat: Double = 0.toDouble()
    var fetchLng: Double = 0.toDouble()
    var fetchPhone: String? = null
    var fetchTel: String? = null
    var originatorRemark: String? = null
    var expectedFetchTime: String? = null
    var categoryName: String? = null
    var cargoAmount: String? = null
    var cargoWeight: Int = 0
    var insuranceFee: BigDecimal? = null
    var compensationFee: BigDecimal? = null
    var distance: Double = 0.toDouble()
    var orderAmount: Double = 0.toDouble()
    var realPriceCount: Double = 0.toDouble()
    var orderStatus: Int = 0

    var payService: Int = 0
    var originatorReviewStatus: Int = 0
    var carrierReviewStatus: Int = 0
    var createTime: String? = null
    var lastModifyTime: String? = null
    var orderTakeTime: String? = null
    var id: String? = null
    //列表状态
    var canceledReason: String? = null
    var contactFetcher:Boolean = false
    var contactReceiver:Boolean = false
    var checkDeliverLockerCode:Boolean = false
    var checkPickupLockerCode:Boolean = false

    var carrier: CarrierBean?  = null
}
