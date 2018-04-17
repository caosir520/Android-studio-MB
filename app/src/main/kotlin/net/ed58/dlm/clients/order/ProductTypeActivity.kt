package net.ed58.dlm.clients.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.wise.common.baseapp.AppManager
import kotlinx.android.synthetic.main.activity_product_type.*
import net.ed58.dlm.clients.R
import net.ed58.dlm.clients.adapter.ProductTypeAdapter
import net.ed58.dlm.clients.base.BaseCoreActivity
import net.ed58.dlm.clients.entity.ProductTypeListBean
import net.ed58.dlm.clients.global.Constant

class ProductTypeActivity : BaseCoreActivity(), View.OnClickListener {

    var weight:Int = 1

    var categoryProductTypeBean: ProductTypeListBean.ProductTypeBean? = null
    var categoryMoneyTypeBean: ProductTypeListBean.ProductTypeBean? = null

    private var productTypeAdapter:ProductTypeAdapter? = null
    private var moneyTypeAdapter:ProductTypeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        weight = intent.getIntExtra("weight", 1)
        categoryProductTypeBean = intent.getSerializableExtra("categoryProductTypeBean") as ProductTypeListBean.ProductTypeBean?
        categoryMoneyTypeBean = intent.getSerializableExtra("categoryMoneyTypeBean") as ProductTypeListBean.ProductTypeBean?

        initView()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_product_type
    }

    private fun initView() {
        val productCategoryPos = intent.getIntExtra("productCategoryPos", 0)
        val moneyPos = intent.getIntExtra("moneyPos", 0)

        text_weight.text = "${weight}公斤"

        image_back.setOnClickListener(this)
        image_plus.setOnClickListener(this)
        image_jian.setOnClickListener(this)
        button_submit.setOnClickListener(this)

        text_one_type.text = categoryProductTypeBean?.name
        oneRecyclerview.layoutManager = GridLayoutManager(this@ProductTypeActivity, 3)
        productTypeAdapter = ProductTypeAdapter(this@ProductTypeActivity, categoryProductTypeBean?.children)
        productTypeAdapter!!.selectedPos = productCategoryPos
        oneRecyclerview.adapter = productTypeAdapter

        text_two_type.text = categoryMoneyTypeBean?.name
        twoRecyclerview.layoutManager = GridLayoutManager(this@ProductTypeActivity, 3)
        moneyTypeAdapter = ProductTypeAdapter(this@ProductTypeActivity, categoryMoneyTypeBean?.children)
        moneyTypeAdapter!!.selectedPos = moneyPos
        twoRecyclerview.adapter = moneyTypeAdapter
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_back -> {
                AppManager.getAppManager().finishActivity()
            }
            R.id.image_jian -> {
                if (weight == 1) {
                    return
                }
                text_weight.text = "${--weight}公斤"
            }
            R.id.image_plus -> {
                if (weight == 25) {
                    return
                }
                text_weight.text = "${++weight}公斤"
            }
            R.id.button_submit->{
                val intent = Intent()
                intent.putExtra("productCategoryPos", productTypeAdapter!!.selectedPos)
                intent.putExtra("moneyPos", moneyTypeAdapter!!.selectedPos)
                intent.putExtra("weight", weight)
                setResult(Activity.RESULT_OK, intent)
                AppManager.getAppManager().finishActivity()
            }
        }
    }


    companion object {
        fun startActivity(context: Activity, productCategoryPos: Int, moneyPos: Int, weight: Int, categoryProductTypeBean: ProductTypeListBean.ProductTypeBean?, categoryMoneyTypeBean: ProductTypeListBean.ProductTypeBean?) {
            val intent = Intent(context, ProductTypeActivity::class.java)
            intent.putExtra("productCategoryPos", productCategoryPos)
            intent.putExtra("moneyPos", moneyPos)
            intent.putExtra("weight", weight)
            intent.putExtra("categoryProductTypeBean",categoryProductTypeBean)
            intent.putExtra("categoryMoneyTypeBean",categoryMoneyTypeBean)
            (context as BaseCoreActivity).startActivityForResult(intent,Constant.REQUEST_FIRST_CODE)
        }
    }
}
