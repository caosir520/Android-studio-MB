package net.ed58.dlm.clients.adapter

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout

import com.aspsine.irecyclerview.universaladapter.ViewHolderHelper
import com.aspsine.irecyclerview.universaladapter.recyclerview.CommonRecycleViewAdapter
import com.wise.common.commonutils.DisplayUtil
import net.ed58.dlm.clients.R

/**
 * Created by sunpeng on 17/8/30.
 */

class UploadImageAdapter : CommonRecycleViewAdapter<String> {
    private var iOnClickLisener: IOnClickLisener? = null

    constructor(context: Context, mDatass: ArrayList<String>) : super(context, R.layout.item_add_pic, mDatass) {}

    constructor(context: Context, mDatass: ArrayList<String>, upload_count: Int) : super(context, R.layout.item_add_pic, mDatass) {
        UPLOAD_COUNT = upload_count

    }

    constructor(context: Context, mDatass: ArrayList<String>, upload_count: Int, online_picture_quantity: Int) : super(context, R.layout.item_add_pic, mDatass) {
        UPLOAD_COUNT = upload_count

        ONELINE_PICTURE_QUANTITY = online_picture_quantity
    }



    override fun convert(helper: ViewHolderHelper, s: String) {

//        var widthAndheight = (DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dip2px((ONELINE_PICTURE_QUANTITY+1)*10f)) / ONELINE_PICTURE_QUANTITY
        if (mDatas.size - 1 == helper.getmPosition()) {
            if (mDatas.size - 1 == UPLOAD_COUNT) {
                helper.setVisible(R.id.image_click, false)
                helper.setVisible(R.id.layout_image, false)
            } else {
                helper.setVisible(R.id.image_click, true)
                helper.setVisible(R.id.layout_image, false)
            }


            val layoutParams = helper.getView<View>(R.id.image_click).layoutParams as LinearLayout.LayoutParams
            layoutParams.width = (DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dip2px((ONELINE_PICTURE_QUANTITY+1)*10f)) / ONELINE_PICTURE_QUANTITY
            layoutParams.height = (DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dip2px((ONELINE_PICTURE_QUANTITY+1)*10f)) / ONELINE_PICTURE_QUANTITY
            helper.getView<View>(R.id.image_click).layoutParams = layoutParams

            helper.setOnClickListener(R.id.image_click) { iOnClickLisener!!.onClickItem(helper.getmPosition()) }
        } else {
            helper.getView<View>(R.id.image_click).visibility = View.GONE
            helper.getView<View>(R.id.layout_image).visibility = View.VISIBLE
            helper.setImageUrl(R.id.showpic, s)

            val layoutParams = helper.getView<View>(R.id.showpic).layoutParams as FrameLayout.LayoutParams
            val layoutParams_parent = helper.getView<View>(R.id.layout_image).layoutParams as LinearLayout.LayoutParams
            layoutParams.width = (DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dip2px((ONELINE_PICTURE_QUANTITY+1)*10f)) / ONELINE_PICTURE_QUANTITY
            layoutParams.height = (DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dip2px((ONELINE_PICTURE_QUANTITY+1)*10f)) / ONELINE_PICTURE_QUANTITY
            layoutParams_parent.width = (DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dip2px((ONELINE_PICTURE_QUANTITY+1)*10f)) / ONELINE_PICTURE_QUANTITY
            layoutParams_parent.height = (DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dip2px((ONELINE_PICTURE_QUANTITY+1)*10f)) / ONELINE_PICTURE_QUANTITY
            helper.getView<View>(R.id.showpic).layoutParams = layoutParams
            helper.getView<View>(R.id.layout_image).layoutParams = layoutParams_parent

            helper.setOnClickListener(R.id.delete_pic) { view ->
                mDatas.removeAt(helper.getmPosition())
                notifyDataSetChanged()
            }
        }
    }


    fun setiOnClickLisener(iOnClickLisener: IOnClickLisener) {
        this.iOnClickLisener = iOnClickLisener
    }

    interface IOnClickLisener {
        fun onClickItem(position: Int)
    }

    companion object {

        var UPLOAD_COUNT = 6//上传照片的最大数

        var ONELINE_PICTURE_QUANTITY = 3
    }
}
