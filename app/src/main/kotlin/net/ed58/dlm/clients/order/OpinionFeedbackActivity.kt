package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.wise.common.commonutils.JsonUtils
import com.wise.common.commonutils.LogUtil
import com.wise.common.commonutils.ToastUtil
import com.wise.common.commonwidget.photopicker.ImgSelActivity

import kotlinx.android.synthetic.main.activity_opinion_feedback.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.UploadImageAdapter
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.base.BaseUI
import net.ed58.dlm.clients.dialog.SelectImageDialog
import net.ed58.dlm.clients.entity.Comments
import net.ed58.dlm.clients.entity.UploadImagesBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File

/**
 * Created by XiongHongJie on 2017/11/20.
 */

class OpinionFeedbackActivity : BaseCoreActivity(), View.OnClickListener {

    private var adapter: UploadImageAdapter? = null
    private lateinit var photos: ArrayList<String>
    private var dialog: SelectImageDialog? = null
    var type: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initview()

        suggest.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val length =300 - s.toString().length
                suggest_length.text ="$length"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })


    }

    private fun initview() {
        dysfunction.setOnClickListener(this)
        experiencing_problems.setOnClickListener(this)
        new_function.setOnClickListener(this)
        other.setOnClickListener(this)
        image_back.setOnClickListener(this)
        button_confirm.setOnClickListener(this)

//        recyclerView.layoutManager = GridLayoutManager(mContext,4)
        recyclerView.layoutManager = GridLayoutManager(mContext,4)
        photos=arrayListOf()
        photos.add("")
        adapter = UploadImageAdapter(mContext,photos,4,4)
        adapter?.setiOnClickLisener(object : UploadImageAdapter.IOnClickLisener{
            override fun onClickItem(position: Int) {
                dialog = SelectImageDialog(mContext,UploadImageAdapter.UPLOAD_COUNT - photos.size + 1)

                dialog?.setiOnHandleBytes(object :SelectImageDialog.IOnHandleBytes{
                    override fun handleImageBytes(path: String) {

                    }

                    override fun onCameraShot(path: String) {

                        photos.add(0,path)
                        adapter?.notifyDataSetChanged()
                    }

                })
                dialog?.show()
            }

        })
        recyclerView.adapter = adapter
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_opinion_feedback
    }

    override fun onClick(view: View?) {

        when(view?.id){

            R.id.dysfunction -> {
                dysfunction_button.isChecked = true
                type = 1
            }

            R.id.experiencing_problems -> {
                experiencing_problems_button.isChecked = true
                type = 2
            }

            R.id.new_function -> {
                new_function_button.isChecked = true
                type = 3
            }

            R.id.other -> {
                other_button.isChecked = true
                type = 4
            }

            R.id.image_back -> onBackPressed()


            R.id.button_confirm -> {
                if (check()){
                    UploadPictureInFeedback()
                }

            }
        }
    }

    private fun UploadPictureInFeedback(){
        startProgressDialog()
        val tempPhoto = ArrayList<String>()
        val params = HashMap<String,RequestBody>()
        tempPhoto.addAll(photos)
        Log.d("tag1",tempPhoto.toString())
        tempPhoto.removeAt(tempPhoto.size - 1)
        if (tempPhoto.size == 0){
            FeedbackApply("")
        }else{
            for (j in tempPhoto.indices){
                var file = File(tempPhoto[j])
                var requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file)
                if (j == 0){
                    params.put("file" , requestFile)
                }else{
                    params.put("file" + j, requestFile)
                }
            }
        }

        HttpMethods.getInstance().uploadMore(object : RxSubscriber<UploadImagesBean>(mContext,false) {
            override fun _onNext(uploadImages: UploadImagesBean) {

                FeedbackApply(JsonUtils.toJson(uploadImages.value))

                Log.d("tag",uploadImages.value.toString())
            }

            override fun _onError(code: Int, message: String) {

                Log.d("tag","shibai")
                stopProgressDialog()
            }
        }, params)
    }

    /**
     *
     *
     */
    private fun FeedbackApply(s: String) {

        Log.d("tag",s)
        HttpMethods.getInstance().submitComments(object :RxSubscriber<Comments>(mContext){
            override fun _onNext(t: Comments?) {

            }

            override fun _onError(code: Int, message: String?) {

            }

        },type,suggest.text.toString(),s)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_MULTI_TAKE_PHOTO_CODE) {
                dialog?.onCameraShot()
            } else if (requestCode == Constant.REQUEST_MULTI_SELECT_IAMGE_CODE && data != null) {
                val pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT)
                photos.addAll(0, pathList)
                adapter?.notifyDataSetChanged()
            }
            LogUtil.d(photos.toString())
        }
    }

    fun check():Boolean{

        if (type == 0){
            ToastUtil.showBottomtoast(this,"请选择意见反馈的类型")
            return false
        }

        if (suggest.text.toString().length < 10)
        {
            ToastUtil.showBottomtoast(this,"请多给一些建议，至少十个字")
            return false
        }

        return true
    }

    companion object {
        fun startActivity(context: Activity, reportCode: Int) {
            val intent = Intent(context, OpinionFeedbackActivity::class.java)
            context.startActivity(intent)
        }
    }
}