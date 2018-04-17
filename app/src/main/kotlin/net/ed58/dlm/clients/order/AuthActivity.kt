package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.wise.common.baseapp.AppManager
import com.wise.common.baserx.RxJavaDemoUtil
import com.wise.common.commonutils.ImageLoaderUtils
import com.wise.common.commonutils.ToastUtil
import kotlinx.android.synthetic.main.activity_auth.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.dialog.SelectImageDialog
import net.ed58.dlm.clients.entity.CustomerBean
import net.ed58.dlm.clients.entity.UploadImagesBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.network.http.HttpMethods
import net.ed58.dlm.clients.network.subscribers.RxSubscriber
import net.ed58.dlm.clients.util.CommonDialogUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import rx.Subscriber
import java.io.File
import java.util.*
//实名认证页面
class AuthActivity : BaseCoreActivity(), View.OnClickListener {

    private var dialog: SelectImageDialog? = null
    private var photoPostion: Int = -1

    private var topPhotoPath = ""
    private var bottomPhotoPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

    }

    private fun initView() {
        layout_icon.setOnClickListener(this)
        image_back.setOnClickListener(this)
        layout_together.setOnClickListener(this)
        button_submit.setOnClickListener(this)
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_auth
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.layout_icon -> {
                photoPostion = 1
                showDialog()
            }
            R.id.layout_together -> {
                photoPostion = 2
                showDialog()
            }
            R.id.button_submit -> {
                if (check()) {
                    submit()
                }
            }
            R.id.image_back->{
                AppManager.getAppManager().finishActivity()
            }
        }
    }

    //提交认证
    private fun submit() {
        startProgressDialog()
        val params = HashMap<String, RequestBody>()
        val requestTopFile = RequestBody.create(MediaType.parse("multipart/form-data"), File(topPhotoPath))
        val requestBottomFile = RequestBody.create(MediaType.parse("multipart/form-data"), File(bottomPhotoPath))
        params.put("file", requestTopFile)
        params.put("file1", requestBottomFile)

        HttpMethods.getInstance().uploadMore(object : RxSubscriber<UploadImagesBean>(mContext, false) {
            override fun _onNext(uploadImages: UploadImagesBean?) {
//                publicApply(JSON.toJSONString(uploadImages.getValues()))
                HttpMethods.getInstance().sumbitAuth(object : RxSubscriber<CustomerBean>(mContext, false) {
                    override fun _onNext(t: CustomerBean?) {
                        stopProgressDialog()
                        //弹出对话框
                        CommonDialogUtil.showOriginSuccessDialog(mContext,"提交成功！")
                        //倒计时3秒
                        RxJavaDemoUtil.CountDowm(object : Subscriber<Long>(){
                            override fun onError(e: Throwable?) {
                            }

                            override fun onCompleted() {
                            }

                            override fun onNext(time: Long?) {
                                if (time?.toInt() == 2) {
                                    AppManager.getAppManager().finishActivity()
                                }
                            }

                        }, 3)

                    }

                    override fun _onError(code: Int, message: String?) {
                        stopProgressDialog()
                    }

                },edit_name.text.toString().trim(),edit_identity.text.toString().trim(),uploadImages?.value?.get(0),uploadImages?.value?.get(1))
            }

            override fun _onError(code: Int, message: String) {
                stopProgressDialog()
            }
        }, params)
    }

    //校验信息
    fun check(): Boolean {
        if (TextUtils.isEmpty(edit_name.text.toString().trim())) {
            ToastUtil.showBottomtoast(this, "请输入姓名")
            return false
        }
        if (TextUtils.isEmpty(edit_identity.text.toString().trim())) {
            ToastUtil.showBottomtoast(this, "请输入身份证号")
            return false
        }
        if (TextUtils.isEmpty(topPhotoPath)) {
            ToastUtil.showBottomtoast(this, "请拍摄身份证头像面照片")
            return false
        }
        if (TextUtils.isEmpty(bottomPhotoPath)) {
            ToastUtil.showBottomtoast(this, "请拍摄身份证与头像面合照")
            return false
        }
        return true
    }

    fun showDialog() {
        if (dialog == null) {
            dialog = SelectImageDialog(this, object : SelectImageDialog.IOnHandleBytes {
                override fun onCameraShot(path: String) {

                }

                override fun handleImageBytes(path: String) {
                    when (photoPostion) {
                        1 -> {
                            topPhotoPath = path
                            ImageLoaderUtils.display(mContext, image_icon, path)
                        }
                        2 -> {
                            bottomPhotoPath = path
                            ImageLoaderUtils.display(mContext, image_together, path)
                        }
                    }

                }
            })
        }
        dialog?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_SELECT_IAMGE_CODE) {
                dialog?.callBack(data)
            }
        }
    }

    companion object {
        fun startActivity(context: Activity) {
            val intent = Intent(context, AuthActivity::class.java)
            context.startActivity(intent)
        }
    }
}
