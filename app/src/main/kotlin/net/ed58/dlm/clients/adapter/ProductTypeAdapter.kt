package net.ed58.dlm.clients.adapter

import android.content.Context
import android.widget.Button
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.entity.ProductTypeListBean

/**
 * Created by sunpeng on 17/10/31.
 * 品类共用页面
 */
class ProductTypeAdapter(context: Context?, mDatass: List<ProductTypeListBean.ProductTypeBean>?) : CommonRecycleViewAdapter<ProductTypeListBean.ProductTypeBean>(context, R.layout.item_product_type, mDatass) {

    var selectedPos: Int = 0
    var listener:IButtonListener? = null

    override fun convert(helper: ViewHolderHelper?, productTypeBean: ProductTypeListBean.ProductTypeBean?) {
        helper?.setText(R.id.text_type, productTypeBean?.name)
        val text_type = helper?.getView<Button>(R.id.text_type)

        text_type?.isSelected = selectedPos == helper?.getmPosition()

        helper?.setOnClickListener(R.id.text_type) {
            selectedPos = helper.getmPosition()
            notifyDataSetChanged()
            listener?.onItemClick(selectedPos)
        }

    }

    interface IButtonListener {
        fun onItemClick(position: Int)
    }


}