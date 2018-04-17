package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.wise.common.baseapp.AppManager
import com.wise.common.baserx.RxBus
import com.wise.common.commonutils.JsonUtils
import com.wise.common.commonutils.ToastUtil
import com.wise.common.commonwidget.photopicker.ImgSelActivity
import kotlinx.android.synthetic.main.activity_take_product.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.UploadImageAdapter
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.dialog.SelectImageDialog
import net.ed58.dlm.clients.entity.SingleIntValue
import net.ed58.dlm.clients.entity.UploadImagesBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File
import java.util.*

class TakeProductActivity : BaseCoreActivity(), View.OnClickListener {

    private var dialog: SelectImageDialog? = null
    private lateinit var photos: ArrayList<String>
    private var adapter: UploadImageAdapter? = null
    private lateinit var orderId: String
    private var position: Int = -1//点的条目位置
    private var index: Int = -1//在哪个tab操作的

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = intent.getStringExtra("orderId")
        position = intent.getIntExtra("clickPos", -1)
        index = intent.getIntExtra("index", -1)

        initView()
    }

    private fun initView() {
        button_submit.setOnClickListener(this)
        image_back.setOnClickListener(this)
        recyclerView.layoutManager = GridLayoutManager(mContext, 3)
        photos = arrayListOf()
        photos.add("")
        adapter = UploadImageAdapter(mContext, photos, 4)
        adapter?.setiOnClickLisener(object : UploadImageAdapter.IOnClickLisener {
            override fun onClickItem(position: Int) {
                dialog = SelectImageDialog(mContext, UploadImageAdapter.UPLOAD_COUNT - photos.size + 1)
                dialog?.setiOnHandleBytes(object : SelectImageDialog.IOnHandleBytes {
                    override fun onCameraShot(path: String) {
                        photos.add(0, path)
                        adapter?.notifyDataSetChanged()
                    }

                    override fun handleImageBytes(path: String) {

                    }


                })
                dialog?.show()
            }
        })

        recyclerView.adapter = adapter
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_take_product
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_back -> {
                AppManager.getAppManager().finishActivity()
            }
            R.id.button_submit -> {
                uploadImagesAndPublic()
            }
        }

    }


    /**
     * 上传图片并且submit
     */
    private fun uploadImagesAndPublic() {
        val tempPhotos = ArrayList<String>()
        tempPhotos.addAll(photos)
        tempPhotos.removeAt(tempPhotos.size - 1)
        if (tempPhotos.size < 3) {
            ToastUtil.showBottomtoast(this, "请至少上传3张角度的照片")
        } else {
            startProgressDialog()
            val params = HashMap<String, RequestBody>()
            for (j in tempPhotos.indices) {
                val file = File(tempPhotos[j])
                val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                if (j == 0) {
                    params.put("file", requestFile)
                } else {
                    params.put("file" + j, requestFile)
                }
            }
            HttpMethods.getInstance().uploadMore(object : RxSubscriber<UploadImagesBean>(mContext, false) {
                override fun _onNext(uploadImages:UploadImagesBean) {
                    publicApply(JsonUtils.toJson(uploadImages.value))
                }

                override fun _onError(code: Int, message: String) {
                    stopProgressDialog()
                }
            }, params)
        }

    }

    /**
     * submit
     *
     * @param images
     */
    fun publicApply(images: String) {
        HttpMethods.getInstance().uploadDeliverImgs(object : RxSubscriber<SingleIntValue>(this@TakeProductActivity, false) {
            override fun _onNext(singleIntValue: SingleIntValue) {
                ToastUtil.showBottomtoast(this@TakeProductActivity, "取货成功")
                stopProgressDialog()
                when (index) {
                    0,1->setResult(Activity.RESULT_OK)
                    2->RxBus.getInstance().post(RxKey.EVENT_ALL_RECEIVE_LIST_ITEM, -1)
                }

                AppManager.getAppManager().finishActivity()
            }

            override fun _onError(code: Int, message: String) {
                stopProgressDialog()
            }
        }, orderId, images)

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
        }
    }

    companion object {
        fun startActivityForResult(context: Activity, orderId: String,index:Int, position: Int) {
            val intent = Intent(context, TakeProductActivity::class.java)
            intent.putExtra("orderId", orderId)
            intent.putExtra("clickPos", position)
            intent.putExtra("index", index)
            context.startActivityForResult(intent,Constant.REQUEST_SECOND_CODE)
        }
    }
}
