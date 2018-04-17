package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import com.wise.common.baseapp.AppManager
import kotlinx.android.synthetic.main.activity_fill_address_info.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.address.MapSelectAddressActivity
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.entity.AddressInfoBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey

class FillAddressInfoActivity : BaseCoreActivity(), View.OnClickListener {

    private var addressInfo: AddressInfoBean? = null
    private var type = -1//1是发，2是收

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_address_info)

        type = intent.getIntExtra("type", -1)
        addressInfo = intent.getSerializableExtra("addressInfo") as AddressInfoBean?
        initView()
    }

    private fun initView() {
        if (type == FillAddressInfoActivity.SEND_TYPE) {
            text_title.text = "完善发货人信息"
            text_address.hint = "发货人地址"
            text_name.hint = "发货人姓名"
            text_phone.hint = "发货人手机号"
        }else if (type == FillAddressInfoActivity.RECEIVE_TYPE) {
            text_title.text = "完善收货人信息"
            text_address.hint = "收货人地址"
            text_name.hint = "收货人姓名"
            text_phone.hint = "收货人手机号"
        }

        text_address.text = addressInfo?.address
        text_name.setText(addressInfo?.name)
        text_phone.setText(addressInfo?.phone)
        text_detail_address.setText((addressInfo?.detailAddress))

        button_confirm.setOnClickListener(this)
        layout_select_address.setOnClickListener(this)
        image_back.setOnClickListener(this)

        //假如完善了信息 按钮可点
        if (addressInfo?.isCompleted!!) {
            button_confirm.isEnabled = true
        }

        setEditTextWatchListener()
        registRx()
    }

    private fun registRx() {
        mRxManager.on<AddressInfoBean>(RxKey.EVENT_SELECT_COMMON_ADDRESS){addressInfo->
            this.addressInfo = addressInfo
            text_address.text = addressInfo?.address
            text_name.setText(addressInfo?.name)
            text_phone.setText(addressInfo?.phone)
            text_detail_address.setText((addressInfo?.detailAddress))
        }
    }

    /**
     * textwatchlistener
     */
    private fun setEditTextWatchListener() {
        text_name.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(editable: Editable?) {
                button_confirm.isEnabled = (!TextUtils.isEmpty(text_phone.text.toString().trim())
                        && !TextUtils.isEmpty(editable.toString())
                        && text_phone.text.toString().length >= 7 && editable.toString().length <= 11)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })
        text_phone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                button_confirm.isEnabled = (!TextUtils.isEmpty(text_name.text.toString().trim())
                        && !TextUtils.isEmpty(editable.toString())
                        && editable.toString().length >= 7 && editable.toString().length <= 11)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_fill_address_info
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.button_confirm -> {
                addressInfo?.apply {
                    this.detailAddress = text_detail_address.text.toString().trim()
                    this.name = text_name.text.toString().trim()
                    this.phone = text_phone.text.toString().trim()
                }
                if (type == SEND_TYPE || type == -1) {
                    val intent = Intent()
                    intent.putExtra("addressInfo", addressInfo)
                    setResult(Activity.RESULT_OK, intent)
                } else if (type == RECEIVE_TYPE) {
                    mRxManager.post(RxKey.EVENT_UPDATE_MAIN_BOTTOM_ADDRESS, addressInfo)
                }
                AppManager.getAppManager().finishActivity()

            }
            R.id.layout_select_address -> {
                MapSelectAddressActivity.startActivityForResult(this, true, type, addressInfo?.latitude, addressInfo?.longtitude)
            }
            R.id.image_back -> {
                AppManager.getAppManager().finishActivity()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_FIRST_CODE) {
                addressInfo = data?.getSerializableExtra("addressInfo") as AddressInfoBean?
                text_address.text = addressInfo?.address
            }
        }
    }


    companion object {
        const val SEND_TYPE: Int = 1//发 的地址信息
        const val RECEIVE_TYPE: Int = 2//收 的地址信息

        fun startActivity(context: Activity, type: Int, addressInfo: AddressInfoBean, requestCode: Int) {
            val intent = Intent(context, FillAddressInfoActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("addressInfo", addressInfo)
            context.startActivityForResult(intent, requestCode)
        }

        fun startActivity(context: Activity, addressInfo: AddressInfoBean?, requestCode: Int) {
            val intent = Intent(context, FillAddressInfoActivity::class.java)
            intent.putExtra("addressInfo", addressInfo)
            context.startActivityForResult(intent, requestCode)
        }

        fun startActivity(context: Activity, type: Int, addressInfo: AddressInfoBean) {
            val intent = Intent(context, FillAddressInfoActivity::class.java)
            intent.putExtra("type", type)
            intent.putExtra("addressInfo", addressInfo)
            context.startActivity(intent)
        }
    }
}
