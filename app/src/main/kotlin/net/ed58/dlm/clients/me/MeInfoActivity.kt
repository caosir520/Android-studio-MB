package net.ed58.dlm.clients.me


import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.wise.common.baseapp.AppManager
import com.wise.common.baseglide.CircleCrop
import com.wise.common.baserx.RxBus
import com.wise.common.commonutils.LogUtil
import com.wise.common.commonutils.ToastUtil
import kotlinx.android.synthetic.main.activity_me_info.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.dialog.SelectImageDialog
import net.ed58.dlm.clients.entity.CustomerM
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey
import java.io.File

/**
 * Created by Administrator on 2017/11/27/027.
 */

class MeInfoActivity : BaseCoreMVPActivity<MeInfoPersent, MeInfoPersent.Ui>(), MeInfoPersent.Ui {

    var sex:Int?=3;


    override fun update() {
        ToastUtil.showBottomtoast(this,"更新成功")
        mRxManager.mRxBus.post(RxKey.EVENT_ME_UPDATE, listOf(path1,tv_me_name.text.toString()));
        AppManager.getAppManager().finishActivity()
    }

    private var dialog: SelectImageDialog? = null

    private var path1:String?=""

    //读取数据填充

    override fun showMe(date: CustomerM) {
        //拼接头像
        if(!"".equals(date.head)){
            path1=date.head
            Glide.with(this).load(date.head).asBitmap()
                    .placeholder(R.mipmap.icon_default_avatar)
                    .error(R.mipmap.icon_default_avatar)
                    .transform( CircleCrop(this))
                    .into(iv_head)
        }else{
            Glide.with(this).load(R.mipmap.icon_default_avatar).asBitmap()
                    .transform( CircleCrop(this))
                    .into(iv_head)
        }
        //拼接其他
        if(TextUtils.isEmpty(date.nickName)){
            tv_me_name.setText("到了吗"+date.mobile)
        }else{
            tv_me_name.setText(date.nickName)
        }


        tv_me_phone.setText(date.mobile)
        tv_me_add.setText(date.address)
        sex=date.sex
        setSex()

    }

    override fun createPresenter(): MeInfoPersent {
        return MeInfoPersent()
    }

    override fun getUi(): MeInfoPersent.Ui {
        return this
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_me_info
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.getMe();
        //修改头像点击事件
        view.setOnClickListener(View.OnClickListener {
            if(dialog ==null){
                dialog = SelectImageDialog(this,object :SelectImageDialog.IOnHandleBytes{
                    override fun handleImageBytes(path: String) {
                        LogUtil.d(""+path);
                        path1=path
                        Glide.with(mContext).load(path).asBitmap()
                                .placeholder(R.mipmap.icon_default_avatar)
                                .error(R.mipmap.icon_default_avatar)
                                .transform( CircleCrop(mContext))
                                .into(iv_head)
                    }

                    override fun onCameraShot(path: String) {


                    }

                })
            }
            dialog!!.show()
        });

        //返回事件
        toolbar.setNavigationOnClickListener { AppManager.getAppManager().finishActivity() }
        //提交事件
        tv_submit.setOnClickListener{
            if(isSubmit()){
                presenter.upDate(tv_me_name.text.toString(),
                        path1!!, sex!!,tv_me_add.text.toString())
            }
        }

        tv_me_sex_select.setOnClickListener{

            }

    }
    //更具状态设置性别
    fun setSex(){
        when(sex){
            1->tv_me_sex.text="男"
            2->tv_me_sex.text="女"
        }
    }

    //提交认证
    fun isSubmit():Boolean{
        var isTrue:Boolean?=true;
        if(TextUtils.isEmpty(tv_me_name.text.toString())){
            ToastUtil.showBottomtoast(this,"用户名称不能为空")
            return false
        }else if(TextUtils.isEmpty(tv_me_sex.text.toString())){
            ToastUtil.showBottomtoast(this,"用户性别未选择")
            return false
        }else if(TextUtils.isEmpty(tv_me_sex.text.toString())){
            ToastUtil.showBottomtoast(this,"用户地址不能为空")
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == BaseCoreActivity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_SELECT_IAMGE_CODE) {
                dialog?.callBack(data)
            }
        }
    }


    companion object {
        fun atartActivity(context:Activity){
            val intent=Intent(context,MeInfoActivity::class.java)
            context.startActivity(intent)
        }
    }

}
