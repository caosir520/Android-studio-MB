package net.ed58.dlm.clients.entity

import java.math.BigDecimal

/**
 * 创建人: caosir
 * 创建时间：2017/10/31
 * 修改人：
 * 修改时间：
 * 类说明：
 */

class Insured {


    /**
     * list : [{"insuranceFee":1,"compensateFee":100,"status":0,"createTime":"2017-10-31 10:12:25","lastModifyTime":"2017-10-31 10:12:25","id":"8a8080885f6c9293015f703384f3000d"}]
     * total : 1
     */

    var total: Int = 0
    /**
     * insuranceFee : 1
     * compensateFee : 100
     * status : 0
     * createTime : 2017-10-31 10:12:25
     * lastModifyTime : 2017-10-31 10:12:25
     * id : 8a8080885f6c9293015f703384f3000d
     */

    var list: List<ListEntity>? = null

    class ListEntity {
        private var isSelect: Boolean = false

        fun isSelect(): Boolean {
            return isSelect
        }

        fun setSelect(select: Boolean) {
            isSelect = select
        }

        var insuranceFee: BigDecimal ?= null
        var compensateFee: BigDecimal ?= null
        var status: Int = 0
        var createTime: String? = null
        var lastModifyTime: String? = null
        var id: String? = null
    }
}
