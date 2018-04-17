package net.ed58.dlm.clients.adapter

import android.content.Context
import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import com.wise.common.commonwidget.RatingBar
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.entity.ReviewM

/**
 * Created by XiongHongJie on 2017/11/29.
 */
class RiderEvaluationRecordAdapter(context: Context):CommonRecycleViewAdapter<ReviewM.ReviewMM>(context, R.layout.item_rider_evaluation_record) {

    override fun convert(helper: ViewHolderHelper?, t: ReviewM.ReviewMM?) {

        helper?.setText(R.id.reviewTime, t?.reviewTime)
        helper?.setText(R.id.contain, t?.text)
        helper?.setText(R.id.reviewId,t?.reviewId)
        helper?.setText(R.id.orderAmount,t?.orderAmount.toString())
        helper?.setText(R.id.categoryName,t?.categoryName)

        val ratingBar = helper?.getView<RatingBar>(R.id.ratingBar)
        ratingBar?.isClickable =false
        ratingBar?.setStar(t?.rating!!.toFloat())



    }


}