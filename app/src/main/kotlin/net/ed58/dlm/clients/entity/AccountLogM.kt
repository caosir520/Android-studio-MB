package net.ed58.dlm.clients.entity

import java.math.BigDecimal

/**
 * Created by XiongHongJie on 2017/11/27.
 */
class AccountLogM {

    var total: Int = 0

    var list: List<AccountLogMList>? = null

    class AccountLogMList{
        var accountId: String? = null

        var accrualsFee: BigDecimal? = null

        var createTime: String? = null

        var logNo: String? = null

        var logType: Int = 0

        var logTypeDesc: String? = null

        var symbol: String? = null
    }

}