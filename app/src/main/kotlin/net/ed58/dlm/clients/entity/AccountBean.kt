package net.ed58.dlm.clients.entity

import java.math.BigDecimal

/**
 * Created by sunpeng on 17/11/27.
 */
class AccountBean {

    var accountNo: String = ""
    var accountId: String = ""
    var accountType: Int? = null
    var amountFee: BigDecimal = BigDecimal("0.00")
    var availableFee: BigDecimal = BigDecimal("0.00")
    var frozenFee: BigDecimal = BigDecimal("0.00")
}