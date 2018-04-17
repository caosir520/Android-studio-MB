package net.ed58.dlm.clients.entity

import java.io.Serializable

/**
 * Created by sunpeng on 17/10/31.
 */
class ProductTypeListBean :Serializable{

    var list:List<ProductTypeBean>? = null

    class ProductTypeBean:Serializable{
        var id: String? = null
        var name: String? = null
        var icon: String? = null
        var children: MutableList<ProductTypeBean>? = null

        var type: Int? = -1
        var isChecked: Boolean = false
    }

}