package net.ed58.dlm.clients.entity

import java.io.Serializable

/**
 * Created by sunpeng on 17/11/27.
 */
class WithdrawAccountBean :Serializable{

    var list:MutableList<WithdrawAccountBean>? = null

    var accountNo: String = ""
    var accountName: String = ""
    var id: String = ""
    var defaults: Boolean = false
    var accountType: Int? = null
    var bankCode: String? = null
    var bankName: String? = null
}