package net.ed58.dlm.clients.dialog


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.wise.common.commonutils.ImageLoaderUtils
import com.wise.common.commonutils.LogUtil
import com.wise.common.commonutils.ToastUtil
import com.wise.common.commonwidget.photopicker.ImageLoader
import com.wise.common.commonwidget.photopicker.ImgSelActivity
import com.wise.common.commonwidget.photopicker.ImgSelConfig
import com.wise.common.commonwidget.photopicker.utils.FileUtils
import com.wise.common.compressorutils.MyPicUtils
import com.wise.common.compressorutils.ThreadManager
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.global.Constant
import java.io.File


/**
 * 照相图片的Dialog 从底部弹出
 * Created by sunpeng on 17/8/14.
 */

class SelectImageDialog : Dialog {

    private var mContext: Context
    private var IMAGE_FILE_LOCATION: String? = null
    private var view: View? = null
    private var iOnHandleBytes: IOnHandleBytes? = null
    private var maxNum: Int? = null
    private var text_tip: TextView? = null

    private var tempFile: File? = null
    private val loader = object : ImageLoader {
        override fun displayImage(context: Context?, path: String?, imageView: ImageView?) {
            ImageLoaderUtils.display(context, imageView, path)
        }
    }


    constructor(context: Context, iOnHandleBytes: IOnHandleBytes) : super(context, R.style.ActionSheetDialogStyle) {
        this.iOnHandleBytes = iOnHandleBytes
        this.mContext = context

        //填充对话框的布局
        view = LayoutInflater.from(context).inflate(R.layout.dialog_select_image, null)

        //将布局设置给Dialog
        setContentView(view!!)
        //点击外面的窗口是否消失
        setCanceledOnTouchOutside(true)
        //获取当前Activity所在的窗体
        val dialogWindow = window
        //设置Dialog从窗体底部弹出
        dialogWindow!!.setGravity(Gravity.BOTTOM)
        //获得窗体的属性
        val lp = dialogWindow.attributes
        //       将属性设置给窗体
        dialogWindow.attributes = lp

        val tv_take_photo = view!!.findViewById<TextView>(R.id.tv_take_photo)
        val tv_pic = view!!.findViewById<TextView>(R.id.tv_pic)

        tv_pic.setOnClickListener {
            choosePicFromPhoto()
            dismiss()
        }
        tv_take_photo.setOnClickListener {
            takePhoto()
            dismiss()
        }
        val tv_cancel = view!!.findViewById<TextView>(R.id.tv_cancel)
        tv_cancel.setOnClickListener { dismiss() }
    }

    /**
     * @param context
     * @param maxNum  提示照片还有几张
     */
    constructor(context: Context, maxNum: Int) : super(context, R.style.ActionSheetDialogStyle) {

        this.mContext = context
        this.maxNum = maxNum

        //填充对话框的布局
        view = LayoutInflater.from(context).inflate(R.layout.dialog_select_image, null)

        //将布局设置给Dialog
        setContentView(view)
        setCanceledOnTouchOutside(true)
        //获取当前Activity所在的窗体
        val dialogWindow = window
        //设置Dialog从窗体底部弹出
        dialogWindow!!.setGravity(Gravity.BOTTOM)
        //获得窗体的属性
        val lp = dialogWindow.attributes
        LogUtil.d(""+lp.width)
        //       将属性设置给窗体
        dialogWindow.attributes = lp

        val tv_take_photo = view?.findViewById<TextView>(R.id.tv_take_photo)
        val tv_pic = view?.findViewById<TextView>(R.id.tv_pic) //从手机相册里面选择
        val layout_tip = view?.findViewById<LinearLayout>(R.id.layout_tip)
        text_tip = view?.findViewById<View>(R.id.text_tip) as TextView
        text_tip?.text = "你还可选择" + maxNum + "张"
        layout_tip?.visibility = View.VISIBLE


        tv_pic?.setOnClickListener { v ->
            if (maxNum == null) {
                //                单张图片上传
                choosePicFromPhoto()
            } else {
                //选多张图片
                selectMutilPhotos()
            }

            dismiss()
        }
        tv_take_photo?.setOnClickListener { v ->
            if (maxNum == null) {
                takePhoto()
            } else {
                showCameraAction()
            }
            dismiss()
        }
        val tv_cancel = view?.findViewById<TextView>(R.id.tv_cancel)
        tv_cancel?.setOnClickListener { v -> dismiss() }
    }

    /**
     * 多张图片选择
     */
    private fun selectMutilPhotos() {
        val config = ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(true)
                // 确定按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                .btnTextColor(ContextCompat.getColor(mContext, R.color.title_color))
                .titleBgColor(ContextCompat.getColor(mContext, R.color.white))
                .titleColor(ContextCompat.getColor(mContext, R.color.title_color))
                // 返回图标ResId
                .backResId(R.mipmap.icon_back)
                .title("图片")
                // 第一个是否显示相机
                .needCamera(false)
                // 最大选择图片数量
                .maxNum(maxNum!!)
                .build()
        ImgSelActivity.startActivity(mContext as BaseCoreActivity, config, Constant.REQUEST_MULTI_SELECT_IAMGE_CODE)
    }

    /**
     * 和多选图片配套的拍照
     */
    private fun showCameraAction() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(mContext.getPackageManager()) != null) {
            tempFile = File(FileUtils.createRootPath(mContext) + "/" + System.currentTimeMillis() + ".jpg")
            FileUtils.createFile(tempFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile))
            (mContext as BaseCoreActivity).startActivityForResult(cameraIntent, Constant.REQUEST_MULTI_TAKE_PHOTO_CODE)
        } else {
            Toast.makeText(mContext, "打开相机失败", Toast.LENGTH_SHORT).show()
        }
    }

    //照片拍摄
    private fun takePhoto() {
        //Mainfest 权限列表 ，动态权限检查
        if (ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((mContext as Activity?)!!,
                    arrayOf(Manifest.permission.CAMERA),
                    1)
        }
        val imagePaths = (Environment.getExternalStorageDirectory().path
                + "/followme/headimg/"
                + (System.currentTimeMillis().toString() + ".jpg"))

        val vFile = File(imagePaths)
        if (!vFile.exists()) {
            val vDirPath = vFile.parentFile
            vDirPath.mkdirs()
        } else {
            if (vFile.exists()) {
                vFile.delete()
            }
        }
        val cameraUri = Uri.fromFile(vFile)
        IMAGE_FILE_LOCATION = imagePaths
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)//action is capture
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
        (mContext as BaseCoreActivity).startActivityForResult(intent, Constant.REQUEST_SELECT_IAMGE_CODE)
    }

    /**
     * 单张图片选择
     */
    private fun choosePicFromPhoto() {
        val intent: Intent
        intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)//解决 华为手机图片图片"，"分隔符不能解析的问题
        (mContext as BaseCoreActivity).startActivityForResult(intent, Constant.REQUEST_SELECT_IAMGE_CODE)
    }


    override fun show() {
        super.show()//显示对话框
    }

    override fun dismiss() {
        if (isShowing) {
            super.dismiss()
        }
    }

    fun callBack(data: Intent?) {
        // 选择手机图片回调
        if (data != null) {
            val imagePath: String
            val selectedImageUri = data.data

            // 华为返回值： uri::content://media/external/images/media/49
            // 小米返回值：uri::file:///storage/emulated/0/MIUI/Gallery/cloud/.microthumbnailFile/5387d9357423b7888dceab9207daa9248d0cc855.jpg
            //            MyLog.showLog("uri::" + selectedImageUri.getPath());
            //判断是不是华为
            if (selectedImageUri.toString().contains("content://")) {
                //华为
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = mContext!!.contentResolver.query(selectedImageUri, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                imagePath = cursor.getString(columnIndex)
                cursor.close()
            } else {  //应对小米手机返回值直接为 图片路径
                val resultPath = selectedImageUri.path
                val file = File(resultPath)
                if (file.exists()) {
                    imagePath = resultPath
                } else {
                    ToastUtil.showBottomtoast(mContext, "上传失败")
                    return
                }
            }
            /**
             * 上传 复制到缓存文件夹 发送链接
             */
            sendImage(imagePath)
        } else {
            sendImage(IMAGE_FILE_LOCATION)
        }
    }

    /**
     * 上传服务器 复制到缓存文件夹 发送链接
     *
     * @param imagePath
     */
    private fun sendImage(imagePath: String?) {
        //  pd.show();
        ThreadManager.runInThread(Runnable {
            // 通过文件路径获取文件名
            val pictureName = imagePath!!.substring(imagePath.lastIndexOf(File.separator) + 1)
            // 压缩图片
            val smallBitmap = MyPicUtils.getSmallBitmap(imagePath)
            // 压缩文件保存的文件夹路径
            val compressDirPath = Environment.getExternalStorageDirectory().toString() + "/exiu/cache/compress/"
            // 将压缩后的图片保存并返回保存路径
            val compressPath = MyPicUtils.saveFile(smallBitmap, compressDirPath, pictureName, 80)
            val file = File(compressPath)
            //                String resolution = smallBitmap.getWidth() + "*" + smallBitmap.getHeight();、
            ThreadManager.runUIThread({
                iOnHandleBytes?.handleImageBytes(compressPath)
                iOnHandleBytes?.onCameraShot(compressPath)
            }, 1L)
        })
    }


    fun onCameraShot() {
        if (tempFile != null) {
            if (iOnHandleBytes != null) {
                sendImage(tempFile?.absolutePath)
            }
        }
    }

    interface IOnHandleBytes {
        fun handleImageBytes(path: String)

        fun onCameraShot(path: String)
    }


    fun setiOnHandleBytes(iOnHandleBytes: IOnHandleBytes) {
        this.iOnHandleBytes = iOnHandleBytes
    }
}
