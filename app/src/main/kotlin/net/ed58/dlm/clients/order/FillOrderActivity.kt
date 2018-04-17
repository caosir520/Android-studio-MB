package net.ed58.dlm.clients.order

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import com.wise.common.baseapp.AppManager
import com.wise.common.commonutils.CashierInputFilter
import com.wise.common.commonutils.LogUtil
import com.wise.common.commonutils.ToastUtil
import kotlinx.android.synthetic.main.activity_fill_order.*
import net.ed58.dlm.clients.Insured.InsuredActivity
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.base.BaseCoreMVPActivity
import net.ed58.dlm.clients.entity.AddressInfoBean
import net.ed58.dlm.clients.entity.PreOrderBean
import net.ed58.dlm.clients.entity.ProductTypeListBean
import net.ed58.dlm.clients.global.Constant
import net.ed58.dlm.clients.global.RxKey
import java.math.BigDecimal
//下单界面
class FillOrderActivity : BaseCoreMVPActivity<FillOrderPresent, FillOrderPresent.IFillOrderListener>(), FillOrderPresent.IFillOrderListener, View.OnClickListener {

    private var topAddressInfo: AddressInfoBean? = null
    private var bottomAddressInfo: AddressInfoBean? = null

    private var productCategoryPos = 0
    private var moneyPos = 0
    private var weight = 1
    private var remark =null

    var categoryProductTypeBean: ProductTypeListBean.ProductTypeBean? = null
    var categoryMoneyTypeBean: ProductTypeListBean.ProductTypeBean? = null

    //控制最大输入金额
    private val totalAmount = BigDecimal("99999")
    private val totalAmountStr = "99999"
    private var flag = true//控制最大输入金额
    private var inputPrice = ""// 输入的价格
    private var orderAmount: BigDecimal = BigDecimal("0.00")//总价格(输入的价格+保费)
    private var inputAmount: BigDecimal = BigDecimal("0.00")
    private var insuranceFee: BigDecimal = BigDecimal("0")
    private var supportValueId: String = ""
    private var insuranceSelectedPosition: Int = -1
    private var markContent: String = ""
    private var selectedFormatTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        topAddressInfo = intent.getSerializableExtra("topAddressInfo") as AddressInfoBean?
        bottomAddressInfo = intent.getSerializableExtra("bottomAddressInfo") as AddressInfoBean?

        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        text_receive_address.text = "${bottomAddressInfo?.address} ${bottomAddressInfo?.detailAddress}"
        text_bottom_person_info.text = "${bottomAddressInfo?.name} ${bottomAddressInfo?.phone}"
        text_current_address.text = "${topAddressInfo?.address} ${topAddressInfo?.detailAddress}"
        text_top_person_info.text = "${topAddressInfo?.name} ${topAddressInfo?.phone}"

        layout_time.setOnClickListener(this)
        layout_product.setOnClickListener(this)
        button_submit.setOnClickListener(this)
        layout_bao_price.setOnClickListener(this)
        view_map_top.setOnClickListener(this)
        view_map_bottom.setOnClickListener(this)
        image_back.setOnClickListener(this)
        layout_mark.setOnClickListener(this)

        edit_price.filters = arrayOf<InputFilter>(CashierInputFilter(BigDecimal("99999")))

        edit_price.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                inputPrice = s.toString()
                LogUtil.d(inputPrice)
                if (TextUtils.isEmpty(inputPrice)) {
                    inputAmount = BigDecimal("0.00")
                    orderAmount = inputAmount.add(insuranceFee)
                    text_price.text = "$orderAmount"
                } else {
                    inputAmount = BigDecimal(inputPrice)
                    orderAmount = inputAmount.add(insuranceFee)
                    text_price.text = "$orderAmount"
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
        presenter.getCategory()
    }


    override fun createPresenter(): FillOrderPresent {
        return FillOrderPresent()
    }

    override fun getUi(): FillOrderPresent.IFillOrderListener {
        return this
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_fill_order
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.layout_time -> {
                presenter.showSelectTimeDialog()
            }
            R.id.layout_product -> {
                ProductTypeActivity.startActivity(this, productCategoryPos, moneyPos, weight, categoryProductTypeBean, categoryMoneyTypeBean)
            }
            R.id.button_submit -> {
                if (TextUtils.isEmpty(inputPrice) || "0" == text_price.text.toString().trim()) {
                    ToastUtil.showBottomtoast(this, "请输入您的价格")
                    return
                }
                presenter.submitOrder()
            }
            R.id.layout_bao_price -> {
                val bundle = Bundle()
                bundle.putInt("clickPos", insuranceSelectedPosition)
                startActivityForResult(InsuredActivity::class.java, bundle, Constant.REQUEST_SECOND_CODE)
            }
            R.id.view_map_top -> {
                FillAddressInfoActivity.startActivity(this, topAddressInfo, Constant.REQUEST_THIRD_CODE)
            }
            R.id.view_map_bottom -> {
                FillAddressInfoActivity.startActivity(this, bottomAddressInfo, Constant.REQUEST_FOUR_CODE)
            }
            R.id.image_back -> {
                onBackPressed()
            }
            R.id.layout_mark -> {
                RemarkInformationActivity.startActivityForResult(this,markContent,Constant.REQUEST_FIVE_CODE)
            }
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(mContext)
                .setTitle("是否结束发单？")
                .setMessage("结束发单后订单信息将会清除")
                //相当于点击确认按钮
                .setPositiveButton("继续发单", null)
                .setNegativeButton("结束发单") { dialogInterface, i ->
                    mRxManager.post(RxKey.EVENT_ORDER_FINISH, true)
                    AppManager.getAppManager().finishActivity()
                }.create().show()
    }

    /**
     * 去支付页面
     */
    override fun goPayPage(preOrderBean: PreOrderBean) {
        OrderPaymentActivity.startActivity(this,preOrderBean.orderId,"${preOrderBean.orderPrice}",1)
        AppManager.getAppManager().finishActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUEST_FIRST_CODE) {
                //选择品类
                productCategoryPos = data?.getIntExtra("productCategoryPos", 0)!!
                moneyPos = data.getIntExtra("moneyPos", 0)
                weight = data.getIntExtra("weight", 1)

                text_category_name.text = categoryProductTypeBean?.children?.get(productCategoryPos)?.name!!
                text_category_amount.text = categoryMoneyTypeBean?.children?.get(moneyPos)?.name!!
                text_category_weight.text = "${weight}公斤"
            } else if (requestCode == Constant.REQUEST_SECOND_CODE) {
                //选择保价

                insuranceSelectedPosition = data?.getIntExtra("clickPos", -1)!!
                if (insuranceSelectedPosition != -1) {
                    insuranceFee = data.getSerializableExtra("insuranceFee") as BigDecimal
                    supportValueId = data.getStringExtra("supportValueId")
                    text_baojia.text = "保费${insuranceFee}元"
                } else {
                    supportValueId = ""
                    insuranceFee = BigDecimal("0")
                    text_baojia.text = ""
                }
                orderAmount = inputAmount.add(insuranceFee)
                text_price.text = "$orderAmount"
            } else if (requestCode == Constant.REQUEST_THIRD_CODE) {
                //选择上面的地址
                topAddressInfo = data?.getSerializableExtra("addressInfo") as AddressInfoBean?
                text_current_address.text = "${topAddressInfo?.address} ${topAddressInfo?.detailAddress}"
                text_top_person_info.text = "${topAddressInfo?.name} ${topAddressInfo?.phone}"
            } else if (requestCode == Constant.REQUEST_FOUR_CODE) {
                //选择下面的地址
                bottomAddressInfo = data?.getSerializableExtra("addressInfo") as AddressInfoBean?
                text_receive_address.text = "${bottomAddressInfo?.address} ${bottomAddressInfo?.detailAddress}"
                text_bottom_person_info.text = "${bottomAddressInfo?.name} ${bottomAddressInfo?.phone}"
            }else if (requestCode == Constant.REQUEST_FIVE_CODE) {
                //填写备注
                markContent = data?.getStringExtra("markContent")!!
                text_mark.text = markContent
                if (TextUtils.isEmpty(markContent)) {
                    text_mark.gravity = Gravity.RIGHT
                }else{
                    text_mark.gravity = Gravity.LEFT
                }
            }
        }
    }

    override fun setSelectedTime(selectedShowTime: String, selectedFormatTime: String) {
        tv_insured.text = selectedShowTime
        this.selectedFormatTime = selectedFormatTime
    }


    override fun setProductTypeBean(categoryProductTypeBean: ProductTypeListBean.ProductTypeBean) {
        this.categoryProductTypeBean = categoryProductTypeBean
        text_category_name.text = categoryProductTypeBean.children!![0].name!!
    }

    override fun setMoneyTypeBean(categoryMoneyTypeBean: ProductTypeListBean.ProductTypeBean) {
        this.categoryMoneyTypeBean = categoryMoneyTypeBean
        text_category_amount.text = categoryMoneyTypeBean.children!![0].name!!

    }

    override fun setTimeAndSetVisible(time: String) {
        selectedFormatTime = time
        scrollView.visibility = View.VISIBLE
        layout_bottom.visibility = View.VISIBLE
    }

    override fun getReceiverName(): String {
        return bottomAddressInfo?.name!!
    }

    override fun getReceiverAddress(): String {
        return bottomAddressInfo?.address!!
    }

    override fun getReceiverLat(): Double {
        return bottomAddressInfo?.latitude!!
    }

    override fun getReceiverLng(): Double {
        return bottomAddressInfo?.longtitude!!
    }

    override fun getReceiverPhone(): String {
        return bottomAddressInfo?.phone!!
    }

    override fun getReceiverFloorHouseNum(): String {
        return bottomAddressInfo?.detailAddress!!
    }

    override fun getFetchName(): String {
        return topAddressInfo?.name!!
    }

    override fun getFetchAddress(): String {
        return topAddressInfo?.address!!
    }

    override fun getFetchLat(): Double {
        return topAddressInfo?.latitude!!
    }

    override fun getFetchLng(): Double {
        return topAddressInfo?.longtitude!!
    }

    override fun getFetchPhone(): String {
        return topAddressInfo?.phone!!
    }

    override fun getFetchFloorHouseNum(): String {
        return topAddressInfo?.detailAddress!!
    }

    override fun getOriginatorRemark(): String {
        return markContent
    }


    override fun getExpectedFetchTime(): String {
        return "2017-11-05 15:10"
    }

    override fun getCategoryName(): String {
        return categoryProductTypeBean?.children?.get(productCategoryPos)?.name!!
    }

    override fun getCargoAmount(): String {
        return categoryMoneyTypeBean?.children?.get(moneyPos)?.name!!
    }

    override fun getCargoWeight(): Double {
        return weight.toDouble()
    }

    override fun getSupportValueId(): String {
        return supportValueId
    }


    override fun getReceiverCityCode(): String {
        return bottomAddressInfo?.cityCode!!
    }

    override fun getReceiverAdCode(): String {
        return bottomAddressInfo?.adCode!!
    }

    override fun getFetchCityCode(): String {
        return topAddressInfo?.cityCode!!
    }

    override fun getFetchAdCode(): String {
        return topAddressInfo?.adCode!!
    }

    override fun getOrderAmount(): BigDecimal {
        return orderAmount
    }

    companion object {
        fun startActivity(context: Activity, topAddressInfo: AddressInfoBean?, bottomAddressInfo: AddressInfoBean?) {
            val intent = Intent(context, FillOrderActivity::class.java)
            intent.putExtra("topAddressInfo", topAddressInfo)
            intent.putExtra("bottomAddressInfo", bottomAddressInfo)
            context.startActivity(intent)
        }
    }
}
