package net.ed58.dlm.clients.entity

import java.security.cert.CertPathValidatorException

/**
 * Created by XiongHongJie on 2017/11/23.
 */

class ComplaintReason {

    var total: Int = 0

    var list: List<ReasonList>? = null


    class ReasonList{

        var isSelect: Boolean = false

        var complaintObject: Int = 0

        var reason: String? = null

        var createTime: String? = null

        var id: String =""

        var lastModifyTime: String? = null

        var orderNum: Int = 0

    }

}