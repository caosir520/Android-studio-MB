package net.ed58.dlm.clients.entity

import java.math.BigDecimal

/**
 * Created by XiongHongJie on 2017/11/29.
 */
class ReviewM {

    var head: String? = null  // 头像

    var nickName: String? = null  //昵称

    var rating: String? = null // 星级

    var times: Int = 0 // 接单/发单总次数

    var total: Int = 0 //评价总数

    var reviewList: List<ReviewMM>? = null //评价列表

    class  ReviewMM{
        var categoryName: String? = null  //货品种类

        var head: String? = null //评价人头像

        var nickName: String? = null //评价人昵称

        var orderAmount: BigDecimal? = null //订单总价

        var orderId: String? = null  //订单ID

        var orderNo: String? = null  //订单编号

        var rating: Int = 0  //评价星级

        var reviewId: String? = null  //评论id

        var reviewTime: String? = null  //评价时间

        var status: String? = null  //状态 0正常 -1删除

        var text: String? = null  //评价内容
    }
}